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

import org.globus.security.*;
import org.globus.pkcs11.*;
import com.ibm.pkcs11.*; 
import iaik.x509.X509Certificate;

public class PKCS11GridProxyModel extends GridProxyModel {
    
    public GlobusProxy createProxy(String pwd)
	throws Exception {   
	
	getProperties();
	String certHandle = props.getProperty("pkcs11.certhandle");
	String keyHandle  = props.getProperty("pkcs11.keyhandle");

	if (certHandle == null || certHandle.length() == 0) {
	    if (keyHandle == null || keyHandle.length() == 0) {
		certHandle = keyHandle = props.getDefaultPKCS11Handle();
	    } else {
		certHandle = keyHandle;
	    }
	} else {
	    if (keyHandle == null || keyHandle.length() == 0) {
		keyHandle = certHandle;
	    }
	}
	
	try {
            PKCS11Util.initiateSession(pwd,
                                       PKCS11SessionInfo.SERIAL_SESSION |  
                                       PKCS11SessionInfo.RW_USER_FUNCTIONS);
	    
            userCert = PKCS11CertUtil.retrieveCertificate(certHandle);
	    
	    PKCS11Object userPrivateKeyObj = 
		PKCS11Util.retrievePrivateKeyHandle(keyHandle);
	    
	    if (userPrivateKeyObj == null) {
		throw new Exception("Failed to retrieve the key from the PKCS11 device.");
	    }
	    
            return PKCS11CertUtil.createProxy((iaik.x509.X509Certificate)userCert,
					      userPrivateKeyObj,
					      props.getProxyStrength(),
					      props.getProxyLifeTime(),
					      getLimited());
	} finally {
            PKCS11Util.closeSession();
	}
	
    }
    
}
