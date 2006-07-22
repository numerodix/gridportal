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

/**
   Represents a data channel reader or writer
   aware of being in one of a pool of asynchronous
   data channels
 **/
public class EBlockAware {

    public static final int 
	EOF = 64,
	EOD = 8,
	WILL_CLOSE = 4;
    
    protected EBlockParallelTransferContext context;
    

    public void setTransferContext(EBlockParallelTransferContext context) {
	this.context = context;
    }

}
