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

import org.globus.ftp.HostPort;
import org.globus.ftp.Session;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSink;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.vanilla.BasicServerControlChannel;
import org.globus.net.SocketFactory;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Makes a connection to a remote data channel (FTPClient use only).
 **/
public class ActiveConnectTask extends Task {

    protected static Log logger =
        LogFactory.getLog(ActiveConnectTask.class.getName());

    protected HostPort hostPort;
    protected SocketBox mySocketBox;
    protected DataSink sink;
    protected DataSource source;
    protected BasicServerControlChannel control;
    protected Session session;
    protected DataChannelFactory factory;
    protected TransferContext context;
    
    public ActiveConnectTask(HostPort hostPort,
                             DataSink sink,
                             BasicServerControlChannel control,
                             Session session,
                             DataChannelFactory factory,
                             TransferContext context) {
        this.sink = sink;
        init(hostPort, control, session, factory, context);
    }
    
    public ActiveConnectTask(HostPort hostPort,
                             DataSource source,
                             BasicServerControlChannel control,
                             Session session,
                             DataChannelFactory factory,
                             TransferContext context) {
        this.source = source;
        init(hostPort, control, session, factory, context);
    }

    private void init(HostPort hostPort,
                      BasicServerControlChannel control,
                      Session session,
                      DataChannelFactory factory,
                      TransferContext context) {
        this.hostPort = hostPort;
        this.session = session;
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
                                        "active connection failed",
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
                                        "active connection to server failed",
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
        SocketBox sBox = new SimpleSocketBox();
        
        SocketFactory factory = SocketFactory.getDefault();
        Socket mySocket = factory.createSocket(this.hostPort.getHost(), 
                                               this.hostPort.getPort());
        sBox.setSocket(mySocket);
        
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
