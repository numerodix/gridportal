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
package org.globus.io.streams;

import java.io.IOException;
import java.net.Socket;

import org.globus.common.ChainedIOException;
import org.globus.io.gass.client.GassException;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.SelfAuthorization;

import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSContext;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSContext;

public class GassInputStream extends HTTPInputStream {

    private GSSCredential cred;
    private Authorization auth;

    /**
     * Opens Gass input stream in secure mode with default
     * user credentials.
     *
     * @param host host name of the gass server
     * @param port port number of the gass server
     * @param file file to retrieve from the server
     */
    public GassInputStream(String host,
			   int port,
			   String file) 
	throws GassException, GSSException, IOException {
	this(null, SelfAuthorization.getInstance(),
	     host, port, file);
    }
    
    /**
     * Opens Gass input stream in secure mode with specified
     * user credentials.
     *
     * @param cred user credentials to use
     * @param host host name of the gass server
     * @param port port number of the gass server
     * @param file file to retrieve from the server
     */
    public GassInputStream(GSSCredential cred,
			   Authorization auth,
			   String host,
			   int port,
			   String file) 
	throws GassException, GSSException, IOException {
	super();
	this.cred = cred;
	this.auth = auth;
	get(host, port, file);
    }

    protected Socket openSocket(String host, int port) 
	throws IOException {
	
	GSSManager manager = ExtendedGSSManager.getInstance();

	ExtendedGSSContext context = null;
	try { 
	    context = 
		(ExtendedGSSContext)manager.createContext(
                                       null,
				       GSSConstants.MECH_OID,
				       this.cred,
				       GSSContext.DEFAULT_LIFETIME
            );
	
	    context.setOption(GSSConstants.GSS_MODE, GSIConstants.MODE_SSL);
	} catch (GSSException e) {
	    throw new ChainedIOException("Security error", e);
	}
	
	GssSocketFactory factory = GssSocketFactory.getDefault();
	
	socket = factory.createSocket(host, port, context);

	((GssSocket)socket).setAuthorization(this.auth);
	
	return socket;
    }
    
}
