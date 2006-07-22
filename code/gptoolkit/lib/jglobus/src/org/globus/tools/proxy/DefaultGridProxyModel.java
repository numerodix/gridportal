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
package org.globus.tools.proxy;

import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.globus.gsi.CertUtil;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.X509ExtensionSet;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;

public class DefaultGridProxyModel extends GridProxyModel {
    
    public GlobusCredential createProxy(String pwd)
	throws Exception {   

	getProperties();

	userCert = CertUtil.loadCertificate(props.getUserCertFile());
	
	OpenSSLKey key = 
	    new BouncyCastleOpenSSLKey(props.getUserKeyFile());
	
	if (key.isEncrypted()) {
	    try {
		key.decrypt(pwd);
	    } catch(GeneralSecurityException e) {
		throw new Exception("Wrong password or other security error");
	    }
	}
	
	PrivateKey userKey = key.getPrivateKey();
	
	BouncyCastleCertProcessingFactory factory =
	    BouncyCastleCertProcessingFactory.getDefault();

	int proxyType = (getLimited()) ? 
	    GSIConstants.DELEGATION_LIMITED :
	    GSIConstants.DELEGATION_FULL;

	return factory.createCredential(new X509Certificate[] {userCert},
					userKey,
					props.getProxyStrength(), 
					props.getProxyLifeTime() * 3600,
					proxyType,
					(X509ExtensionSet)null);
    }
    
    
}
