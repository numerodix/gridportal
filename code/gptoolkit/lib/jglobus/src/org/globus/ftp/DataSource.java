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
 * Data channel uses this interface to read outgoing data.
 * Implement it to provide your own ways of reading data.
 * It must be thread safe; in parallel transfer mode several
 * streams may attempt to read.
 **/
public interface DataSource {

    /**
     * Reads a data buffer from this data source.
     *
     * @return The data buffer read. Null, if there is
     *         no more data to be read.
     * @throws IOException if an I/O error occurs.
     */
    public Buffer read()
       throws IOException;

    /**
     * Closes this data source and releases any system 
     * resources associated with this source.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close()
	throws IOException;

}
