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
package org.globus.ftp.test;

import org.globus.ftp.dc.AsciiTranslator;
import org.globus.ftp.Buffer;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsciiTranslatorTest extends TestCase {

    static Log logger = 
	LogFactory.getLog(AsciiTranslatorTest.class);
    
    public static void main(String[] argv) {
	junit.textui.TestRunner.run (suite());
    }
    
    public static Test suite() {
	return new TestSuite(AsciiTranslatorTest.class);
    }

    public AsciiTranslatorTest(String name) {
	super(name);
    }

    public void test1() {
	crlnTest("\n");
    }

    public void test2() {
	crlnTest("\r\n");
    }

    public void crlnTest(String lineSep) {
	AsciiTranslator t = new AsciiTranslator(true,
						false,
						lineSep.getBytes());
	
	check(t, "1abcdef", "1abcdef");
	
	check(t, "2abc\r\ndef", "2abc" + lineSep + "def");
	
	check(t, "3abc\r\r\ndef", "3abc\r" + lineSep + "def");
	
	check(t, "4abc\n\n\n\n", "4abc\n\n\n\n");
	
	check(t, "a\rb\r\nc\n\n\r\rd", "a\rb" + lineSep + "c\n\n\r\rd");
	
	check(t, "abc\r", "abc");
	check(t, "def", "\rdef");
	
	check(t, "a\r\n\nbc\r", "a" + lineSep + "\nbc");
	check(t, "\n\r\ndef", lineSep + lineSep + "def");
	
    }

    public void test3() {
	lnTest("\n");
    }

    public void test4() {
	lnTest("\r\n");
    }

    public void lnTest(String lineSep) {
	AsciiTranslator t = new AsciiTranslator(false,
						true,
						lineSep.getBytes());
	
	check(t, "1abcdef", "1abcdef");
	
	check(t, "2abc\r\ndef", "2abc\r" + lineSep + "def");
	
	check(t, "3abc\r\r\ndef", "3abc\r\r" + lineSep + "def");
	
	check(t, "4abc\n\n\n", "4abc" + lineSep + lineSep + lineSep);
	
	check(t, "a\rb\r\nc\n\n\r\rd", "a\rb\r" + lineSep + "c" + lineSep +
	      lineSep + "\r\rd");
	
	check(t, "abc\r", "abc\r");
	
	check(t, "a\r\n\nbc\r", "a\r" + lineSep + lineSep + "bc\r");
	check(t, "\n\r\ndef", lineSep + "\r" + lineSep + "def");
	
    }

    private void check(AsciiTranslator t,
		       String inputStr,
		       String expectedStr) {
	byte [] input = inputStr.getBytes();
	byte [] output = 
	    t.translate(new Buffer(input, input.length)).getBuffer();
	assertEquals(expectedStr, new String(output));
    }
}
