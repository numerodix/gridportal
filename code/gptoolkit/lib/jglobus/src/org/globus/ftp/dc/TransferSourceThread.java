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
import org.globus.ftp.DataSource;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.vanilla.BasicServerControlChannel;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   Implements outgoing transfer.
   While the transfer is in progress, replies are sent to the
   local control channel. Also any failure messages go there
   in the form of a negative reply.
**/
public class TransferSourceThread extends TransferThread {

    protected static Log logger =
	LogFactory.getLog(TransferSourceThread.class.getName());
    
    protected DataChannelWriter writer;
    protected DataSource source;
    protected BasicServerControlChannel localControlChannel;
    protected TransferContext context;
    protected SocketBox socketBox = null;
    
    public TransferSourceThread(AbstractDataChannel dataChannel,
				SocketBox socketBox,
				DataSource source,
				BasicServerControlChannel localControlChannel,
				TransferContext context)
	throws Exception {
	this.socketBox = socketBox;
	this.source = source;
	this.localControlChannel = localControlChannel;
	this.context = context;
	this.writer = dataChannel.getDataChannelSink(context);
	logger.debug("using socket " + socketBox.getSocket().toString());
	writer.setDataStream(socketBox.getSocket().getOutputStream());
    }

    public void run() {
	Buffer buf;
	long transferred = 0;
	boolean error = false;
	logger.debug("TransferSourceThread executing");

	try {
	    startup();
			
	    try {
		while ((buf = source.read()) != null) {
		    transferred += buf.getLength();
		    writer.write(buf);
		}

		logger.debug("finished sending data; sent " + 
			     transferred + " bytes");

	    } catch (Exception e) {
		// this happens also if thread gets interrupted
		error = true;
		FTPServerFacade.exceptionToControlChannel(
				    e,
				    "exception during TransferSourceThread",
				    localControlChannel);
	    }

	    Object quitToken = shutdown();
	    
	    if (!error && (quitToken != null)) {
		//226 Transfer complete
		localControlChannel.write(new LocalReply(226));
	    }

	} catch (Exception e) {
	    FTPServerFacade.cannotPropagateError(e);
	}
    }

    protected void startup() {
	//send initial reply only if nothing has yet been sent
	synchronized(localControlChannel) {
	    if (localControlChannel.getReplyCount() == 0) {
		// 125 Data connection already open; transfer starting
		localControlChannel.write(new LocalReply(125));
	    }
	}
    }

    // called after the transfer completes, before 226
    protected Object shutdown() throws IOException {
	logger.debug("shutdown");

	// close the socket
	writer.close();
	
	// garbage collect the socket
	socketBox.setSocket(null);

	// attempt to obtain permission to close data source
	Object quitToken = context.getQuitToken();
	
	// data source is shared by all data channels,
	// so should be closed by the last one exiting
	if (quitToken != null) {
	    source.close();
	}
	
	return quitToken;
    }
    
    
}
