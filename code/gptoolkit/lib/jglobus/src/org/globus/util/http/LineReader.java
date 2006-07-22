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
import java.io.FilterInputStream;

public class LineReader extends FilterInputStream {
    
    private static final int MAX_LEN = 16* 1024;

    protected int _charsRead    = 0;
    
    public LineReader(InputStream is) {
	super(is);
    }

    public InputStream getInputStream() {
	return in;
    }

    public int getCharsRead() {
	return _charsRead;
    }
    
    public String readLine()
	throws IOException {
	return readLine(in);
    }
    
    /**
     * Read a line of text from the given Stream and return it
     * as a String.  Assumes lines end in CRLF.
     * @param in a connected stream which contains the entire
     * message being sen.
     * @exception IOException if a connection fails or abnormal connection
     * termination.
     * @return the next line read from the stream.
     */
    protected String readLine(InputStream in) 
	throws IOException {
	StringBuffer buf = new StringBuffer();
	int c, length = 0;
	
	while(true) {
	    c = in.read();
	    if (c == -1 || c == '\n' || length > MAX_LEN) {
		_charsRead++;
		break;
	    } else if (c == '\r') {
		in.read();
		_charsRead+=2;
		break;
	    } else {
		buf.append((char)c);
		length++;
	    }
	}
	_charsRead += length;
	return buf.toString();
    }
}
