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

import org.globus.ftp.Session;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSink;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.vanilla.BasicServerControlChannel;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   This task will wait on the local server for the new incoming connection 
   and when it comes it will start a new transfer thread on the new connection.
   It is little tricky: it will cause data channel to start
   a new thread. By the time this task completes, the new
   thread is running the transfer.

   Any resulting exceptions are piped to the local control channel.

 **/
public class PassiveConnectTask extends Task {

    protected static Log logger =
        LogFactory.getLog(PassiveConnectTask.class.getName());

    protected ServerSocket myServer;
    protected SocketBox mySocketBox;
    protected DataSink sink;
    protected DataSource source;
    protected BasicServerControlChannel control;
    protected Session session;
    protected DataChannelFactory factory;
    protected TransferContext context;
    
    public PassiveConnectTask(ServerSocket myServer,
                              DataSink sink,
                              BasicServerControlChannel control,
                              Session session,
                              DataChannelFactory factory,
                              TransferContext context) {
        this.sink = sink;
        init(myServer, control, session, factory, context);
    }
    
    public PassiveConnectTask(ServerSocket myServer,
                              DataSource source,
                              BasicServerControlChannel control,
                              Session session,
                              DataChannelFactory factory,
                              TransferContext context) {
        this.source = source;
        init(myServer, control, session, factory, context);
    }

    private void init(ServerSocket myServer,
                      BasicServerControlChannel control,
                      Session session,
                      DataChannelFactory factory,
                      TransferContext context) {
        if (!(session.serverMode == Session.SERVER_PASSIVE
              || session.serverMode == GridFTPSession.SERVER_EPAS)) {
            throw new IllegalStateException();
        }
        
        if (myServer == null) {
            throw new IllegalArgumentException("server is nul");
        }
        this.session = session;
        this.myServer = myServer;
        this.control = control;
        this.factory = factory;
        this.context = context;
    }

    public void execute() {
        try {
            DataChannel dataChannel = null;
            mySocketBox = null;
            try {
                mySocketBox = openSocket();
            } catch (Exception e) {
                FTPServerFacade.exceptionToControlChannel(
                                        e,
                                        "server.accept() failed",
                                        control);
                return;
            }

            try {
                dataChannel = factory.getDataChannel(session, mySocketBox);
                if (sink != null) {
                    logger.debug("starting sink data channel");
                    dataChannel.startTransfer(sink, control, context);
                } else if (source != null) {
                    logger.debug("starting source data channel");
                    dataChannel.startTransfer(source, control, context);
                } else {
                    logger.error("not set");
                }

            } catch (Exception e) {
                FTPServerFacade.exceptionToControlChannel(
                                        e,
                                        "startTransfer() failed: ",
                                        control);
                if (dataChannel != null) {
                    dataChannel.close();
                }
            }

        } catch (Exception e) {
            FTPServerFacade.cannotPropagateError(e);
        }
    }

    /**
       Override this to implement authentication
    **/
    protected SocketBox openSocket() throws Exception {
        logger.debug("server.accept()");
        
        SocketBox sBox = new SimpleSocketBox();
        Socket newSocket = myServer.accept();
        sBox.setSocket(newSocket);
        
        return sBox;
    }
    
    private void close() {
        // server will by closed by the FTPServerFacade.
        //try { server.close(); } catch (Exception ignore) {}
    }
    
    public void stop() {
        close();
    }
}
