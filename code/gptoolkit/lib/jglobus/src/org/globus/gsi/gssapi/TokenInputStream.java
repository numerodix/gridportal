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
package org.globus.gsi.gssapi;

import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used as token-oriented input stream needed for SSL library I/O abstraction.
 */
public class TokenInputStream extends InputStream {
    
    private static Log logger = 
	LogFactory.getLog(TokenInputStream.class.getName());

    private LinkedList tokens; // list of buffers 
    private byte [] buff; // current buffer
    private int index; // position within current buffer

    private boolean closed;

    public TokenInputStream() {
	this.tokens = new LinkedList();
	this.index = 0;
	this.closed = false;
    }
    
    // main function
    public void putToken(byte [] buf, int off, int len) {
	if (buf == null || len <=0) {
	    return;
	}

	if (logger.isDebugEnabled()) {
	    logger.debug("put token: " + len);
	}
	
	byte[] localBuf = buf;
	if (off != 0) {
	    localBuf = new byte[len];
	    System.arraycopy(buf, off, localBuf, 0, len);
	}

	synchronized(this) {
	    if (this.buff == null ||
		this.buff != null && this.buff.length == this.index) {
		this.buff = localBuf;
		this.index = 0;
	    } else {
		this.tokens.add(localBuf);
	    }
	    notify();
	}
    }

    public int read(byte [] data)
	throws IOException {
	return read(data, 0, data.length);
    }

    public int read(byte [] data, int off, int len) 
	throws IOException {
	if (logger.isDebugEnabled()) {
	    logger.debug("read byte array: " + len);
	}

	if (!checkData()) {
	    return -1;
	}

	int size = Math.min(len, buff.length-index);
	System.arraycopy(buff, index, data, off, size);
	index += size;

	return size;
    }

    public int read() 
	throws IOException {
	logger.debug("read byte");

	if (!checkData()) {
	    return -1;
	}

	return buff[index++] & 0xff;
    }

    protected synchronized boolean checkData() {
	try {
	    while(!hasData()) {
		wait();
		if (closed) {
		    return false;
		}
	    }
	} catch(Exception e) {
	    return false;
	}
	return true;
    }

    protected boolean hasData() {
	if (this.buff == null) {
	    return false;
	}
	
	if (this.buff.length == this.index) {
	    if (tokens.isEmpty()) {
		return false;
	    } else {
		this.buff  = (byte[])tokens.removeFirst();
		this.index = 0;
		return true;
	    }
	}
	return true;
    }

    public int available() 
	throws IOException {
	if (!hasData()) { 
	    return 0;
	} else {
	    return buff.length-index;
	}
    }

    public void close()
	throws IOException {
	logger.debug("close() called");
	synchronized(this) {
	    this.closed = true;
	    notify();
	}
    }
    
    public String toString() {
	return tokens.toString() + " " + index + " " + buff.length;
    }
    
}
