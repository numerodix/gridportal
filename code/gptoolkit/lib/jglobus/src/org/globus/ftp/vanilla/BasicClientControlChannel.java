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
package org.globus.ftp.vanilla;

import org.globus.ftp.exception.FTPReplyParseException;
import java.io.IOException;
import org.globus.ftp.exception.ServerException;

/**
   Basic subset of client side control channel functionality, enough to
   implement the part of transfer after sending transfer command (RETR)
   up until receiving 200 reply.
 **/
public abstract class BasicClientControlChannel{

    public static final int WAIT_FOREVER = -1;

    public abstract Reply read() 
        throws ServerException, 
               IOException,
               FTPReplyParseException;

    /**
       Return when reply is waiting
     **/
    public void waitFor(Flag flag,
                        int waitDelay) 
        throws ServerException,
               IOException,
               InterruptedException {
        waitFor(flag, waitDelay, WAIT_FOREVER);
    }

    /**
       Block until reply is waiting in the control channel,
       or after timeout (maxWait), or when flag changes to true.
       If maxWait == WAIT_FOREVER, do not timeout.
       @param maxWait timeout in miliseconds
     **/

    public abstract void waitFor(Flag flag,
                                 int waitDelay,
                                 int maxWait)
        throws ServerException,
               IOException,
               InterruptedException;

    /*    public void write(Command cmd)
        throws IOException,
               IllegalArgumentException;
    */

    public abstract void abortTransfer();

} //FTPServerFacade



