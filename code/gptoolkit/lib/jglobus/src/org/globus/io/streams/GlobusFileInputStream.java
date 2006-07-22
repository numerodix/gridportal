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

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class GlobusFileInputStream extends GlobusInputStream {
    
    private FileInputStream input;
    private long size = -1;
    
    public GlobusFileInputStream(String file) throws IOException {
	File f = new File(file);
	input = new FileInputStream(f);
	size = f.length();
    }

    public long getSize() {
	return size;
    }

    public void abort() {
	try {
	    input.close();
	} catch(Exception e) {}
    }

    // standard InputStream methods

    public void close() 
	throws IOException {
	input.close();
    }
    
    public int read(byte [] msg) 
	throws IOException {
	return input.read(msg);
    }
    
    public int read(byte [] buf, int off, int len) 
	throws IOException {
	return input.read(buf, off, len);
    }
    
    public int read()
        throws IOException {
        return input.read();
    }
    
    public int available()
        throws IOException {
        return input.available();
    }
    
}
