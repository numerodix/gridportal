/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.globus.gsi;

import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.util.Date;

import org.globus.common.ChainedIOException;
import org.globus.common.CoGProperties;
import org.globus.util.Base64;

import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.bc.BouncyCastleUtil;
import org.globus.gsi.gssapi.SSLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Provides a Java object representation of Globus credential 
 * which can include the proxy file or host certificates.
 */
public class GlobusCredential implements Serializable {

    private static Log logger =
	LogFactory.getLog(GlobusCredential.class.getName());

    private static GlobusCredential defaultCred = null;

    // indicates if default credential was explicitely set
    // and if so - if the credential expired it try
    // to load the proxy from a file.
    private static boolean credentialSet = false;

    /** holds the private key - this key is time limited */
    private PrivateKey key;
    
    /** holds both the certificate chain */
    private X509Certificate [] certs;
    
    /** Creates a GlobusCredential from a private key and 
     * a certificate chain.
     *
     * @param key the private key
     * @param certs the certificate chain
     */
    public GlobusCredential(PrivateKey key, X509Certificate [] certs) {
	this.key = key;
	this.certs = certs;
    }
    
    /**
     * Creates a GlobusCredential from a proxy file.
     *
     * @param proxyFile the file to load the credential from.
     * @exception GlobusCredentialException if the credential failed to 
     *            load.
     */
    public GlobusCredential(String proxyFile) 
	throws GlobusCredentialException {
	if (proxyFile == null) {
	    throw new IllegalArgumentException("proxyFile == null");
	}

	logger.debug("Loading proxy file: " + proxyFile);
	
	try {
	    InputStream in = new FileInputStream(proxyFile);
	    load(in);
	} catch(FileNotFoundException f) {
	    throw new GlobusCredentialException(
				    GlobusCredentialException.IO_ERROR, 
				    "proxyNotFound",
				    new Object[] {proxyFile});
	}
    }

    /**
     * Creates a GlobusCredential from certificate file and a
     * unencrypted key file.
     *
     * @param certFile the file containing the certificate
     * @param unencryptedKeyFile the file containing the private key. The key
     *                           must be unencrypted.
     * @exception GlobusCredentialException if something goes wrong.
     */
    public GlobusCredential(String certFile,
			    String unencryptedKeyFile)
	throws GlobusCredentialException {
	
	if (certFile == null || unencryptedKeyFile == null) {
	    throw new IllegalArgumentException();
	}

	try {
	    this.certs = CertUtil.loadCertificates(certFile);
	    OpenSSLKey k = new BouncyCastleOpenSSLKey(unencryptedKeyFile);
	    if (k.isEncrypted()) {
		throw new GlobusCredentialException(
					 GlobusCredentialException.DEFECTIVE,
					 "encPrivKey",
					 new Object [] {unencryptedKeyFile});
	    }

	    this.key = k.getPrivateKey();
	} catch (IOException e) {
	    throw new GlobusCredentialException(
					  GlobusCredentialException.IO_ERROR, 
					  "ioError00",
					  e);
	} catch (GeneralSecurityException e) {
	    throw new GlobusCredentialException(
					GlobusCredentialException.SEC_ERROR, 
					"secError00",
					e);
	} catch (Exception e) {
	    throw new GlobusCredentialException(
					GlobusCredentialException.FAILURE,
					"error00",
					e);
	}
    }
    
    /**
     * Creates a GlobusCredential from an input stream.
     *
     * @param input the stream to load the credential from.
     * @exception GlobusCredentialException if the credential failed to 
     *            load.
     */
    public GlobusCredential(InputStream input) 
	throws GlobusCredentialException {
	load(input);
    }

    protected void load(InputStream input) 
	throws GlobusCredentialException {

	if (input == null) {
	    throw new IllegalArgumentException("input stream cannot be null");
	}

	PrivateKey key = null;
	X509Certificate cert = null;
	Vector chain = new Vector(3);

	String line;
	BufferedReader reader = null;

	try {
	    reader = new BufferedReader(new InputStreamReader(input));

	    while( (line = reader.readLine()) != null  ) {
   
		if (line.indexOf("BEGIN CERTIFICATE") != -1) {
		    byte [] data = getDecodedPEMObject(reader);
		    cert = CertUtil.loadCertificate(new ByteArrayInputStream(data));
		    chain.addElement(cert);
		} else if (line.indexOf("BEGIN RSA PRIVATE KEY") != -1) {
		    byte [] data = getDecodedPEMObject(reader);
		    OpenSSLKey k = new BouncyCastleOpenSSLKey("RSA", data);
		    key = k.getPrivateKey();
		}
	    }
	    
	} catch (IOException e) {
	    throw new GlobusCredentialException(
					  GlobusCredentialException.IO_ERROR, 
					  "ioError00",
					  e);
	} catch (GeneralSecurityException e) {
	    throw new GlobusCredentialException(
					GlobusCredentialException.SEC_ERROR, 
					"secError00",
					e);
	} catch (Exception e) {
	    throw new GlobusCredentialException(
					GlobusCredentialException.FAILURE,
					"error00",
					e);
	} finally {
	    if (reader != null) {
		try { reader.close(); } catch(IOException e) {}
	    }
	}

	int size = chain.size();

	if (size == 0) {
	    throw new GlobusCredentialException(
					  GlobusCredentialException.SEC_ERROR, 
					  "noCerts00",
					  (Exception)null);
	}

	if (key == null) {
	     throw new GlobusCredentialException(
					  GlobusCredentialException.SEC_ERROR, 
					  "noKey00",
					  (Exception)null);
	}

	// set chain
	this.certs = new X509Certificate[size];
	chain.copyInto(certs);

	// set key
	this.key = key;
    }

    /**
     * Reads Base64 encoded data from the stream and returns
     * its decoded value. The reading continues until the "END"
     * string is found in the data. Otherwise, returns null.
     */
    private static final byte[] getDecodedPEMObject(BufferedReader reader) 
	throws IOException {
	String line;
	StringBuffer buf = new StringBuffer();
	while( (line = reader.readLine()) != null  ) {
	    if (line.indexOf("--END") != -1) { // found end
		return Base64.decode(buf.toString().getBytes());
	    } else {
		buf.append(line);
	    }
	}
	throw new EOFException("PEM footer missing");
    }

    /**
     * Saves the credential into a specified output stream.
     * The self-signed certificates in the certificate chain will not be saved.
     * The output stream should always be closed after calling this function.
     *
     * @param out the output stream to write the credential to.
     * @exception IOException if any error occurred during saving.
     */
    public void save(OutputStream out) 
	throws IOException {
	
	try {
	    CertUtil.writeCertificate(out, this.certs[0]);
	    
	    OpenSSLKey k = new BouncyCastleOpenSSLKey(key);
	    k.writeTo(out);
	    
	    for (int i=1;i<this.certs.length;i++) {
		// this will skip the self-signed certificates
		if (this.certs[i].getSubjectDN().equals(certs[i].getIssuerDN())) continue;
		CertUtil.writeCertificate(out, this.certs[i]);
	    }
	} catch (CertificateEncodingException e) {
            throw new ChainedIOException(e.getMessage(), e);
	}
	
	out.flush();
    }

    /**
     * Verifies the validity of the credential. Each certificate in the
     * chain is check for expiration date.
     *
     * @exception GlobusCredentialException if one of the certificates in
     *            the chain expired or some other error.
     */
    public void verify() 
	throws GlobusCredentialException {
	// TODO: check chain and verify again CA cert
	
	// for now just check vadility
	for (int i=0;i<this.certs.length;i++) {
	    try {
		this.certs[i].checkValidity();
	    } catch (CertificateExpiredException e) {
		String dn = this.certs[i].getSubjectDN().getName();
		throw new GlobusCredentialException(
					  GlobusCredentialException.EXPIRED,
					  "expired00",
					  new Object[] {dn});
	    } catch (GeneralSecurityException e) {
		throw new GlobusCredentialException(
					  GlobusCredentialException.SEC_ERROR, 
					  "secError00",
					  e);
	    }
	}
    }

    /**
     * Returns the identity certificate of this credential. The identity
     * certificate is the first certificate in the chain that is not 
     * an impersonation proxy certificate.
     *
     * @return <code>X509Certificate</code> the identity cert. Null,
     *         if unable to get the identity certificate (an error
     *         occurred)
     */
    public X509Certificate getIdentityCertificate() {
	try {
	    return BouncyCastleUtil.getIdentityCertificate(this.certs);
	} catch (CertificateException e) {
            logger.debug("Error getting certificate identity", e);
	    return null;
	}
    }

    /**
     * Returns the identity of this credential. 
     * @see #getIdentityCertificate()
     *
     * @return The identity cert in Globus format (e.g. /C=US/..). Null,
     *         if unable to get the identity (an error occurred)
     */
    public String getIdentity() {
	try {
	    return BouncyCastleUtil.getIdentity(this.certs);
	} catch (CertificateException e) {
            logger.debug("Error getting certificate identity", e);
	    return null;
	}
    }

    /**
     * Returns the private key of this credential.
     *
     * @return <code>PrivateKey</code> the private key
     */
    public PrivateKey getPrivateKey() {
	return key;
    }

    /**
     * Returns the certificate chain of this credential.
     *
     * @return <code>X509Certificate []</code> the certificate chain
     */
    public X509Certificate [] getCertificateChain() {
	return this.certs;
    }

    /**
     * Returns the number of certificates in the credential without the
     * self-signed certificates.
     *
     * @return number of certificates without counting self-signed certificates
     */
    public int getCertNum() {
	for (int i=this.certs.length-1;i>=0;i--) {
	    if (!this.certs[i].getSubjectDN().equals(this.certs[i].getIssuerDN())) {
		return i+1;
	    }
	}
	return this.certs.length;
    }
    
    /**
     * Returns strength of the private/public key in bits.
     *
     * @return strength of the key in bits. Returns -1
     *         if unable to determine it.
     */
    public int getStrength() {
	if (key == null) return -1;
	return ((RSAPrivateKey)key).getModulus().bitLength();
    }
  
    /**
     * Returns the subject DN of the first certificate in the chain.
     *
     * @return subject DN. 
     */
    public String getSubject() {
	return this.certs[0].getSubjectDN().getName();
    }

    /**
     * Returns the issuer DN of the first certificate in the chain.
     *
     * @return issuer DN. 
     */
    public String getIssuer() {
	return this.certs[0].getIssuerDN().getName();
    }
  
    /**
     * Returns the certificate type of the first certificate in
     * the chain. Returns -1 if unable to determine the certificate
     * type (an error occurred)
     * @see BouncyCastleUtil#getCertificateType(X509Certificate)
     *
     * @return the type of first certificate in the chain. -1 if unable
     *         to determine the certificate type.
     */
    public int getProxyType() {
	try {
	    return BouncyCastleUtil.getCertificateType(this.certs[0]);
	} catch (CertificateException e) {
            logger.debug("Error getting certificate type", e);
	    return -1;
	}
    }

    /**
     * Returns time left of this credential. The time left of the credential
     * is based on the certificate with the shortest validity time.
     *
     * @return time left in seconds. Returns 0 if the 
     *         certificate has expired.
     */
    public long getTimeLeft() {
	Date earliestTime = null;
	for (int i=0;i<this.certs.length;i++) {
	    Date time = this.certs[i].getNotAfter();
	    if (earliestTime == null ||
		time.before(earliestTime)) {
		earliestTime = time;
	    }
	}
	long diff = (earliestTime.getTime() - System.currentTimeMillis())/1000;
	return (diff < 0) ? 0 : diff;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        byte [] encoded;

        // write key
        OpenSSLKey encodedKey = new BouncyCastleOpenSSLKey(this.key);
        encoded = encodedKey.getEncoded();
        oos.writeInt(encoded.length);
        oos.write(encoded);

        // write certs
        oos.writeInt(this.certs.length);
        try {
            for (int i=0;i<this.certs.length;i++) {
                encoded = this.certs[i].getEncoded();
                oos.writeInt(encoded.length);
                oos.write(encoded);
            }
        } catch (Exception e) {
            throw new ChainedIOException("", e);
        }
    }

    private static byte[] readData(ObjectInputStream ois)
        throws IOException {
        int len = ois.readInt();
        byte [] encoded = new byte[len];
        SSLUtil.readFully(ois, encoded, 0, len);
        return encoded;
    }

    private void readObject(ObjectInputStream ois) 
        throws IOException, ClassNotFoundException {
        
        // read key
        try {
            byte [] encoded = readData(ois);
            OpenSSLKey encodedKey = new BouncyCastleOpenSSLKey("RSA", encoded);
            this.key = encodedKey.getPrivateKey();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ChainedIOException("", e);
        }

        // read certs
        int certs = ois.readInt();
        this.certs = new X509Certificate[certs];
        try {
            for (int i=0;i<certs;i++) {
                InputStream in = new ByteArrayInputStream(readData(ois));
                this.certs[i] = CertUtil.loadCertificate(in);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ChainedIOException("", e);
        }
    }

    /**
     * Returns the default credential. The default credential is usually
     * the user proxy certificate. <BR>
     * The credential will be loaded on the initial call. It must not
     * be expired. All subsequent calls to this function return 
     * cached credential object. Once the credential is cached, and
     * becomes expired, credential file will be reloaded.
     *
     * @return the default credential.
     * @exception GlobusCredentialException if the credential expired or 
     *            some other error with the credential.
     */
    public synchronized static GlobusCredential getDefaultCredential() 
	throws GlobusCredentialException {
	if (defaultCred == null ||
	    (!credentialSet && defaultCred.getTimeLeft() <= 0)) {
	    String proxyLocation = CoGProperties.getDefault().getProxyFile();
	    defaultCred = new GlobusCredential(proxyLocation);
	    // TODO: do we want to verify each time?
	    defaultCred.verify();
	}
	return defaultCred;
    }

    /**
     * Sets default credential.
     *
     * @param cred the credential to set a default.
     */
    public synchronized static void setDefaultCredential(GlobusCredential cred) {
	defaultCred = cred;
	credentialSet = (cred != null);
    }

    public String toString() {
	String lineSep = System.getProperty("line.separator");
	StringBuffer buf = new StringBuffer();
	buf.append("subject    : ").append(getSubject()).append(lineSep);
	buf.append("issuer     : ").append(getIssuer()).append(lineSep);
	buf.append("strength   : ").append(getStrength() + " bits").append(lineSep);
	buf.append("timeleft   : ").append(getTimeLeft() + " sec").append(lineSep);
	buf.append("proxy type : ").append(CertUtil.getProxyTypeAsString(getProxyType()));
	return buf.toString();
    }
    
}
