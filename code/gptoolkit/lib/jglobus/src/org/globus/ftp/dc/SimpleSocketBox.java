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

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleSocketBox implements SocketBox {

    private static Log logger = 
        LogFactory.getLog(SimpleSocketBox.class.getName());
    
    protected Socket socket;
    
    /**
     * @see org.globus.ftp.dc.SocketBox#setSocket(Socket)
     */
    public void setSocket(Socket newSocket) {
        if (newSocket == null) {
            logger.debug("Setting socket to null");
            closeSocket();
        } else {
            logger.debug("Setting socket");
        }
        this.socket = newSocket;
    }
    
    public Socket getSocket() {
        return this.socket;
    }

    private void closeSocket() {
        if (this.socket != null) {
            try { this.socket.close(); } catch (IOException e) {}
        }
    }

}
