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
package org.globus.gatekeeper.internal;

import java.io.InputStream;
import java.io.IOException;

import org.globus.util.http.HTTPProtocol;

import org.globus.gatekeeper.GateKeeperException;
import org.globus.gatekeeper.BadRequestException;
import org.globus.gatekeeper.ServiceNotFoundException;
import org.globus.gatekeeper.AuthorizationException;

/**
 * This is the class responsible for preparing messages before they are sent to
 * the client using the HTTP protocol known as the GateKeeper Protocol.
 * GateKeeperProtocol implements ProtocolSend, which is an interface used by
 * the GateKeeper to send messages to client independant of the implementation.
 *
 */
public class GateKeeperProtocol extends HTTPProtocol {

    private static GateKeeperProtocol protocol = null;

    private static final String GATEKEEPER_APP = "application/x-globus-gram";

    public static GateKeeperProtocol getInstance(String prot) {
	if (protocol == null) {
	    protocol = new GateKeeperProtocol();
	}
	return protocol;
    }
    
    public String getErrorMessage(Exception e) {
	if (e instanceof BadRequestException) {
	    return getBadRequestErrorReply();
	} else if (e instanceof ServiceNotFoundException) {
	    return getFileNotFoundErrorReply();
	} else if (e instanceof AuthorizationException) {
	    return getForbiddenErrorReply();
	} else {
	    // default
	    return getServerErrorReply();
	}
    }
    
    public String getPingSuccessMessage() {
	return getOKReply(GATEKEEPER_APP);
    }

    public GateKeeperRequest parseRequest(InputStream in) 
	throws IOException, GateKeeperException {
	return new GateKeeperRequest(in);
    }
    
}
