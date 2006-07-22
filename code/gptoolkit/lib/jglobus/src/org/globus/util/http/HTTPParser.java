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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class HTTPParser {

    private static Log logger =
	LogFactory.getLog(HTTPParser.class.getName());

    protected String _server;
    protected String _host;
    protected String _contentType;
    protected String _connection;
    protected long _contentLength;
    protected boolean _chunked;
    protected LineReader _reader;
    
    public HTTPParser(InputStream is) 
	throws IOException {
	_contentLength = -1;
	_chunked = false;
	setInputStream(is);
	parse();
    }

    public String getContentType() {
	return _contentType;
    }

    public long getContentLength() {
	return _contentLength;
    }

    public boolean isChunked() {
	return _chunked;
    }
    
    public LineReader getReader() {
	return _reader;
    }
    
    public void setInputStream(InputStream in) {
	_reader = new LineReader(in);
    }
    
    public abstract void parseHead(String line) 
	throws IOException;
    
    /**
     * Parses the typical HTTP header.
     * @exception IOException if a connection fails or bad/incomplete request
     */
    protected void parse() 
	throws IOException {
	
	String line;
	line = _reader.readLine();
	if (logger.isTraceEnabled()) {
	    logger.trace(line);
	}
	parseHead(line);

	while ( (line = _reader.readLine()).length() != 0 ) {
	    if (logger.isTraceEnabled()) {
		logger.trace(line);
	    }
	    
	    if (line.startsWith(HTTPProtocol.CONNECTION)) {
		_connection = getRest(line, HTTPProtocol.CONNECTION.length());
	    } else if (line.startsWith(HTTPProtocol.SERVER)) {
		_server = getRest(line, HTTPProtocol.SERVER.length());
	    } else if (line.startsWith(HTTPProtocol.CONTENT_TYPE)) {
		_contentType = getRest(line, HTTPProtocol.CONTENT_TYPE.length());
	    } else if (line.startsWith(HTTPProtocol.CONTENT_LENGTH)) {
		_contentLength = Long.parseLong(getRest(line, 
							HTTPProtocol.CONTENT_LENGTH.length()));
	    } else if (line.startsWith(HTTPProtocol.HOST)){
		_host = getRest(line, HTTPProtocol.HOST.length());
	    } else if (line.startsWith(HTTPProtocol.CHUNKED)) {
		_chunked = true;
	    }
	    
	}
    }
    
    protected static final String getRest(String line, int index) {
	return line.substring(index).trim();
    }
    
}
