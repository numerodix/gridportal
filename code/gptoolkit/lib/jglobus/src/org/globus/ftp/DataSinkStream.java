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
package org.globus.ftp;

import java.io.IOException;
import java.io.OutputStream;

/**
   Reference implementation of DataSink. It can be used with non-parallel transfers.
   It cannot be used with Extended Block Mode because it uses implicit assumption 
   that data arrives in correct sequence. It is not thread safe.  
 **/
public class DataSinkStream implements DataSink {

    protected OutputStream out;
    protected boolean autoFlush;
    protected boolean ignoreOffset;
    protected long offset = 0;

    public DataSinkStream(OutputStream out) {
	this(out, false, false);
    }

    public DataSinkStream(OutputStream out,
			  boolean autoFlush,
			  boolean ignoreOffset) {
	this.out = out;
	this.autoFlush = autoFlush;
	this.ignoreOffset = ignoreOffset;
    }

    public void write(Buffer buffer)
	throws IOException {
	long bufOffset = buffer.getOffset();
	if (ignoreOffset ||
	    bufOffset == -1 ||
	    bufOffset == offset) {
	    out.write(buffer.getBuffer(), 0, buffer.getLength());
	    if (autoFlush) {
		out.flush();
	    }
	    offset += buffer.getLength();
	} else {
	    throw new IOException("Random offsets not supported.");
	}
    }
    
    public void close()
	throws IOException {
	out.close();
    }
    
}
