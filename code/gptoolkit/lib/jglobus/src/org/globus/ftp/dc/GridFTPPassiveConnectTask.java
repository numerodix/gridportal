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

import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSink;
import org.globus.ftp.vanilla.BasicServerControlChannel;
import org.globus.ftp.extended.GridFTPServerFacade;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   Unlike in the parent class, here we use authentication
   and protection.
 **/
public class GridFTPPassiveConnectTask extends PassiveConnectTask {

    protected static Log logger = 
        LogFactory.getLog(GridFTPPassiveConnectTask.class.getName());

    // alias to session
    GridFTPSession gSession;

    public GridFTPPassiveConnectTask(ServerSocket myServer, 
                                     DataSink sink,
                                     BasicServerControlChannel control,
                                     GridFTPSession session,
                                     DataChannelFactory factory,
                                     EBlockParallelTransferContext context)  {
        super(myServer, sink, control, session, factory, context);
        gSession = session;
    }
        
    public GridFTPPassiveConnectTask(ServerSocket myServer, 
                                     DataSource source,
                                     BasicServerControlChannel control,
                                     GridFTPSession session,
                                     DataChannelFactory factory,
                                     EBlockParallelTransferContext context) {
        super(myServer, source, control, session, factory, context);
        gSession = session;
    }
    
    protected SocketBox openSocket() throws Exception{
        
        logger.debug("server.accept()");

        Socket newSocket = myServer.accept();

        // set TCP buffer size
        if (gSession.TCPBufferSize != gSession.SERVER_DEFAULT) {
            logger.debug("setting socket's TCP buffer size to " 
                         + gSession.TCPBufferSize);
            newSocket.setReceiveBufferSize(gSession.TCPBufferSize);
            newSocket.setSendBufferSize(gSession.TCPBufferSize);
        }
        
        logger.debug("server.accept() returned");
        
        if (!gSession.dataChannelAuthentication.equals(
                           DataChannelAuthentication.NONE)) {
            logger.debug("authenticating");
            newSocket = GridFTPServerFacade.authenticate(newSocket,
                                                         false, // this is NOT client socket
                                                         gSession.credential,
                                                         gSession.dataChannelProtection,
                                                         gSession.dataChannelAuthentication);
        } else {
            // do not authenticate
            logger.debug("not authenticating");
        }

        // mark the socket as busy and store in the global socket pool
            
        ManagedSocketBox sBox = new ManagedSocketBox();
        sBox.setSocket(newSocket);
        sBox.setStatus(ManagedSocketBox.BUSY);
            
        if (session.transferMode != GridFTPSession.MODE_EBLOCK) {
                            
            // synchronize to prevent race condidion against
            // the section in GridFTPServerFacade.setTCPBufferSize
            synchronized (sBox) {
                sBox.setReusable(false);
            }
        }
        
        SocketPool socketPool = ((EBlockParallelTransferContext)context).getSocketPool();
        logger.debug("adding new socket to the pool");
        socketPool.add(sBox);
        logger.debug("available cached sockets: " + socketPool.countFree()  
                     + "; busy: " + socketPool.countBusy());

        return sBox;
        
    }

} // class
