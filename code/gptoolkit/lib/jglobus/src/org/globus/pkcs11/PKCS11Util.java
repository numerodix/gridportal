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
package org.globus.pkcs11;

import org.globus.security.Config;

import com.ibm.pkcs11.PKCS11Object;
import com.ibm.pkcs11.PKCS11Session;
import com.ibm.pkcs11.PKCS11Slot;
import com.ibm.pkcs11.PKCS11;
import com.ibm.pkcs11.PKCS11Mechanism;
import com.ibm.pkcs11.nat.NativePKCS11;

import iaik.x509.*;
import iaik.security.rsa.*;
import iaik.asn1.*;
import iaik.asn1.structures.*;

import java.security.Principal;
import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*****************************************************************************
 * Contains various utility methods for dealing with PKCS 11 compliant 
 * devices.  This
 * includes performing the following operations:
 * <UL>
 * <LI>Session management</LI>
 * <LI>Retrieving certificates from the device</LI>
 * <LI>Retrieving private keys from the device</LI>
 * <LI>Loading certificates onto the device</LI>
 * <LI>Loading private keys onto the device</LI>
 * </UL>
 *
 * This package depends on libraries from IBM alphaworks that we are not 
 * currently distributing with the Java CoG Kit.  These libraries are 
 * can be downloaded from
 * <a href="http://www.alphaworks.ibm.com/formula/PKCS11APIforJava">IBM AlphaWorks</a>
 *
 * This class assumes ONE session, and would be hopelessly confused if you
 * tried to use it with more than ONE.
 ****************************************************************************/
public class PKCS11Util {
    
    private static Log logger =
	LogFactory.getLog(PKCS11Util.class.getName());

    private static PKCS11Session session = null;
    private static PKCS11Slot token = null;

    // need to define the rest of these as well (or at least the common ones)
    // but this will work for now

    /**
     * Initiates a session with the PKCS11 device with the given flags, and 
     * logs in as a regular user with the given pin.  
     *
     * Also, if there happens to be more than one PKCS11 device attached, 
     * this function will simply grab the first one it encounters.
     **/
    public static void initiateSession(String pin, int flags) 
	throws PKCS11Exception {
	if (token == null) {
	    String libName = Config.getPKCS11LibraryName();
	    logger.debug("Loading PKCS11 native library: " + libName);
	    PKCS11 pkcs11 = new NativePKCS11(libName);
	    
	    PKCS11Slot[] tokenSlots = pkcs11.getSlotList(true);
	    if (tokenSlots.length == 0) {
		throw new PKCS11Exception("No PKCS11 device present");
	    } else {
		logger.debug("Located " + tokenSlots.length + " token(s)");
		logger.debug("Using first located token");
		token = tokenSlots[0];
	    }
	}
	 
	if (session != null) closeSession();

	// open a session 
	logger.debug("Opening a session with the PKCS11 device...");
	session = token.openSession(flags, null, null);
	logger.debug("Logging into the PKCS11 device...");
	session.login(false, pin);
    }

    /**
     * Retrieves a handle to a user certificate with the given label that 
     * is on the PKCS11 device.  This
     * handle can be used to instantiate an X509Certificate object.
     **/
    public static PKCS11Object retrieveUserCertHandle(String certLabel) {
	logger.debug("Retrieving a user certificate handle with label: " 
		     + certLabel);
	PKCS11Object foundUserCert =
	    retrieveObjectByLabel(PKCS11Object.CERTIFICATE, certLabel);
	if (foundUserCert == null) {
	    logger.debug("No user certificate with label " + certLabel + 
			 " found");
	} else {
	    logger.debug("Retrieved a user certificate handle with label: " 
			 + certLabel);	    
	}
	return foundUserCert;
    }
    
    /**
     * Retrieves a handle to a private key with the given label.  The private 
     * key never actually leaves the ibutton device, but this handle can be 
     * used later to initialize a signing operation.
     **/
    public static PKCS11Object retrievePrivateKeyHandle(String keyLabel) {
	logger.debug("Retrieving a default private key handle");
	PKCS11Object userPrivateKeyHandle = 
	    retrieveObjectByLabel(PKCS11Object.PRIVATE_KEY, keyLabel);
	if (userPrivateKeyHandle == null) {
	    logger.debug("No private key with label " + keyLabel +
			 " found");
	} else {
	    logger.debug("Retrieved a default private key handle");
	}
	return userPrivateKeyHandle;
    }

    public static PKCS11Object retrieveObjectByLabel(Integer classType, String label) {
	if (session == null) {
	    logger.error("Session is null");
	    return null;
	}
	int[] searchAttrTypes = {PKCS11Object.CLASS, PKCS11Object.LABEL};
	Object[] searchAttrValues = {classType, label}; 
	session.findObjectsInit(searchAttrTypes, searchAttrValues);
	PKCS11Object handle = session.findObject();
	// this should always be called.
	session.findObjectsFinal(); 
	return handle;
    }
    
    /**
     * Initializes the PKCS11 device for signing.  Called by signCertificate.
     **/
    private static void signInit(PKCS11Object userPrivKeyHandle) {
	logger.debug("Initializing PKCS11 device for signing");
	session.signInit(PKCS11Mechanism.RSA_PKCS,
			 null,
			 userPrivKeyHandle);
    }
    
    /**
     * Creates a signed certificate from the passed in certificate.  Signs the 
     * certificate with the specified private key
     *
     **/
    public static X509Certificate signCertificate(X509Certificate cert, 
						  PKCS11Object userPrivKeyHandle) 
	throws PKCS11Exception {
	try {
	    signInit(userPrivKeyHandle);
	    logger.debug("Signing a certificate");
	    cert.setSignatureAlgorithm(AlgorithmID.md5WithRSAEncryption);
	    byte[] certBytes = cert.getTBSCertificate();
	    
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    md5.update(certBytes);
	    byte[] hashBytes = md5.digest();
	    
	    byte[] signature = new byte[300];
	    int sigLength;
	    byte[] revSignature;
	    
	    ASN1Object digestInfo = new SEQUENCE();
	    digestInfo.addComponent(AlgorithmID.md5.toASN1Object());
	    digestInfo.addComponent(new OCTET_STRING(hashBytes));
	    byte[] toBeEncrypted = DerCoder.encode(digestInfo);
	    
	    sigLength = session.sign(toBeEncrypted, 0, 
				     toBeEncrypted.length,
				     signature, 0);
	    revSignature = new byte[sigLength];
	    System.arraycopy(signature, 0, revSignature, 0, sigLength);
	    
	    ASN1Object tbsCert = DerCoder.decode(certBytes);
	    
	    SEQUENCE  certASN1 = new SEQUENCE();
	    certASN1.addComponent(tbsCert);
	    certASN1.addComponent(AlgorithmID.md5WithRSAEncryption.toASN1Object());
	    certASN1.addComponent(new BIT_STRING(revSignature));
	    
	    return new X509Certificate(DerCoder.encode(certASN1));
	} catch(Exception e) {
	    throw new PKCS11Exception("Failed to sign proxy certificate", e);
	}
    }

    /**
     * Loads a certificate onto the PKCS11 device with a default label
     */
    public static PKCS11Object instantiateUserCert(X509Certificate userCert, 
						   byte[] id) 
	throws CertificateEncodingException {
	return instantiateUserCert(userCert, null, id);
    }
    
    /**
     * Loads a certificate onto the PKCS11 device and labels it with the specified
     * label
     */
    public static PKCS11Object instantiateUserCert(X509Certificate userCert,
						   String label, 
						   byte[] id) 
	throws CertificateEncodingException {
      
	Name issuer = (Name)userCert.getIssuerDN();
	Name subject = (Name)userCert.getSubjectDN();
		
	byte[] issuerBytes = issuer.getEncoded();
	byte[] subjectBytes = subject.getEncoded();
	
	if (label == null) {
	    label = subject.toString();
	}
	
	logger.debug("Instantiating user cert with label " + label + " on device");
	//X_509 CERTIFICATE
	int[] certAttributes = {PKCS11Object.CLASS,
				PKCS11Object.TOKEN,
				PKCS11Object.LABEL,
				PKCS11Object.CERTIFICATE_TYPE,
				PKCS11Object.ID,
				PKCS11Object.SUBJECT,
				PKCS11Object.ISSUER,
				PKCS11Object.SERIAL_NUMBER,
				PKCS11Object.VALUE};
	
	Object[] certAttrValues = {PKCS11Object.CERTIFICATE,
				   PKCS11Object.TRUE,
				   label,
				   PKCS11Object.X_509,
				   id,
				   subjectBytes,
				   issuerBytes,
				   userCert.getSerialNumber().toByteArray(),
				   userCert.getEncoded()};
       	
	return session.createObject(certAttributes,
				    certAttrValues);
    }
    
    /**
     * Loads the specified private key onto the PKCS11 device.  The subject param
     * should be the subject of the certificate this private key is associated with.
     */
    public static PKCS11Object instantiateUserPrivateKey(RSAPrivateKey userPrivateKey,
							 Principal subject, 
							 byte[] id) {
	
	logger.debug("Instantiating private key on device");
	//RSA_PRIVATE_KEY
	int[] privkeyAttributes = {PKCS11Object.CLASS,
				   PKCS11Object.TOKEN,
				   PKCS11Object.KEY_TYPE,
				   PKCS11Object.SENSITIVE,
				   PKCS11Object.SUBJECT,
				   PKCS11Object.ID,
				   PKCS11Object.MODULUS,
				   PKCS11Object.PRIVATE_EXPONENT,
				   PKCS11Object.PRIME_1,
				   PKCS11Object.PRIME_2};
	
	Object[] privkeyAttrValues = {PKCS11Object.PRIVATE_KEY,
				      PKCS11Object.TRUE,
				      PKCS11Object.RSA,
				      PKCS11Object.TRUE,
				      ((Name)subject).getEncoded(),
				      id,
				      userPrivateKey.getModulus(),
				      userPrivateKey.getPrivateExponent(),
				      userPrivateKey.getPrimeP(),
				      userPrivateKey.getPrimeQ()};
	return session.createObject(privkeyAttributes,
				    privkeyAttrValues);
    }

    public static void closeSession() {
	if (session == null) return;
	try {
	    session.logout();
	} catch(Exception e) {}
	try {
	    session.close();
	} catch(Exception e) {}
	session = null;
    }
}
