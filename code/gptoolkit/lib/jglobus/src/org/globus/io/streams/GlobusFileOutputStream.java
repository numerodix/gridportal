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
package org.globus.io.streams;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;

public class GlobusFileOutputStream extends GlobusOutputStream {
    
    private OutputStream output;
    
    public GlobusFileOutputStream(String file, boolean append) 
	throws IOException {
	output = new FileOutputStream(file, append);
    }

    public void abort() {
	try {
	    output.close();
	} catch(Exception e) {}
    }

    public void close() 
	throws IOException {
	output.close();
    }

    public void write(byte [] msg) 
	throws IOException {
	output.write(msg);
    }

    public void write(byte [] msg, int from, int length) 
	throws IOException {
	output.write(msg, from, length);
    }

    public void write(int b)
	throws IOException {
	output.write(b);
    }

    public void flush() 
	throws IOException {
	output.flush();
    }
    
}






