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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   A container for Socket, making it available to pass a null socket reference.

   We use asynchronously active connect task to initialize socket,
   and active start transfer task to run it.
   We need to pass the socket reference, which is sometimes null
   (before initialization).
   This is a sane way to do it; a simple socket container.

   Additionally, the box contains a flag that states whether the socket 
   is currently in use, ie whether it is assigned to some data channel.
   It is needed in GridFTP for data channel reuse.
 **/

public class ManagedSocketBox extends SimpleSocketBox {

    private static Log logger = 
        LogFactory.getLog(ManagedSocketBox.class.getName());

    public static final int FREE = 1;
    public static final int BUSY = 2;
    
    public static final boolean REUSABLE = true;
    public static final boolean NON_REUSABLE = false;

    protected int status = FREE;
        
    // should the socket be reused? by default, yes
    protected boolean reusable = true;

    public ManagedSocketBox() {
    }

    public void setStatus(int s) {
        this.status = s;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setReusable(boolean r) {
        this.reusable = r;
    }
    
    public boolean isReusable() {
        return reusable;
    }   
    
}
