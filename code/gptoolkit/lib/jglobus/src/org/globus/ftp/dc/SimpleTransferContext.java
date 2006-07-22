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
   transfer context for single threaded transfers
   using 1 data channel.
 **/
public class SimpleTransferContext 
    implements TransferContext {


    private static SimpleTransferContext singleton = new SimpleTransferContext();

    /**
       return the default instance of this class
     **/
    public static TransferContext getDefault() {
	return singleton;
    }

    /**
       @return always non-null
     **/
    public Object getQuitToken() {
	return new Object();
    }


}
