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

import java.io.InterruptedIOException;
import java.io.IOException;

import org.globus.ftp.MarkerListener;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransferState {

    private static Log logger = 
	LogFactory.getLog(TransferState.class.getName());

    private int transferDone;
    private int transferStarted;
    private Exception transferException = null;

    private MarkerListener listener;

    public TransferState(MarkerListener listener) {
	this.transferDone = 0;
	this.transferStarted = 0;
	this.transferException = null;
	this.listener = listener;
    }

    // this is called when transfer successfully started (opening data conn)
    public synchronized void transferStarted() {
	this.transferStarted++;
	notifyAll();
    }

    // this is called when TransferMonitor thread is finished
    public synchronized void transferDone() {
	this.transferDone++;
	notifyAll();
    }

    // this is called when an error occurs during transfer
    public synchronized void transferError(Exception e) {
	logger.debug("intercepted exception: " + e.getClass().getName());
	if (transferException == null) {
	    transferException = e;
	} else if (transferException instanceof InterruptedException
		   || transferException instanceof InterruptedIOException) { 
	    //if one of the threads throws an error, it interrupts
	    //the other thread (by InterruptedException).
	    //Here we make sure that transferException will store the
	    //primary failure reason, not the resulting InterruptedException
	    transferException = e;
	}
	notifyAll();
    }

    /**
     * Blocks until the transfer is complete or 
     * the transfer fails.
     */
    public synchronized void waitForEnd() 
	throws ServerException,
	       ClientException,
	       IOException {
	if (this.transferDone >= 2) {
	    checkError();
	    return;
	}
	try {
	    while(this.transferDone != 2) {
		wait();
	    }
	} catch(Exception e) {
	}
	checkError();
    }

    /**
     * Blocks until the transfer begins or
     * the transfer fails to start.
     */
    public synchronized void waitForStart() 
	throws ServerException,
	       ClientException,
	       IOException {
	if (this.transferStarted >= 2) {
	    checkError();
	    return;
	}
	try {
	    while(this.transferStarted != 2 &&
		  this.transferException == null) {
		wait();
	    } 
	} catch(Exception e) {
	}
	checkError();
    }
    
    public synchronized boolean hasError() {
	return (transferException != null);
    }

    public Exception getError() {
	return transferException;
    }

    public void checkError()
	throws ServerException,
	       ClientException,
	       IOException {
	if (transferException == null) {
	    return;
	}
	if (transferException instanceof ServerException) {
	    throw (ServerException)transferException;
	} else if (transferException instanceof IOException) {
	    throw (IOException)transferException;
	} else if (transferException instanceof InterruptedException) {
	    throw new ClientException(ClientException.THREAD_KILLED);
	}
    }
    
}
