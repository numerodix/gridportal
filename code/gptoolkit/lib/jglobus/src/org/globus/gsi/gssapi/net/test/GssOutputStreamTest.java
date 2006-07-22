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
package org.globus.gsi.gssapi.net.test;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import org.globus.gsi.gssapi.net.GssOutputStream;

import org.ietf.jgss.GSSContext;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class GssOutputStreamTest extends TestCase {

    public GssOutputStreamTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(GssOutputStreamTest.class);
    }
    
    public void test1() throws Exception {
	ByteArrayOutputStream out
	    = new ByteArrayOutputStream();

	TestGssOutputStream t = new TestGssOutputStream(out, 5);

	t.write('A');
	t.write('B');
	
	assertEquals(2, t.getIndex());

	t.write('C');
	t.write('D');
	t.write('E');

	assertEquals(5, t.getIndex());

	t.write('F');

	assertEquals(1, t.getIndex());

	assertEquals("ABCDE", new String(out.toByteArray()));
    }


    public void test2() throws Exception {
	ByteArrayOutputStream out
	    = new ByteArrayOutputStream();

	TestGssOutputStream t = new TestGssOutputStream(out, 5);

	byte [] m1 = new byte[] {'A', 'B'};
	t.write(m1);
	
	assertEquals(2, t.getIndex());

	byte [] m2 = new byte[] {'C', 'D', 'E'};
	t.write(m2);

	assertEquals(5, t.getIndex());

	t.write('F');

	assertEquals(1, t.getIndex());

	assertEquals("ABCDE", new String(out.toByteArray()));
    }
    
    public void test3() throws Exception {
	ByteArrayOutputStream out
	    = new ByteArrayOutputStream();

	TestGssOutputStream t = new TestGssOutputStream(out, 5);

	byte [] m1 = new byte[] {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
	t.write(m1);
	
	assertEquals(2, t.getIndex());

	assertEquals("ABCDE", new String(out.toByteArray()));
    }

    public void test4() throws Exception {
	ByteArrayOutputStream out
	    = new ByteArrayOutputStream();
	
	TestGssOutputStream t = new TestGssOutputStream(out, 5);
	
	byte [] m1 = new byte[] {'A', 'B', 'C', 'D', 'E', 
				 'F', 'G', 'H', 'I', 'J',
				 'K', 'L', 'M'};
	t.write(m1);
	
	assertEquals(3, t.getIndex());

	assertEquals("ABCDEFGHIJ", new String(out.toByteArray()));
    }

    public void test5() throws Exception {
	ByteArrayOutputStream out
	    = new ByteArrayOutputStream();
	
	TestGssOutputStream t = new TestGssOutputStream(out, 5);
	
	byte [] m1 = new byte[] {'A', 'B', 'C', 'D', 'E', 
				 'F', 'G', 'H', 'I', 'J',
				 'K', 'L', 'M', 'N', 'O'};
	t.write(m1);
	
	assertEquals(5, t.getIndex());

	assertEquals("ABCDEFGHIJ", new String(out.toByteArray()));

	t.write('B');
	
	assertEquals(1, t.getIndex());
	assertEquals("ABCDEFGHIJKLMNO", new String(out.toByteArray()));
    }

    class TestGssOutputStream extends GssOutputStream {

	public TestGssOutputStream(OutputStream out, int size) {
	    super(out, null, size);
	}

	public int getIndex() {
	    return index;
	}

	public void flush()
	    throws IOException {
	    out.write(buff, 0, index);
	    index = 0;
	}

    }

}
