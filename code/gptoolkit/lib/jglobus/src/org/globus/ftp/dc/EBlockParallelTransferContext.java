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

/*
This context has two functions. First, it keeps tracks of EODs. Second, it has the pool
of the available sockets.
 */
public class EBlockParallelTransferContext 
    implements TransferContext {

    protected static Log logger = LogFactory.getLog(EBlockParallelTransferContext.class.getName()); 

    protected SocketPool socketPool;

	protected Object quitToken = new Object();
	
	// since the threadmanager won't change during one transfer,
	// the context is being used as a reference holder for transferThreadManger
	private TransferThreadManager transferThreadManager;
	
    public static final int UNDEFINED = -1;
    /**
       if sending data, this is interpreted as the number of EODS
       sent. If receiving data, this is the number of EODS received.
     **/
    protected int eodsTransferred = 0;
    /**
       if sending data, this is the total number of EODS we should send.
       if receiving data, this is the total number of EODS we are expecting.
     **/
    protected int eodsTotal = UNDEFINED;

	
    synchronized public void eodTransferred() {
	eodsTransferred ++;
    }

    synchronized public int getEodsTransferred() {
	return eodsTransferred;
    }

    synchronized public void setEodsTotal(int total) {
	eodsTotal = total;
    }

    synchronized public int getEodsTotal() {
	return eodsTotal;
    }

    /**
       release the token if and only if (all EODS have been sent, or all EODS have been 
       received), and the token has not been released yet.
       So this method will return non-null only one in the instance's lifetime.
     **/
    synchronized public Object getQuitToken() {
	logger.debug("checking if ready to quit");
	logger.debug("eodsTotal = " + eodsTotal + "; eodsTransferred = " + eodsTransferred);
	if (eodsTotal != UNDEFINED &&
		eodsTransferred == eodsTotal) {
			// ready to release the quit token. But make sure not to do it twice.
			// This section only returns non-nul the first time it is entered.
			Object myToken = quitToken;
			quitToken = null;
			return myToken;
		} else {
			// not ready to quit yet
			return null;
		}		
    }

    synchronized public void setSocketPool(SocketPool sp) {
	this.socketPool = sp;
    }

    synchronized public SocketPool getSocketPool() {
	return this.socketPool;
    }

	public void setTransferThreadManager(TransferThreadManager transferThreadManager) {
		this.transferThreadManager = transferThreadManager;
	}

	public TransferThreadManager getTransferThreadManager() {
		return transferThreadManager;
	}


}

