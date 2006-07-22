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
   Represents an environment shared by all data channels
   performing the same transfer.
 **/
public interface TransferContext {
    
    /**
     * A thread can ask for a quit token to perform the operations associated with closing the transfer.
     * The token is interpreted as a permission to perform these operations. The reason for such policy
     * lies mainly in the nature of multithreaded transfer, where there are many transfer threads sharing
     * the same context, but the closing should be done only once.
       @return a non-null token is a permission for closing, null means no permission.
     **/
    public Object getQuitToken();
}
