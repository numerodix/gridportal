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
package org.globus.pkcs11.tools;

import org.globus.tools.ProxyInit;
import org.globus.pkcs11.PKCS11Util;
import org.globus.pkcs11.PKCS11CertUtil;
import org.globus.util.Util;

import com.ibm.pkcs11.PKCS11SessionInfo;
import com.ibm.pkcs11.PKCS11Object;

/**
 * This is a PKCS11 module for the ProxyInit command-line tool. Please, do not use it
 * as a library.
 */
public class PKCS11ProxyInit extends ProxyInit {

    private PKCS11Object userPrivateKeyObj = null;
    
    public void init(String [] args) {	 
	String pin = Util.getInput("Enter PKCS11 device pin: ");
	if (pin == null) return; // user canceled

	try {
	    PKCS11Util.initiateSession(pin, PKCS11SessionInfo.SERIAL_SESSION |  
				       PKCS11SessionInfo.RW_USER_FUNCTIONS);
	} catch(Exception p) {
	    System.err.println("Could not initiate session with PKCS11 device: " + 
			       p.getMessage());
	    System.exit(-1);
	}
    }
    
    public void loadCertificate(String arg) {
	try {
	    certificate = PKCS11CertUtil.retrieveCertificate(arg);
	} catch (java.security.cert.CertificateException e) {
	    System.err.println("Failed to retrieve the certificate from the device: " +
			       e.getMessage());
	    exit(-1);
	}
    }

    public void loadKey(String arg) {
	userPrivateKeyObj = PKCS11Util.retrievePrivateKeyHandle(arg);
	if (userPrivateKeyObj == null) {
	    System.err.println("Failed to retrieve the key from the device");
	    exit(-1);
	}
    }

    public void sign() {
	try {
	    proxy = PKCS11CertUtil.createProxy((iaik.x509.X509Certificate)certificate,
					       userPrivateKeyObj,
					       bits,
					       hours,
					       limited);
	} catch (Exception e) {
	    System.err.println("Failed to create a proxy: " + e.getMessage());
	    exit(-1);
	}
    }

    public void dispose() {
	PKCS11Util.closeSession();
    }
    
    private void exit(int rs) {
	PKCS11Util.closeSession();
	System.exit(rs);
    }
    
}
