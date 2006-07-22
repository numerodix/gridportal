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
package org.globus.ftp.dc;

import java.io.InputStream;
import java.io.IOException;

import org.globus.ftp.Buffer;

public class StreamImageDCReader implements DataChannelReader {

    public static final int BUF_SIZE = 2048;

    protected int bufferSize = BUF_SIZE;
    protected InputStream input;

    public void setDataStream(InputStream in) {
	input = in;
    }
    
    public Buffer read()
	throws IOException {
	byte[] bt = new byte[bufferSize];
	int read = input.read(bt);
	if (read == -1) {
	    return null;
	} else {
	    return new Buffer(bt, read);
	}
    }
    
    public void close() 
	throws IOException {
	input.close();
    }

}
