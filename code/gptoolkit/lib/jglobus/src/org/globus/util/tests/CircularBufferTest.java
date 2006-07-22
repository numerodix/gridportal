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

import org.globus.util.CircularBuffer;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CircularBufferTest extends TestCase {

    private CircularBuffer buffer;

    public  CircularBufferTest(String name) {
	super(name);
    }

    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(CircularBufferTest.class);
    }

    protected void setUp() throws Exception {
	buffer = new CircularBuffer(5);
    }

    public void testInterruptBoth() throws Exception {

	assertTrue(buffer.put("a"));
	assertTrue(buffer.put("b"));
	buffer.interruptBoth();
	assertTrue(!buffer.put("c"));
	assertTrue(!buffer.put("d"));

	assertEquals(null, buffer.get());
	assertEquals(null, buffer.get());
    }

    public void testPutFull() throws Exception {

	assertTrue(buffer.put("a"));
	assertTrue(buffer.put("b"));
	assertTrue(buffer.put("c"));
	assertTrue(buffer.put("d"));
	assertTrue(buffer.put("e"));

	Thread t = (new Thread() {
		public void run() {
		    buffer.closePut();
		    buffer.interruptPut();
		}
	    });
	t.start();

	assertTrue(!buffer.put("f"));
	assertTrue(!buffer.put("g"));

	assertEquals("a", buffer.get());
	assertEquals("b", buffer.get());
	assertEquals("c", buffer.get());
	assertEquals("d", buffer.get());
	assertEquals("e", buffer.get());
	assertEquals(null, buffer.get());
    }

}
