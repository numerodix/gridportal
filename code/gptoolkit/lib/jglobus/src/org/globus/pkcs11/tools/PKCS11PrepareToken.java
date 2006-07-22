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

import org.globus.util.Util;
import org.globus.pkcs11.PKCS11Util;
import org.globus.security.CertUtil;
import org.globus.security.SSLeayKeyHolder;
import org.globus.common.CoGProperties;
import org.globus.common.Version;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import iaik.security.rsa.*;

import com.ibm.pkcs11.PKCS11SessionInfo;
import com.ibm.pkcs11.PKCS11Object;

/**
 * Allows a Globus user to load his/her credentials onto a PKCS11 
 * device. It uses cog.properties to determine where the credentials
 * are located.  Asks the user for the user pin of the pkcs11 device
 * in order to write to the device.  It also asks (if needed) for the user's
 * grid pass phrase in order to access the user's private credentials.
 */
public class PKCS11PrepareToken {

    private static final String message =
	"\n" +
	"Syntax: java PKCS11PrepareToken [options]\n" +
	"        java PKCS11PrepareToken -help\n\n" +
	"\tLoads Globus credentials onto a PKCS11 device.\n" +
	"\tOptions\n" +
	"\t-help, -usage             Displays usage\n" +
	"\t-version                  Displays version\n" +
	"\t-debug                    Enables extra debug output\n" +
	"\t-cert     <certfile>      Non-standard location of user certificate\n" +
	"\t-key      <keyfile>       Non-standard location of user key\n" +
	"\t-label    <label>         Label associated with the credentials on \n" +
	"\t                          the device. Defaults to '" + 
	CoGProperties.getDefault().getDefaultPKCS11Handle() + 
	"\n" +
	"\t-pin <pin>                PKCS11 device pin number.\n\n\n";
    
    public static void main(String[] args) {

	CoGProperties properties = CoGProperties.getDefault();

	boolean error   = false;
	boolean debug   = false;
	String keyFile  = properties.getUserKeyFile();
	String certFile = properties.getUserCertFile();
	String label    = properties.getDefaultPKCS11Handle();
	String pin      = null;
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equalsIgnoreCase("-debug")) {
		debug = true;
	    } else if (args[i].equalsIgnoreCase("-key")) {
		keyFile = args[++i];
	    } else if (args[i].equalsIgnoreCase("-cert")) {
		certFile = args[++i];
	    } else if (args[i].equalsIgnoreCase("-label")) {
		label = args[++i];
	    } else if (args[i].equalsIgnoreCase("-pin")) {
		pin = args[++i];
	    } else if (args[i].equalsIgnoreCase("-version")) {
		System.err.println(Version.getVersion());
		System.exit(1);
	    } else if (args[i].equalsIgnoreCase("-help") ||
		       args[i].equalsIgnoreCase("-usage")) {
		System.err.println(message);
		System.exit(1);
	    } else {
		System.err.println("Error: Argument not recognized: " + args[i]);
		error = true;
	    }
	}
	
	if (error) return;

	if (debug) {
	    System.out.println("### Current settings ###");
	    System.out.println("       Certificate file : " + certFile);
	    System.out.println("        SSLeay key file : " + keyFile);
	    System.out.println("           PKCS11 label : " + label);
	}
	

	RSAPrivateKey userKey = null;
	iaik.x509.X509Certificate userCert = null;

	try {
	    userCert = 
		(iaik.x509.X509Certificate)CertUtil.loadCert(certFile);
	} catch(IOException e) {
	    System.err.println("Error: Failed to load the certificate: " + 
			       e.getMessage());
	    System.exit(-1);
	} catch(GeneralSecurityException e) {
	    System.err.println("Error: Something is wrong with the certificate: " +
			       e.getMessage());
	    System.exit(-1);
	}

	try {
	    SSLeayKeyHolder key = new SSLeayKeyHolder(keyFile);
	    if (key.isEncrypted()) {
		String pwd = Util.getInput("Enter GRID pass phrase: ");
		if (pwd == null) return; // user canceled
		key.decrypt(pwd);
	    } 
	    userKey = (RSAPrivateKey)key.getPrivateKey();
	} catch(IOException e) {
	    System.err.println("Error: Failed to load the key: " + keyFile);
	    System.exit(-1);
	} catch(GeneralSecurityException e) {
	    System.err.println("Error: Wrong pass phrase or some other security problem with the key (" + e.getMessage() + ")");
	    System.exit(-1);
	}

	if (pin == null) {
	    pin = Util.getInput("Enter PKCS11 device pin: ");
	    if (pin == null) return; // user canceled
	}
	
	try {
	    // start a serial, read-write session with the device
	    PKCS11Util.initiateSession(pin, 
				       PKCS11SessionInfo.SERIAL_SESSION | 
				       PKCS11SessionInfo.RW_SESSION);
	} catch(Exception e) {
	    System.err.println("Error: Could not initiate session with the device: "
			       + e.getMessage());
	    System.exit(-1);
	}
	
	// anywhere here close the session
	
	try {
	    //create a shared ID for the cert and key from the
	    //hash of the private key's modulus
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    md5.update(userKey.getModulus().toByteArray());
	    byte[] idBytes = md5.digest();
	    
	    PKCS11Object userCertHandle = 
		PKCS11Util.instantiateUserCert(userCert, 
					       label,
					       idBytes);
	    PKCS11Object userKeyHandle = 
		PKCS11Util.instantiateUserPrivateKey(userKey, 
						     userCert.getSubjectDN(), 
						     idBytes);

	    System.out.println("Your credentials were successfully loaded onto the" +
			       " device under '" + label + "' label.");
	    
	} catch(Exception e) {
	    System.err.println("Error: Failed to load the credentials onto the PKCS11 device: " + e.getMessage());
	    if (debug) {
		e.printStackTrace();
	    }
	    System.exit(-1);
	} finally {
	    // close session!!!
	    PKCS11Util.closeSession();
	}
    }
    
}
