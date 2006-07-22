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

import java.io.OutputStream;
import java.io.IOException;

import org.globus.common.ChainedIOException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class GssOutputStream extends OutputStream {

    private static Log logger = 
	LogFactory.getLog(GssOutputStream.class.getName());

    protected OutputStream out;
    protected GSSContext context;

    protected boolean autoFlush = false;

    protected byte [] buff;
    protected int index;

    public GssOutputStream(OutputStream out, GSSContext context) {
	this(out, context, 16384);
    }

    public GssOutputStream(OutputStream out, GSSContext context, int size) {
	this.out = out;
	this.context = context;
	this.buff = new byte[size];
	this.index = 0;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    public boolean getAutoFlush() {
        return this.autoFlush;
    }

    public void write(int b)
        throws IOException {
	if (this.index == this.buff.length) {
	    flushData();
	}
	
        buff[index++] = (byte)b;

        if (this.autoFlush) {
            flushData();
        }
    }
    
    public void write(byte[] data)
	throws IOException {
	write(data, 0, data.length);
    }
    
    public void write(byte [] data, int off, int len) 
	throws IOException {
	int max;
	while (len > 0) {
	    if (this.index + len > this.buff.length) {
		max = (this.buff.length - this.index);
		System.arraycopy(data, off, this.buff, this.index, max);
		this.index += max;
		flushData();
		len -= max;
		off += max;
	    } else {
		System.arraycopy(data, off, this.buff, this.index, len);
		this.index += len;
                if (this.autoFlush) {
                    flushData();
                }
		break;
	    }
	}
    }

    protected byte[] wrap()
	throws IOException {
	try {
	    return context.wrap(this.buff, 0, this.index, null);
	} catch (GSSException e) {
	    throw new ChainedIOException("wrap failed", e);
	}
    }

    public abstract void flush() 
	throws IOException;

    private void flushData()
	throws IOException {
	flush();
	this.index = 0;
    }
    
    public void close()
	throws IOException {
	logger.debug("close");
	flushData();
	this.out.close();
    }

}
