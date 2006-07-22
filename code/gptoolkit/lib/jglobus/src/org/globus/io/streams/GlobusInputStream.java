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
package org.globus.io.streams;

import java.io.InputStream;
import java.io.IOException;

public abstract class GlobusInputStream extends InputStream {

    /**
     * Returns the total size of input data.
     *
     * @return -1 if size is unknown.
     */
    public long getSize() {
	return -1;
    }
    
    public int read() throws IOException {
	throw new IOException("Not implemented.");
    }

    /**
     * Aborts transfer. Usually makes sure to 
     * release all resources (sockets, file descriptors)
     * <BR><i>Does nothing by default.</i>
     */
    public void abort() {
	// FIXME: is this still used/needed?
    }
}
