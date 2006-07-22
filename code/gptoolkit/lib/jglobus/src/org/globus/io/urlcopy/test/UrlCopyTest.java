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
package org.globus.io.urlcopy.test;

import java.io.File;

import org.globus.io.gass.server.GassServer;
import org.globus.io.urlcopy.UrlCopy;
import org.globus.util.GlobusURL;
import org.globus.util.TestUtil;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class UrlCopyTest extends TestCase { 

    private static final String CONFIG = 
	"org/globus/io/urlcopy/test/test.properties";

    private static TestUtil util;
    
    private GassServer server;

    static {
	try {
	    util = new TestUtil(CONFIG);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }

    public UrlCopyTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(UrlCopyTest.class);
    }

    public void setUp() throws Exception {
	server = new GassServer();
	server.setOptions( GassServer.STDOUT_ENABLE |
			   GassServer.STDERR_ENABLE |
			   GassServer.READ_ENABLE |
			   GassServer.WRITE_ENABLE );
    }

    public void testGassGet() throws Exception {

	File src = new File(util.get("src.file"));
	assertTrue(src.exists());

	String from = server.getURL() + "/" +
	    util.get("src.file");

	File tmp = File.createTempFile("gassget", null);
	tmp.deleteOnExit();
	
	UrlCopy uc = new UrlCopy();
	uc.setSourceUrl(new GlobusURL(from));
	uc.setDestinationUrl(new GlobusURL(tmp.toURL()));
	uc.copy();
	
	assertEquals(src.length(), tmp.length());
    }

    public void testGassPut() throws Exception {

	File src = new File(util.get("src.file"));
	assertTrue(src.exists());

	File tmp = File.createTempFile("gassput", null);
	tmp.deleteOnExit();

	String to = server.getURL() + "/" + tmp.getAbsolutePath();

	System.out.println(to);

	UrlCopy uc = new UrlCopy();
	uc.setSourceUrl(new GlobusURL(src.toURL()));
	uc.setDestinationUrl(new GlobusURL(to));
	uc.copy();
	
	assertEquals(src.length(), tmp.length());
    }

    public void tearDown() throws Exception {
	if (server != null) {
	    server.shutdown();
	}
    }
}
