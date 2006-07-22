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
package org.globus.gsi.gssapi.net;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.globus.common.ChainedIOException;
import org.globus.net.WrappedSocket;
import org.globus.net.SocketFactory;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.SelfAuthorization;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class GssSocket extends WrappedSocket {

    private static Log logger = 
	LogFactory.getLog(GssSocket.class.getName());

    protected GSSContext context;
    protected boolean clientMode;

    protected InputStream in;
    protected OutputStream out;

    protected Authorization authorization = 
	SelfAuthorization.getInstance();

    public static final int SSL_MODE = 1;
    public static final int GSI_MODE = 2;
    
    protected int mode = -1;
    
    public GssSocket(String host, int port, GSSContext context)
	throws IOException {
	this(SocketFactory.getDefault().createSocket(host, port), context);
    }
    
    public GssSocket(Socket socket, GSSContext context) {
	super(socket);
	this.context = context;
	this.clientMode = true;
    }

    public void setAuthorization(Authorization auth) {
	this.authorization = auth;
    }

    public Authorization getAuthorization() {
	return this.authorization;
    }

    public void setUseClientMode(boolean clientMode) {
	this.clientMode = clientMode;
    }

    public boolean getClientMode() {
	return this.clientMode;
    }

    public void setWrapMode(int mode) {
	this.mode = mode;
    }
    
    public int getWrapMode() {
	return this.mode;
    }

    public GSSContext getContext() {
	return this.context;
    }
    
    abstract protected void writeToken(byte [] token)
	throws IOException;

    abstract protected byte[] readToken()
	throws IOException;
    
    protected synchronized void authenticateClient() 
	throws IOException, GSSException {
	
	byte [] outToken = null;
	byte [] inToken = new byte[0];
	
	while (!this.context.isEstablished()) {

	    outToken = 
		this.context.initSecContext(inToken, 0, inToken.length);
	    
	    if (outToken != null) {
		writeToken(outToken);
	    }
	    
	    if (!this.context.isEstablished()) {
		inToken = readToken();
	    }
	}
    }

    protected synchronized void authenticateServer() 
	throws IOException, GSSException {
	
	byte [] outToken = null;
	byte [] inToken = null;

	while (!this.context.isEstablished()) {
	    inToken = readToken();
	    
	    outToken = 
		this.context.acceptSecContext(inToken, 0, inToken.length);
	    
	    if (outToken != null) {
		writeToken(outToken);
	    }
	}
    }
    
    public synchronized void startHandshake()
	throws IOException {
	if (this.context.isEstablished()) return;
	logger.debug("Handshake start");
	
	try {
	    if (this.clientMode) {
		authenticateClient();
	    } else {
		authenticateServer();
	    }
	} catch (GSSException e) {
	    throw new ChainedIOException("Authentication failed", e);
	}

	logger.debug("Handshake end");
	if (this.authorization != null) {
	    logger.debug("Performing authorization.");
	    this.authorization.authorize(this.context, 
					 getInetAddress().getHostAddress());
	} else {
	    logger.debug("Authorization not set");
	}
    }

    public synchronized OutputStream getOutputStream() 
	throws IOException {
	startHandshake();
	return this.out;
    }
    
    public synchronized InputStream getInputStream() 
	throws IOException {
	startHandshake();
	return this.in;
    }

    /**
     * Disposes of the context and closes the connection
     */
    public void close() 
	throws IOException {
	try {
	    this.context.dispose();
	} catch (GSSException e) {
	    throw new ChainedIOException("dispose failed.", e);
	} finally {
	    this.socket.close();
	}
    }

}
