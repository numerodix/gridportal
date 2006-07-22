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
package org.globus.io.urlcopy;

public interface UrlCopyListener {
  
    /**
     * This function is contniuosly called during url transfers.
     *
     * @param transferedBytes number of bytes currently trasfered
     *                        if -1, then performing thrid party transfer
     * @param totalBytes      number of total bytes to transfer
     *                        if -1, the total size in unknown.
     */
    public void transfer(long transferedBytes, long totalBytes);
    
    /**
     * This function is called only when an error occurs.
     *
     * @param exception  the actual error exception
     */
    public void transferError(Exception exception);

    /**
     * This function is called once the transfer is completed
     * either successfully or because of a failure. If an error occurred
     * during the transfer the transferError() function is called first.
     */ 
    public void transferCompleted();
    
}
