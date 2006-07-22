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

import java.security.Security;
import java.security.Provider;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import java.security.Principal;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.globus.util.Base64;
import org.globus.util.PEMUtils;
import org.globus.common.CoGProperties;
import org.globus.gsi.bc.X509NameHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x509.X509Name;

/** 
 * Contains various security-related utility methods.
 */
public class CertUtil {
  
    private static Log logger =
        LogFactory.getLog(CertUtil.class.getName());
    
    static {
        Security.addProvider(new BouncyCastleProvider());
        setProvider("BC");
        installSecureRandomProvider();
    }

    private static String provider;

    /**
     * A no-op function that can be used to force the class
     * to load and initialize.
     */
    public static void init() {
    }

    /**
     * Sets a provider name to use for loading certificates
     * and for generating key pairs.
     *
     * @param providerName provider name to use.
     */
    public static void setProvider(String providerName) {
        provider = providerName;
        if (logger.isDebugEnabled()) {
            logger.debug("Provider set to : " + providerName);
        }
    }

    /**
     * Returns appropriate <code>CertificateFactory</code>.
     * If <I>provider</I> was set a provider-specific 
     * <code>CertificateFactory</code> will be used. Otherwise, 
     * a default <code>CertificateFactory</code> will be used.
     *
     * @return <code>CertificateFactory</code> 
     */
    protected static CertificateFactory getCertificateFactory() 
        throws GeneralSecurityException {
        if (provider == null) {
            return CertificateFactory.getInstance("X.509");
        } else {
            return CertificateFactory.getInstance("X.509", provider);
        }
    }

    /**
     * Loads a X509 certificate from the specified input stream.
     * Input stream must contain DER-encoded certificate.
     *
     * @param in the input stream to read the certificate from.
     * @return <code>X509Certificate</code> the loaded certificate.
     * @exception GeneralSecurityException if certificate failed to load.
     */
    public static X509Certificate loadCertificate(InputStream in)
        throws GeneralSecurityException {
        return (X509Certificate)getCertificateFactory().generateCertificate(in);
    }

    /** 
     * Loads an X509 certificate from the specified file.  It
     * reads only what is in between the lines containing "BEGIN CERTIFICATE"
     * and "END".
     *
     * @param file the certificate file to load the certificate from.
     * @return <code>java.security.cert.X509Certificate</code> 
     *         the loaded certificate.
     * @exception IOException if I/O error occurs
     * @exception GeneralSecurityException if security problems occurs.
     */
    public static X509Certificate loadCertificate(String file) 
        throws IOException, GeneralSecurityException {

        if (file == null) {
            throw new IllegalArgumentException("Certificate file is null");
        }

        X509Certificate cert = null;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            cert = readCertificate(reader);
        } finally {
            reader.close();
        }
        
        if (cert == null) {
            throw new GeneralSecurityException("Certificate data not found.");
        }
        
        return cert;
    }

    /** 
     * Loads multiple X509 certificates from the specified file.  It
     * reads only what is in between the lines containing "BEGIN CERTIFICATE"
     * and "END".
     *
     * @param file the certificate file to load the certificate from.
     * @return <code>java.security.cert.X509Certificate</code> 
     *         the loaded certificate.
     * @exception IOException if I/O error occurs
     * @exception GeneralSecurityException if security problems occurs.
     */
    public static X509Certificate[] loadCertificates(String file) 
        throws IOException, GeneralSecurityException {

        if (file == null) {
            throw new IllegalArgumentException("Certificate file is null");
        }

        ArrayList list = new ArrayList();
        X509Certificate cert = null;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            while ( (cert = readCertificate(reader)) != null ) {
                list.add(cert);
            }
        } finally {
            reader.close();
        }
        
        if (list.isEmpty()) {
            throw new GeneralSecurityException("Certificate data not found.");
        }

        int size = list.size();
        return (X509Certificate[])list.toArray(new X509Certificate[size]);
    }

    private static X509Certificate readCertificate(BufferedReader reader)
        throws IOException, GeneralSecurityException {
        String line = null;
        StringBuffer buff = new StringBuffer();
        boolean isCert = false;
        while ((line = reader.readLine()) != null) {
            if (line.indexOf("BEGIN CERTIFICATE") != -1) {
                isCert = true;
            } else if (isCert && line.indexOf("END CERTIFICATE") != -1) {
                byte [] data = Base64.decode(buff.toString().getBytes());
                return loadCertificate(new ByteArrayInputStream(data));
            } else if (isCert) {
                buff.append(line);
            }
        }
        return null;
    }

    /**
     * Writes certificate to the specified output stream in PEM format.
     */
    public static void writeCertificate(OutputStream out, 
                                        X509Certificate cert) 
        throws IOException, CertificateEncodingException {
        PEMUtils.writeBase64(out, 
                             "-----BEGIN CERTIFICATE-----",
                             Base64.encode(cert.getEncoded()),
                             "-----END CERTIFICATE-----");
    }

    /**
     * Converts DN of the form "CN=A, OU=B, O=C" into Globus 
     * format "/CN=A/OU=B/O=C".<BR>
     * This function might return incorrect Globus-formatted ID when one of
     * the RDNs in the DN contains commas.
     * @see #toGlobusID(String, boolean)
     *
     * @param dn the DN to convert to Globus format.
     * @return the converted DN in Globus format.
     */
    public static String toGlobusID(String dn) {
        return toGlobusID(dn, true);
    }

    /**
     * Converts DN of the form "CN=A, OU=B, O=C" into Globus 
     * format "/CN=A/OU=B/O=C" or "/O=C/OU=B/CN=A" depending on the
     * <code>noreverse</code> option. If <code>noreverse</code> is true
     * the order of the DN components is not reveresed - "/CN=A/OU=B/O=C" is
     * returned. If <code>noreverse</code> is false, the order of the 
     * DN components is reversed - "/O=C/OU=B/CN=A" is returned. <BR>
     * This function might return incorrect Globus-formatted ID when one of
     * the RDNs in the DN contains commas.
     *
     * @param dn the DN to convert to Globus format.
     * @param noreverse the direction of the conversion.
     * @return the converted DN in Globus format.
     */
    public static String toGlobusID(String dn, boolean noreverse) {
        if (dn == null) {
            return null;
        }

        StringTokenizer tokens = new StringTokenizer(dn, ",");
        StringBuffer buf = new StringBuffer();
        String token;
        
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken().trim();

            if (noreverse) {
                buf.append("/");
                buf.append(token);
            } else {
                buf.insert(0, token);
                buf.insert(0, "/");
            }
        }
        
        return buf.toString();
    }

    /**
     * Converts the specified principal into Globus format.
     * If the principal is of unrecognized type a simple string-based
     * conversion is made using the {@link #toGlobusID(String) toGlobusID()}
     * function.
     *
     * @see #toGlobusID(String)
     *
     * @param name the principal to convert to Globus format.
     * @return the converted DN in Globus format.
     */
    public static String toGlobusID(Principal name) {
        if (name instanceof X509Name) {
            return X509NameHelper.toString((X509Name)name);
        } else {
            return CertUtil.toGlobusID(name.getName());
        }
    }

    /**
     * Installs SecureRandom provider. 
     * This function is automatically called when this class is loaded.
     */
    public static void installSecureRandomProvider() {
        CoGProperties props = CoGProperties.getDefault();
        String providerName = props.getSecureRandomProvider();
        try {
            logger.debug("Loading SecureRandom provider: " + providerName);
            Class providerClass = Class.forName(providerName);
            Security.insertProviderAt( (Provider)providerClass.newInstance(), 1 );
        } catch (Exception e) {
            logger.debug("Unable to install PRNG. Using default PRNG.", e);
        }
    }

    /**
     * Generates a key pair of given algorithm and strength.
     *
     * @param algorithm the algorithm of the key pair.
     * @param bits the strength
     * @return <code>KeyPair</code> the generated key pair.
     * @exception GeneralSecurityException if something goes wrong.
     */
    public static KeyPair generateKeyPair(String algorithm, int bits) 
        throws GeneralSecurityException {
        KeyPairGenerator generator = null;
        if (provider == null) {
            generator = KeyPairGenerator.getInstance(algorithm);
        } else {
            generator = KeyPairGenerator.getInstance(algorithm, provider);
        }
        generator.initialize(bits);
        return generator.generateKeyPair();
    }


    // proxy utilies 

    /**
     * Determines if a specified certificate type indicates a GSI-2 or
     * GSI-3 proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-2 or GSI-3 proxy, false 
     *         otherwise.
     */
    public static boolean isProxy(int certType) {
        return (isGsi2Proxy(certType) ||
                isGsi3Proxy(certType) ||
                isGsi4Proxy(certType));
    }
    
    /**
     * Determines if a specified certificate type indicates a 
     * GSI-4 proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-3 proxy, false 
     *         otherwise.
     */
    public static boolean isGsi4Proxy(int certType) {
        return (certType == GSIConstants.GSI_4_IMPERSONATION_PROXY ||
                certType == GSIConstants.GSI_4_INDEPENDENT_PROXY ||
                certType == GSIConstants.GSI_4_RESTRICTED_PROXY ||
                certType == GSIConstants.GSI_4_LIMITED_PROXY);
    }

    /**
     * Determines if a specified certificate type indicates a 
     * GSI-3 proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-3 proxy, false 
     *         otherwise.
     */
    public static boolean isGsi3Proxy(int certType) {
        return (certType == GSIConstants.GSI_3_IMPERSONATION_PROXY ||
                certType == GSIConstants.GSI_3_INDEPENDENT_PROXY ||
                certType == GSIConstants.GSI_3_RESTRICTED_PROXY ||
                certType == GSIConstants.GSI_3_LIMITED_PROXY);
    }

    /**
     * Determines if a specified certificate type indicates a 
     * GSI-2 proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-2 proxy, false 
     *         otherwise.
     */
    public static boolean isGsi2Proxy(int certType) {
        return (certType == GSIConstants.GSI_2_PROXY ||
                certType == GSIConstants.GSI_2_LIMITED_PROXY);
    }

    /**
     * Determines if a specified certificate type indicates a 
     * GSI-2 or GSI-3 limited proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-2 or GSI-3 limited proxy, 
     *         false otherwise.
     */
    public static boolean isLimitedProxy(int certType) {
        return (certType == GSIConstants.GSI_3_LIMITED_PROXY ||
                certType == GSIConstants.GSI_2_LIMITED_PROXY ||
                certType == GSIConstants.GSI_4_LIMITED_PROXY);
    }

    /**
     * Determines if a specified certificate type indicates a 
     *  GSI-3 or GS-4 limited proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-3 or GSI-4 independent proxy, 
     *         false otherwise.
     */
    public static boolean isIndependentProxy(int certType) {
        return (certType == GSIConstants.GSI_3_INDEPENDENT_PROXY ||
                certType == GSIConstants.GSI_4_INDEPENDENT_PROXY);
    }

    /**
     * Determines if a specified certificate type indicates a 
     * GSI-2 or GSI-3 impersonation proxy certificate.
     *
     * @param certType the certificate type to check.
     * @return true if certType is a GSI-2 or GSI-3 impersonation proxy, 
     *         false otherwise.
     */
    public static boolean isImpersonationProxy(int certType) {
        return (certType == GSIConstants.GSI_3_IMPERSONATION_PROXY ||
                certType == GSIConstants.GSI_3_LIMITED_PROXY ||
                certType == GSIConstants.GSI_4_IMPERSONATION_PROXY ||
                certType == GSIConstants.GSI_4_LIMITED_PROXY ||
                certType == GSIConstants.GSI_2_LIMITED_PROXY ||
                certType == GSIConstants.GSI_2_PROXY);
    }
    
    /**
     * Returns a string description of a specified proxy
     * type.
     *
     * @param proxyType the proxy type to get the string 
     *        description of.
     * @return the string description of the proxy type.
     */
    public static String getProxyTypeAsString(int proxyType) {
        switch(proxyType) {
        case GSIConstants.GSI_4_IMPERSONATION_PROXY:
            return "RFC 3820 compliant impersonation proxy";
        case GSIConstants.GSI_4_INDEPENDENT_PROXY:
            return "RFC 3820 compliant independent proxy";
        case GSIConstants.GSI_4_LIMITED_PROXY:
            return "RFC 3820 compliant limited proxy";
        case GSIConstants.GSI_4_RESTRICTED_PROXY:
            return "RFC 3820 compliant restricted proxy";
        case GSIConstants.GSI_3_IMPERSONATION_PROXY:
            return "Proxy draft compliant impersonation proxy";
        case GSIConstants.GSI_3_INDEPENDENT_PROXY:
            return "Proxy draft compliant independent proxy";
        case GSIConstants.GSI_3_LIMITED_PROXY:
            return "Proxy draft compliant limited proxy";
        case GSIConstants.GSI_3_RESTRICTED_PROXY:
            return "Proxy draft compliant restricted proxy";
        case GSIConstants.GSI_2_PROXY:
            return "full legacy globus proxy";
        case GSIConstants.GSI_2_LIMITED_PROXY:
            return "limited legacy globus proxy";
        default:
            return "not a proxy";
        }
    }

    /**
     * Checks if GSI-3 mode is enabled.
     *
     * @return true if <I>"org.globus.gsi.version"</I> system property
     *         is set to "3". Otherwise, false.
     */
    public static boolean isGsi3Enabled() {
        String ver = System.getProperty("org.globus.gsi.version");
        return (ver != null && ver.equals("3"));
    }

    // CRL Utilities FIXME - move to separate class ?
    public static X509CRL loadCrl(String file) 
        throws IOException, GeneralSecurityException {

        if (file == null) {
            throw new IllegalArgumentException("CRL file is null");
        }

        boolean isCrl = false;
        X509CRL crl = null;

        BufferedReader reader = null;
         
        String line       = null;
        StringBuffer buff = new StringBuffer();
           
        reader = new BufferedReader(new FileReader(file));
        
        try {
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("BEGIN X509 CRL") != -1) {
                    isCrl = true;
                } else if (isCrl && line.indexOf("END X509 CRL") != -1) {
                    byte [] data = Base64.decode(buff.toString().getBytes());
                    crl = loadCrl(new ByteArrayInputStream(data));
                } else if (isCrl) {
                    buff.append(line);
                }
            }
        } finally {
            reader.close();
        }
        
        if (crl == null) {
            throw new GeneralSecurityException("CRL data not found.");
        }
        
        return crl;
    }

    public static X509CRL loadCrl(InputStream in)
        throws GeneralSecurityException {
        return (X509CRL)getCertificateFactory().generateCRL(in);
    }

}
