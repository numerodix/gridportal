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
package org.globus.gsi.bc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.globus.gsi.OpenSSLKey;

import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

/**
 * BouncyCastle-based implementation of OpenSSLKey.
 */
public class BouncyCastleOpenSSLKey extends OpenSSLKey {

    public BouncyCastleOpenSSLKey(InputStream is) 
	throws IOException, GeneralSecurityException {
	super(is);
    }

    public BouncyCastleOpenSSLKey(String file)
	throws IOException, GeneralSecurityException {
	super(file);
    }

    public BouncyCastleOpenSSLKey(PrivateKey key) {
	super(key);
    }

    public BouncyCastleOpenSSLKey(String algorithm, byte [] data) 
	throws GeneralSecurityException {
	super(algorithm, data);
    }
  
    protected PrivateKey getKey(String alg, byte [] data) 
	throws GeneralSecurityException {
	if (alg.equals("RSA")) {
	    try {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DERInputStream derin = new DERInputStream(bis);
		DERObject keyInfo = derin.readObject();
		
		DERObjectIdentifier rsa_oid = PKCSObjectIdentifiers.rsaEncryption;    	   
		AlgorithmIdentifier rsa = new AlgorithmIdentifier(rsa_oid);
		PrivateKeyInfo pkeyinfo = new PrivateKeyInfo(rsa, keyInfo);
		DERObject derkey = pkeyinfo.getDERObject();		
		
		byte[] keyData = BouncyCastleUtil.toByteArray(derkey);

		// The DER object needs to be mangled to 
		// create a proper ProvateKeyInfo object 
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyData);
		KeyFactory kfac = KeyFactory.getInstance("RSA");
		
		return kfac.generatePrivate(spec);
	    } catch (IOException e) {
		// that should never happen
		return null;
	    }
	    
	} else {
	    return null;
	}
    }
    
    protected byte[] getEncoded(PrivateKey key) {
	String format = key.getFormat();
	if (format != null && 
	    (format.equalsIgnoreCase("PKCS#8") ||
	     format.equalsIgnoreCase("PKCS8"))) {
	    try {
		DERObject keyInfo = BouncyCastleUtil.toDERObject(key.getEncoded());
		PrivateKeyInfo pkey = new PrivateKeyInfo((ASN1Sequence)keyInfo);
		DERObject derKey = pkey.getPrivateKey();
		return BouncyCastleUtil.toByteArray(derKey);
	    } catch (IOException e) {
		// that should never happen
		e.printStackTrace();
		return null;
	    }
	} else if (format != null && 
		   format.equalsIgnoreCase("PKCS#1") &&
		   key instanceof RSAPrivateCrtKey) { // this condition will rarely be true
	    RSAPrivateCrtKey pKey = (RSAPrivateCrtKey)key;
	    RSAPrivateKeyStructure st = 
		new RSAPrivateKeyStructure(pKey.getModulus(),
					   pKey.getPublicExponent(),
					   pKey.getPrivateExponent(),
					   pKey.getPrimeP(),
					   pKey.getPrimeQ(),
					   pKey.getPrimeExponentP(),
					   pKey.getPrimeExponentQ(),
					   pKey.getCrtCoefficient());
	    DERObject ob = st.getDERObject();
	    
	    try {
		return BouncyCastleUtil.toByteArray(ob);
	    } catch (IOException e) {
		// that should never happen
		return null;
	    }
	} else {
	    return null;
	}
    }

    protected String getProvider() {
	return "BC";
    }
}
