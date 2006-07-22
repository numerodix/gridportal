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
package org.globus.gsi.ptls.test;

import COM.claymoresystems.cert.X509Name;

import org.globus.gsi.ptls.PureTLSUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.TrustedCertificates;

import java.util.Vector;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class PureTLSUtilTest extends TestCase {
    
    public PureTLSUtilTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(PureTLSUtilTest.class);
    }

    public void testSimple() throws Exception {

	X509Name name;
	Vector dn;
	Vector rdn;

	name = PureTLSUtil.getX509Name("/C=US");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(1, dn.size());

	name = PureTLSUtil.getX509Name("/C=US/O=ANL");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(2, dn.size());

	name = PureTLSUtil.getX509Name("/C=US/O=Globus/O=ANL/OU=MCS/CN=gawor/CN=proxy");
	assertEquals("C=US,O=Globus,O=ANL,OU=MCS,CN=gawor,CN=proxy", 
		     name.getNameString());
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(6, dn.size());

	name = PureTLSUtil.getX509Name("/C=US/O=Globus/O=ANL/OU=MCS/CN=gawor/CN=host/pitcairn.mcs.anl.gov");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(6, dn.size());
	rdn = (Vector)dn.elementAt(dn.size()-1);
	assertEquals(1, rdn.size());
	assertEquals("CN", ((String[])rdn.elementAt(0))[0]);
	assertEquals("host/pitcairn.mcs.anl.gov", ((String[])rdn.elementAt(0))[1]);
	
	name = PureTLSUtil.getX509Name("/C=US/O=Globus/O=ANL/OU=MCS/CN=host/pitcairn.mcs.anl.gov/CN=gawor");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(6, dn.size());
	rdn = (Vector)dn.elementAt(dn.size()-2);
	assertEquals(1, rdn.size());
	assertEquals("CN", ((String[])rdn.elementAt(0))[0]);
	assertEquals("host/pitcairn.mcs.anl.gov", ((String[])rdn.elementAt(0))[1]);

	name = PureTLSUtil.getX509Name("/C=US/CN=host/pitcairn.mcs.anl.gov/CN=gawor+OU=ANL");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(3, dn.size());
	rdn = (Vector)dn.elementAt(dn.size()-1);
	assertEquals(2, rdn.size());
	assertEquals("CN", ((String[])rdn.elementAt(0))[0]);
	assertEquals("gawor", ((String[])rdn.elementAt(0))[1]);
	assertEquals("OU", ((String[])rdn.elementAt(1))[0]);
	assertEquals("ANL", ((String[])rdn.elementAt(1))[1]);

	name = PureTLSUtil.getX509Name("/C=US/CN=gawor+EmailAddress=gawor@anl.gov/CN=host/pitcairn.mcs.anl.gov");
	System.out.println(name.getNameString());
	dn = name.getName();
	assertEquals(3, dn.size());
	rdn = (Vector)dn.elementAt(dn.size()-2);
	assertEquals(2, rdn.size());
	assertEquals("CN", ((String[])rdn.elementAt(0))[0]);
	assertEquals("gawor", ((String[])rdn.elementAt(0))[1]);
	assertEquals("EMAILADDRESS", ((String[])rdn.elementAt(1))[0]);
	assertEquals("gawor@anl.gov", ((String[])rdn.elementAt(1))[1]);
    }

}
