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
package org.globus.tools.ui.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.globus.gsi.CertUtil;

public class Configure {

    // --------- CERTIFICATE VERIFICATION ---------

    public static X509Certificate loadAndVerifyCertificate(String file)
	throws Exception {
	
	X509Certificate cert = null;
	String msg = null;
	
	try {
	    cert = CertUtil.loadCertificate(file);
	    cert.checkValidity();
	} catch (IOException e) {
	    msg = "Unable to load certificate : " + e.getMessage();
	} catch (GeneralSecurityException e) {
	    msg = "Invalid certificate : " + e.getMessage();
	}
	
	if (msg != null) {
	    throw new Exception(msg);
	} else {
	    return cert;
	}
    }


    public static X509Certificate verifyUserCertificate(String file)
	throws Exception {
	
	X509Certificate cert = loadAndVerifyCertificate(file);
	
	if (cert.getSubjectDN().equals(cert.getIssuerDN())) {
	    throw new Exception("This is a self-signed certificate.");
	}
	
	return cert;
    }
    
    public static X509Certificate verifyCertWithCA(String file,
						   X509Certificate usercert)
	throws Exception {
	
	X509Certificate cert = loadAndVerifyCertificate(file);
	    
	if (!cert.getSubjectDN().equals(cert.getIssuerDN())) {
	    throw new Exception("This is NOT a self-signed certificate.");
	}

	try {
	    // verify if this is the right ca cert...
	    usercert.verify(cert.getPublicKey());
	} catch(GeneralSecurityException e) {
	    throw new Exception("User certificate probably not signed by this" +
				" CA : " + e.getMessage());
	}

	return cert;
    }

}



