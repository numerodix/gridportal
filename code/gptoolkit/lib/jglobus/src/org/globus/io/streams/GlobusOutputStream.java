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

import java.io.OutputStream;
import java.io.IOException;

public abstract class GlobusOutputStream extends OutputStream {

    /**
     * Aborts transfer. Usually makes sure to
     * release all resources (sockets, file descriptors)
     * <BR><i>Does nothing by default.</i>
     */
    public void abort() {
        // FIXME: is this still used/needed?
    }

    public void write(int b)
	throws IOException {
	throw new IOException("Not implemented.");
    }
    
}
