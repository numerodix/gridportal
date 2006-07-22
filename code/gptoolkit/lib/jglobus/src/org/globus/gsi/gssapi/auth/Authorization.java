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
package org.globus.gsi.gssapi.auth;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSName;

/**
 * Interface for authorization mechanisms.
 * The authorization is performed once the connection was authenticated.
 */
public abstract class Authorization {
    
    /**
     * Performes authorization checks. Throws 
     * <code>AuthorizationException</code> if the authorization fails.
     * Otherwise, the function completes normally.
     *
     * @param context the securit context
     * @param host host address of the peer.
     * @exception AuthorizationException if the peer is
     *            not authorized to access/use the resource.
     */
    public abstract void authorize(GSSContext context, String host) 
	throws AuthorizationException;


    protected void generateAuthorizationException(GSSName expected,
						  GSSName target)
	throws AuthorizationException {

	String lineSep = System.getProperty("line.separator");
	StringBuffer msg = new StringBuffer();
	msg.append("Mutual authentication failed").append(lineSep)
	    .append("  Expected target subject name=\"")
	    .append(expected.toString()).append("\"")
	    .append(lineSep)
	    .append("  Target returned subject name=\"")
	    .append(target.toString())
	    .append("\"");
	throw new AuthorizationException(msg.toString()); 
    }
    
}
