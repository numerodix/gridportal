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

import org.globus.security.GlobusProxy;
import org.globus.security.CertUtil;

import iaik.x509.*;
import iaik.asn1.*;
import iaik.asn1.structures.*;
import iaik.security.rsa.*;

import java.security.PublicKey;
import java.security.KeyPair;
import java.security.InvalidKeyException;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Calendar;

import com.ibm.pkcs11.PKCS11Object;

/***********************************************************************
 * Contains various utility functions related to dealing with certificates
 * and slightly related to PKCS11.  Some of this functionality needs to 
 * come out of this class and be better integrated with the org.globus.
 * security.CertUtil class but for the moment, it works.
 **********************************************************************/
public class PKCS11CertUtil {
    
    /**
     * Creates a Globus Proxy object.  This function is aware of PKCS11
     * because it needs to deal with a private key handle rather than
     * a private key, and also deals with the PKCS11 signing.
     **/
    public static GlobusProxy createProxy(X509Certificate userCert,
					  PKCS11Object userPrivateKeyObj,
					  int bits,
					  int hours,
					  boolean limited) 
	throws PKCS11Exception {
	
	KeyPair rsaKeyPair = CertUtil.generateKeyPair("RSA", bits);
	X509Certificate certToBeSigned = null;
	try {
	    certToBeSigned = 
		createNewCertificate(userCert, rsaKeyPair.getPublic(), 
				     limited, hours);
	} catch(InvalidKeyException e) {
	    throw new PKCS11Exception("Failure to extract public key from key pair", 
				      e);
	}
	
	X509Certificate proxyCert = null;
	try {
	    proxyCert = PKCS11Util.signCertificate(certToBeSigned, userPrivateKeyObj);
	} catch(Exception e) {
	    throw new PKCS11Exception("Failed to sign certificate: " + 
				      e.getMessage(), e);
	}
	
	X509Certificate[] crts = new X509Certificate[2];
	crts[0] = proxyCert;
	crts[1] = userCert;
	
	return new GlobusProxy(rsaKeyPair.getPrivate(), crts);
    }
    
    /**
     * Creates a new certificate based on the information passed in.  This
     * function has nothing to do with PKCS11, but needs to be seperated out
     * properly in the org.globus.security.CertUtil class
     */
    private static X509Certificate createNewCertificate(X509Certificate cert,
							PublicKey pubKey, 
							boolean limited,
							int hours) 
	throws java.security.InvalidKeyException {
	Name user_subject = CertUtil.dupName((Name)cert.getSubjectDN());
	
	String name = null;
	
	if (limited) {
	    name = "limited proxy";
	} else {
	    name = "proxy";
	} 
	
	user_subject.addRDN(ObjectID.commonName, name);
	
	X509Certificate newCert = 
	    new iaik.x509.X509Certificate();
	
	newCert.setSerialNumber(new BigInteger(20,
					       new Random()));
	newCert.setSubjectDN(user_subject);
	newCert.setIssuerDN(cert.getSubjectDN());
	newCert.setPublicKey(pubKey);
	
	GregorianCalendar date = 
	    new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	
	date.add(Calendar.MINUTE, -5);
	newCert.setValidNotBefore(date.getTime());                    
	
	date.add(Calendar.MINUTE, 5);
	date.add(Calendar.HOUR, hours);
	newCert.setValidNotAfter(date.getTime());

	return newCert;
    }
    
    public static X509Certificate retrieveCertificate(String certLabel) 
	throws java.security.cert.CertificateException {
	PKCS11Object handle = PKCS11Util.retrieveUserCertHandle(certLabel);
	if (handle == null) {
	    throw new java.security.cert.CertificateException("Could not find certificate with '" + certLabel + "' label.");
	}
	return new iaik.x509.X509Certificate
	    (handle.getByteArrayAttributeValue(PKCS11Object.VALUE));
    }

}
