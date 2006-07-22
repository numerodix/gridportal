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

public class HTTPResponseParser extends HTTPParser {

    protected String _httpType;
    protected int _httpCode;
    protected String _httpMsg;

    public HTTPResponseParser(InputStream is) 
	throws IOException {
	super(is);
    }

    public String getMessage() {
	return _httpMsg;
    }

    public int getStatusCode() {
	return _httpCode;
    }

    public boolean isOK() {
	return (_httpCode == 200);
    }

    public void parseHead(String line) 
	throws IOException {
	int st = line.indexOf(" ");
	if (st == -1) {
	    throw new IOException("Bad HTTP header");
	}
	_httpType = line.substring(0, st);
	
	st++;
	int et = line.indexOf(" ", st);
	if (et == -1) {
	    throw new IOException("Bad HTTP header");
	}
	
	try {
	    _httpCode = Integer.parseInt(line.substring(st, et).trim());
	} catch(Exception e) {
	    throw new IOException("Bad HTTP header");
	}
	
	et++;
	_httpMsg = line.substring(et);
    }
    
}
