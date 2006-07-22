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
package org.globus.util.tests;

import org.globus.util.Util;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilTest extends TestCase {

    private static final String uStr1 = "(exe = mis)";
    private static final String qStr1 = "\"(exe = mis)\"";
    
    private static final String uStr2 = "(exe = \"mis\")";
    private static final String qStr2 = "\"(exe = \\\"mis\\\")\"";

    private static final String uStr3 = "(exe = \"mis\"\\test)";
    private static final String qStr3 = "\"(exe = \\\"mis\\\"\\\\test)\"";
    
    public UtilTest(String name) {
	super(name);
    }

    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(UtilTest.class);
    }

    public void testQuote1() {
	String tStr1 = Util.quote(uStr1);
	System.out.println(uStr1 + " : " + tStr1);
	assertEquals("t1", qStr1, tStr1);

	String tStr2 = Util.quote(uStr2);
        System.out.println(uStr2 + " : " + tStr2);
        assertEquals("t2", qStr2, tStr2);

        String tStr3 = Util.quote(uStr3);
        System.out.println(uStr3 + " : " + tStr3);
        assertEquals("t3", qStr3, tStr3);
    }

    public void testUnQuote1() {
        try {
            String tStr0 = Util.unquote(uStr1);
            System.out.println(uStr1 + " : " + tStr0);
            assertEquals("t0", uStr1, tStr0);
        } catch(Exception e) {
            fail("Unquote failed.");
        }

	try {
	    String tStr1 = Util.unquote(qStr1);
	    System.out.println(qStr1 + " : " + tStr1);
	    assertEquals("t1", uStr1, tStr1);
	} catch(Exception e) {
	    fail("Unquote failed.");
	}

        try {
            String tStr2 = Util.unquote(qStr2);
            System.out.println(qStr2 + " : " + tStr2);
            assertEquals("t2", uStr2, tStr2);
        } catch(Exception e) {
            fail("Unquote failed.");
        }

        try {
            String tStr3 = Util.unquote(qStr3);
            System.out.println(qStr3 + " : " + tStr3);
            assertEquals("t3", uStr3, tStr3);
        } catch(Exception e) {
            fail("Unquote failed.");
        }	
    }
}

