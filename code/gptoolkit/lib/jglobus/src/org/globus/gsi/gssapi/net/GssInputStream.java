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
package org.globus.gsi.gssapi.net;

import java.io.InputStream;
import java.io.IOException;

import org.globus.common.ChainedIOException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

public abstract class GssInputStream extends InputStream {
  
    protected InputStream in;
    protected GSSContext context;
  
    protected byte [] buff;
    protected int index;

    public GssInputStream(InputStream in, GSSContext context) {
	this.in = in;
	this.context = context;
	this.buff = new byte[0];
	this.index = 0;
    }
  
    protected byte[] unwrap(byte [] msg) 
	throws IOException {
	try {
	    return this.context.unwrap(msg, 0, msg.length, null);
	} catch (GSSException e) {
	    throw new ChainedIOException("unwrap failed", e);
	}
    }
	
    protected abstract void readMsg()
	throws IOException;

    public int read(byte [] data)
	throws IOException {
	return read(data, 0, data.length);
    }

    public int read(byte [] data, int off, int len) 
	throws IOException {
	if (!hasData()) {
	    return -1;
	}

	int max = (index + len > buff.length) ? buff.length - index : len;

	System.arraycopy(buff, index, data, off, max);
	index += max;
	return max;
    }

    public int read() 
	throws IOException {
	if (!hasData()) {
	    return -1;
	}

	return buff[index++] & 0xff;
    }

    protected boolean hasData() 
	throws IOException {
	if (this.buff == null) {
	    return false;
	}
	if (this.buff.length == this.index) {
	    readMsg();
	}
	if (this.buff == null) {
            return false;
        }
	return (this.buff.length != this.index);
    }
    
    /* does not dispose of the context */
    public void close()
	throws IOException {
	this.buff = null;
	in.close();
    }

    public int available()
	throws IOException {
	if (this.buff == null) {
	    return -1;
	}
	int avail = this.buff.length - this.index;
	return (avail == 0) ? in.available() : avail;
    }
    
}
