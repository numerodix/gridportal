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

import org.ietf.jgss.GSSContext;

import org.globus.gsi.gssapi.net.GssSocketFactory;

public class GSIGssSocketFactory extends GssSocketFactory {
  
    public Socket createSocket(Socket s, 
			       String host,
			       int port, 
			       GSSContext context) {
	return new GSIGssSocket(s, context);
    }

    public Socket createSocket(String host, 
			       int port, 
			       GSSContext context)
	throws IOException {
	return new GSIGssSocket(host, port, context);
    }
    
}
