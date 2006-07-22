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

import java.io.ByteArrayOutputStream;

import org.globus.ftp.Buffer;

public class AsciiTranslator { 

    public static final byte[] CRLF = {'\r', '\n'};

    protected boolean possibleBreak = false;
    
    /* enables checking for \r\n */
    protected boolean rnSep;
    /* enables checking for \n */
    protected boolean nSep;
    
    protected byte[] lineSep;

    protected static byte[] systemLineSep;

    static {
	systemLineSep = System.getProperty("line.separator").getBytes();
    }

    /**
     * Output tokens with system specific line separators
     */
    public AsciiTranslator(boolean rnSep, 
			   boolean nSep) {
	this(rnSep, nSep, systemLineSep);
    }

    public AsciiTranslator(boolean rnSep, 
			   boolean nSep,
			   byte[] lineSeparator) {
	this.rnSep = rnSep;
	this.nSep = nSep;
	this.lineSep = lineSeparator;
    }

    public Buffer translate(Buffer buffer) {
	// TODO: This can be optimized if destination line separator
	// is the same
	byte[] buf = buffer.getBuffer();
	int len = buffer.getLength();
	int bufLastPos = 0;
	
	ByteArrayOutputStream byteArray = new ByteArrayOutputStream(len);

	if (possibleBreak) {
	    if (len > 0 && buf[0] == '\n') {
		byteArray.write(lineSep, 0, lineSep.length);
		bufLastPos = 1;
	    } else {
		byteArray.write('\r');
	    }
	    possibleBreak = false;
	}

	byte ch;
	for (int i=bufLastPos;i<len;i++) {
	    ch = buf[i];
	    if (rnSep && ch == '\r') {
		if (i+1 == len) {
		    // TODO: must test this condition but it might be rare
		    // append everything to the buffer except last byte
		    byteArray.write(buf, bufLastPos, i - bufLastPos);
		    bufLastPos = len;
		    possibleBreak = true;
		    break; 
		}

		if (buf[i+1] == '\n') {
		    i++;
		    byteArray.write(buf, bufLastPos, i - 1 - bufLastPos);
		    byteArray.write(lineSep, 0, lineSep.length);
		    bufLastPos = ++i;
		}
	    } else if (nSep && ch == '\n') {
		byteArray.write(buf, bufLastPos, i - bufLastPos);
		byteArray.write(lineSep, 0, lineSep.length);
		bufLastPos = i+1;
	    }
	}

	if (bufLastPos < len) {
	    byteArray.write(buf, bufLastPos, len - bufLastPos);
	}
	
	byte [] newBuf = byteArray.toByteArray();
	return new Buffer(newBuf, newBuf.length, buffer.getOffset());
    }
    
}

