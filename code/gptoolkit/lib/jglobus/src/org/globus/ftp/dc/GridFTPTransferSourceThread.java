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

import org.globus.ftp.DataSource;
import org.globus.ftp.vanilla.BasicServerControlChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GridFTPTransferSourceThread extends TransferSourceThread {

    protected static Log logger =
        LogFactory.getLog(GridFTPTransferSourceThread.class.getName());

    // utility alias to context
    protected EBlockParallelTransferContext eContext;

    public GridFTPTransferSourceThread(
                          AbstractDataChannel dataChannel,
                          SocketBox socketBox,
                          DataSource source,
                          BasicServerControlChannel localControlChannel,
                          EBlockParallelTransferContext context)
        throws Exception {
        super(dataChannel, socketBox, source, localControlChannel, context);
        this.eContext = context;
    }

    protected void startup() {
        //update manager's thread count
        TransferThreadManager threadManager =
            eContext.getTransferThreadManager();
        threadManager.transferThreadStarting();

        //send initial reply only if nothing has yet been sent
        synchronized(localControlChannel) {
            if (localControlChannel.getReplyCount() == 0) {
                // 125 Data connection already open; transfer starting
                localControlChannel.write(new LocalReply(125));
            }
        }
    }

    // called after the transfer completes, before 226
    protected Object shutdown() throws java.io.IOException {

        // send EOD and maybe EOF
        writer.endOfData();

        // attempt to obtain permission to close data source
        Object quitToken = context.getQuitToken();

        SocketPool pool =
            ((EBlockParallelTransferContext) context).getSocketPool();

        if (((ManagedSocketBox) socketBox).isReusable()) {
            // we're in EBLOCK mode. Store the socket for later reuse
            logger.debug("shutdown; leaving the socket open");
            pool.checkIn(socketBox);
        } else {
            // we're in stream mode or other non-eblock,
            // or it was indicated that socket should not be reused.
            logger.debug("shutdown; closing the socket");
            writer.close();

            // do not reuse the socket
            pool.remove(socketBox);
            socketBox.setSocket(null);
        }

        // data sink is shared by all data channels,
        // so should be closed by the last one exiting
        if (quitToken != null) {
            logger.debug("closing the data source");
            source.close();
        }

        //update manager's thread count
        TransferThreadManager threadManager =
            eContext.getTransferThreadManager();
        threadManager.transferThreadTerminating();
        
        return quitToken;
    }

}
