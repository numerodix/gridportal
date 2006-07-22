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
package org.globus.util.http;

import java.io.InputStream;
import java.io.IOException;

public class HTTPChunkedInputStream extends InputStream {

    protected byte[] _buf;
    protected int _index;
    protected int _max;
    protected boolean _eof;
    protected InputStream _in;
    
    public HTTPChunkedInputStream(InputStream in) {
	_in = in;
	// initial buf size - will adjust automatically
	_buf = new byte[2048]; 
	_index = 0;
	_max = 0;
	_eof = false;
    }
    
    /* only called when the buffer is empty */
    private void readChunk() 
	throws IOException {

	String line = readLine(_in).trim();
	int length  = Integer.parseInt(line, 16);

	if (length > 0) {
	    
	    // make sure the chunk will fit into the buffer
	    
	    if (length > _buf.length) {
		_buf = new byte[length];
	    } 

	    int bytesLeft = length;
	    int reqBytes = 0;
	    int off = 0;
	    int read = 0;

	    /*  multiple reads might be necessary to load
		the entire chunk */
	    while (bytesLeft != 0) {
		reqBytes = bytesLeft;
		read = _in.read(_buf, off, reqBytes);
		if (read == -1) break;
		bytesLeft -= read;
		off += read;
	    }

	    _max = off;
	    _index = 0;
	    
	} else {
	    // end of data indicated
	    _eof = true;
	}
	
	_in.read(); // skip CR
	_in.read(); // skip LF
	
    }
    
    /**
     * Read a line of text from the given Stream and return it
     * as a String.  Assumes lines end in CRLF.
     */
    private String readLine(InputStream in) throws IOException {
	StringBuffer buf = new StringBuffer();
	int c, length = 0;
    
	while(true) {
	    c = in.read();
	    if (c == -1 || c == '\n' || length > 512) {
		break;
	    } else if (c == '\r') { 
		in.read(); 
		return buf.toString();
	    } else {      
		buf.append((char)c);
		length++;       
	    }
      
	}
	return buf.toString();
    }

    public int read(byte [] buffer, int off, int len) 
	throws IOException {
	if (_eof) return -1;
	if (_max == _index) readChunk();
	
	if (_index + len <= _max) {
	    // that's easy
	    System.arraycopy(_buf, _index, buffer, off, len);
	    _index += len;
	    return len;
	} else {
	    int maximum = _max - _index;
	    System.arraycopy(_buf, _index, buffer, off, maximum);
	    _index += maximum;
	    int read = read(buffer, off+maximum, len-maximum);
	    if (read == -1) {
		return maximum;
	    } else {
		return maximum + read;
	    }
	}
    }
    
    public int read()
	throws IOException {
	if (_eof) return -1;
	if (_max == _index) readChunk();
	return _buf[_index++] & 0xff;
    }

    public int available()
        throws IOException {
        return _in.available();
    }
    
    public void close() 
	throws IOException {
	_in.close();
    }
    
}
