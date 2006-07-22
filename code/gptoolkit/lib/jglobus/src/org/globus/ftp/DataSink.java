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

/**
 * Data channel uses this interface to write the incoming data.
 * Implement it to provide your own ways of storing data.
 * It must be thread safe; in parallel transfer mode several
 * streams may attempt to write.
 **/
public interface DataSink {
    
    /**
     * Writes the specified buffer to this data sink. <BR>
     * <i>Note: {@link Buffer#getOffset() buffer.getOffset()} might
     * return -1 if the transfer mode used does not support
     * data offsets, for example stream transfer mode.</i>
     *
     * @param buffer the data buffer to write. 
     * @throws IOException if an I/O error occurs.
     */
    public void write(Buffer buffer)
	throws IOException;
    
    /**
     * Closes this data sink and releases any system 
     * resources associated with this sink.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close()
	throws IOException;
    
}
