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
package org.globus.net;

import java.net.Socket;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.globus.common.ChainedIOException;
import org.globus.util.http.HTTPProtocol;
import org.globus.util.http.HTTPResponseParser;
import org.globus.util.http.HTTPChunkedInputStream;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssSocketFactory;

import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSContext;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSContext;

public class GSIHttpURLConnection extends GSIURLConnection {

    public static final int PORT = 8443;

    private Socket socket;
    private int port;

    private HTTPResponseParser response;
    private InputStream is;

    public GSIHttpURLConnection(URL u) {
	super(u);
    }
    
    public synchronized void connect() 
	throws IOException {

	if (this.connected) {
	    return;
	} else {
	    this.connected = true;
	}
	
	this.port = (url.getPort() == -1) ? PORT : url.getPort();

	GSSManager manager = ExtendedGSSManager.getInstance();

	ExtendedGSSContext context = null;
	
	try {
	    context = 
		(ExtendedGSSContext)manager.createContext(getExpectedName(),
							  GSSConstants.MECH_OID,
							  this.credentials,
							  GSSContext.DEFAULT_LIFETIME);
	    
	    switch (this.delegationType) {
	    
	    case GSIConstants.DELEGATION_NONE:
		context.requestCredDeleg(false);
		break;
	    case GSIConstants.DELEGATION_LIMITED:
		context.requestCredDeleg(true);
		context.setOption(GSSConstants.DELEGATION_TYPE,
				  GSIConstants.DELEGATION_TYPE_LIMITED);
		break;
	    case GSIConstants.DELEGATION_FULL:
		context.requestCredDeleg(true);
		context.setOption(GSSConstants.DELEGATION_TYPE,
				  GSIConstants.DELEGATION_TYPE_FULL);
		break;
	    default:
		context.requestCredDeleg(true);
		context.setOption(GSSConstants.DELEGATION_TYPE,
				  new Integer(this.delegationType));
		
	    }
	    
            if (this.gssMode != null) {
                context.setOption(GSSConstants.GSS_MODE,
                                  gssMode);
            }

	} catch (GSSException e) {
	    throw new ChainedIOException("Failed to init GSI context", e);
	}

	GssSocketFactory factory = GssSocketFactory.getDefault();
	
	socket = factory.createSocket(url.getHost(), this.port, context);

	((GssSocket)socket).setAuthorization(authorization);
    }
    
    public void disconnect() {
	if (socket != null) {
	    try { socket.close(); } catch (Exception e) {}
	}
    }

    public synchronized InputStream getInputStream()
	throws IOException {

	if (is == null) {
	    connect();

	    OutputStream out = socket.getOutputStream();
	    InputStream in = socket.getInputStream();
	    
	    String msg = 
		HTTPProtocol.createGETHeader(url.getFile(),
					     url.getHost() + ":" + this.port,
					     "Java-Globus-GASS-HTTP/1.1.0");
	    
	    out.write( msg.getBytes() );
	    out.flush();

	    response = new HTTPResponseParser(in);

	    if (!response.isOK()) {
		throw new IOException(response.getMessage());
	    }

	    if (response.isChunked()) {
		is = new HTTPChunkedInputStream(in);
	    } else {
		is = in;
	    }
	}
	
	return is;
    }
    
    public String getHeaderField(String name) {
	if (response == null) {
	    return null;
	}
	
	if (name.equalsIgnoreCase("content-type")) {
	    return response.getContentType();
	} else if (name.equalsIgnoreCase("content-length")) {
	    return String.valueOf(response.getContentLength());
	} else {
	    return null;
	}
    }

}
