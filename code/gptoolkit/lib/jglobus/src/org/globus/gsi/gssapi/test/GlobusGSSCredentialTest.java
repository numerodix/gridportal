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
package org.globus.gsi.gssapi.test;

import java.io.File;
import java.security.cert.X509Certificate;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSCredential;

import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSCredential;

import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.globus.gsi.gssapi.GlobusGSSException;
import org.globus.gsi.gssapi.GSSConstants;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class GlobusGSSCredentialTest extends TestCase {

    ExtendedGSSManager manager;

    public GlobusGSSCredentialTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(GlobusGSSCredentialTest.class);
    }
    
    protected void setUp() throws Exception {
	manager = new GlobusGSSManagerImpl();
    }

    public void testImportBadFile() throws Exception {
	String handle = "PROXY = /a/b/c";
	
	try {
	    manager.createCredential(handle.getBytes(),
				     ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC,
				     GSSCredential.DEFAULT_LIFETIME,
				     null,
				     GSSCredential.ACCEPT_ONLY);
	    fail("Exception not thrown as expected.");
	} catch (GSSException e) {
	    // TODO: check for specific major/minor code
	}
	
    }

    public void testImportBadOption() throws Exception {
	String handle = "PROXY = /a/b/c";
	
	try {
	    manager.createCredential(handle.getBytes(),
				     3,
				     GSSCredential.DEFAULT_LIFETIME,
				     null,
				     GSSCredential.ACCEPT_ONLY);
	    fail("Exception not thrown as expected.");
	} catch (GSSException e) {
	    if (e.getMajor() != GSSException.FAILURE &&
		e.getMinor() != GlobusGSSException.BAD_ARGUMENT) {
		e.printStackTrace();
		fail("Unexpected exception");
	    }
	}
	
    }

    public void testImportExportOpaque() throws Exception {
	
	GlobusGSSCredentialImpl cred = 
	    (GlobusGSSCredentialImpl)manager.createCredential(GSSCredential.ACCEPT_ONLY);
	assertTrue(cred != null);
	
	byte [] data = cred.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
	assertTrue(data != null);

	System.out.println(new String(data));
	
	GlobusGSSCredentialImpl cred2 = 
	    (GlobusGSSCredentialImpl)manager.createCredential(data,
							      ExtendedGSSCredential.IMPEXP_OPAQUE,
							      GSSCredential.DEFAULT_LIFETIME,
							      null,
							      GSSCredential.ACCEPT_ONLY);
	assertTrue(cred2 != null);
	assertEquals(cred.getPrivateKey(), cred2.getPrivateKey());
    }
    
    public void testImportExportMechSpecific() throws Exception {
	
	GlobusGSSCredentialImpl cred = 
	    (GlobusGSSCredentialImpl)manager.createCredential(GSSCredential.ACCEPT_ONLY);
	assertTrue(cred != null);
	
	byte [] data = cred.export(ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC);
	assertTrue(data != null);

	String handle = new String(data);
	System.out.println(handle);
	
	GlobusGSSCredentialImpl cred2 = 
	    (GlobusGSSCredentialImpl)manager.createCredential(data,
							      ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC,
							      GSSCredential.DEFAULT_LIFETIME,
							      null,
							      GSSCredential.ACCEPT_ONLY);
	assertTrue(cred2 != null);

	assertEquals(cred.getPrivateKey(), cred2.getPrivateKey());

	handle = handle.substring(handle.indexOf('=')+1);
	assertTrue((new File(handle)).delete());
    }

    public void testInquireByOid() throws Exception {

	ExtendedGSSCredential cred =
	    (ExtendedGSSCredential)manager.createCredential(GSSCredential.ACCEPT_ONLY);

	Object tmp = null;
	X509Certificate[] chain = null;
	
	tmp = cred.inquireByOid(GSSConstants.X509_CERT_CHAIN);
	assertTrue(tmp != null);
	assertTrue(tmp instanceof X509Certificate[]);
	chain = (X509Certificate[])tmp;
	assertTrue(chain.length > 0);
    }
}
