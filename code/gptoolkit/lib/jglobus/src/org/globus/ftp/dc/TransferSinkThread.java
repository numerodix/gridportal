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

import org.globus.ftp.Buffer;
import org.globus.ftp.DataSink;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.vanilla.BasicServerControlChannel;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   Implements incoming transfer.
   While the transfer is in progress, replies are sent to the
   local control channel. Also any failure messages go there
   in the form of a negative reply.
**/
public class TransferSinkThread extends TransferThread {

    protected static Log logger =
	LogFactory.getLog(TransferSinkThread.class.getName());
    
    protected DataChannelReader reader;
    protected DataSink sink;
    protected BasicServerControlChannel localControlChannel;
    protected TransferContext context;
    protected SocketBox socketBox;
    
    public TransferSinkThread(AbstractDataChannel dataChannel,
			      SocketBox socketBox,
			      DataSink sink,
			      BasicServerControlChannel localControlChannel,
			      TransferContext context)
	throws Exception {
	this.socketBox = socketBox;
	this.sink = sink;
	this.localControlChannel = localControlChannel;
	this.context = context;
	this.reader = dataChannel.getDataChannelSource(context);
	reader.setDataStream(socketBox.getSocket().getInputStream());
    }
    
    public void run() {
	boolean error = false;
	Object quitToken = null;
	logger.debug("TransferSinkThread executing");
	
	try {
	    startup();
	    
	    try {
		copy();
	    } catch (Exception e) {
		error = true;
		FTPServerFacade.exceptionToControlChannel(
				    e,
				    "exception during TransferSinkThread",
				    localControlChannel);
	    } finally {
		// attempt to obtain permission to close resources
		quitToken = context.getQuitToken();
		shutdown(quitToken);
	    }
	    
	    if (!error) {
		// local control channel is shared by all data channels
		// so only the last one exiting may send "226 transfer complete"
		if (quitToken != null) {
		    localControlChannel.write(new LocalReply(226));
		}
	    }
	    
	} catch (Exception e) {
	    // exception occurred when trying to write to local
	    // control channel. So there is no way to inform
	    // the user.
	    FTPServerFacade.cannotPropagateError(e);
	}
    }
    
    protected void startup() throws Exception {
	//send initial reply only if nothing has yet been sent
	synchronized(localControlChannel) {
	    if (localControlChannel.getReplyCount() == 0) {
		// 125 Data connection already open; transfer starting
		localControlChannel.write(new LocalReply(125));
	    }
	}
    }

    protected void copy() throws Exception {
	Buffer buf;
	long transferred = 0;
	
	while ((buf = reader.read()) != null) {
	    transferred += buf.getLength();
	    sink.write(buf);
	}
	logger.debug("finished receiving data; received " + 
		     transferred + " bytes");
    }

    protected void shutdown(Object quitToken) throws IOException {
	logger.debug("shutdown");

	reader.close();

        // garbage collect the socket
	socketBox.setSocket(null);
	
	// data sink is shared by all data channels,
	// so should be closed by the last one exiting
	if (quitToken != null) {
	    sink.close();
	}
    }
    
}
