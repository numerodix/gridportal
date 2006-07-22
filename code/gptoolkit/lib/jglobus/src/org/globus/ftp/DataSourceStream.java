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
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
   Reference implementation of DataSource. It can be used with non-parallel transfers.
   It cannot be used with Extended Block Mode because it is not thread safe.
 **/

public class DataSourceStream implements DataSource {

    static Log logger = LogFactory.getLog(DataSourceStream.class.getName());

    private static final int DEFAULT_BUFFER_SIZE = 16384;

    protected InputStream in;
    protected int bufferSize;
    long totalRead = 0;

    public DataSourceStream(InputStream in) {
	this(in, DEFAULT_BUFFER_SIZE);
    }
    
    public DataSourceStream(InputStream in, int bufferSize) {
	this.in = in;
	this.bufferSize = bufferSize;
    }

    public Buffer read()
	throws IOException {
	byte [] buf = new byte[bufferSize];
	int read = in.read(buf);
	if (read == -1) {
	    return null;
	} else {
	    Buffer result =  new Buffer(buf, read, totalRead);
	    totalRead += read;
	    logger.debug("totalRead: " + totalRead);
	    return result;
	}
    }
    
    public void close()
	throws IOException {
	in.close();
    }
    
}
