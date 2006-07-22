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
package org.globus.gsi.gssapi.net.impl;

import java.net.Socket;
import java.io.IOException;

import org.globus.gsi.gssapi.net.GssSocket;

import org.ietf.jgss.GSSContext;

public class GSIGssSocket extends GssSocket {

    public GSIGssSocket(String host, int port, GSSContext context)
	throws IOException {
	super(host, port, context);
    }

    public GSIGssSocket(Socket socket, GSSContext context) {
	super(socket, context);
    }

    public void setWrapMode(int mode) {
	this.mode = mode;
    }

    public int getWrapMode() {
	return this.mode;
    }

    protected void writeToken(byte [] token)
	throws IOException {
	if (this.out == null) {
	    if (this.mode == -1) {
		if (this.in != null) {
		    this.mode = ((GSIGssInputStream)in).getWrapMode();
		}
	    }
	    this.out = new GSIGssOutputStream(this.socket.getOutputStream(), 
					      this.context,
					      this.mode);
	}
	((GSIGssOutputStream)this.out).writeToken(token);
    }
    
    protected byte[] readToken()
	throws IOException {
	if (this.in == null) {
	    this.in = new GSIGssInputStream(this.socket.getInputStream(),
					    this.context);
	}
	return ((GSIGssInputStream)this.in).readHandshakeToken();
    }
    
}
