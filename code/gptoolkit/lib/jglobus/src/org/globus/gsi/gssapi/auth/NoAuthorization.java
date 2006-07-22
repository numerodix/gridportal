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
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the simplest authorization mechanism that does
 * not do any authorization checks.
 */
public class NoAuthorization extends Authorization {

    private static Log logger =
	LogFactory.getLog(NoAuthorization.class.getName());

    private static NoAuthorization authorization;
    
    /**
     * Returns a single instance of this class.
     *
     * @return the instance of this class.
     */
    public synchronized static NoAuthorization getInstance() {
	if (authorization == null) {
	    authorization = new NoAuthorization();
	}
	return authorization;
    }
    
    /**
     * Always returns null.
     */
    public GSSName getExpectedName(GSSCredential cred, String host) 
	throws GSSException {
        return null;
    }

    /**
     * Performs no authorization checks. The function is always
     * successful. It does not throw any exceptions.
     *
     */
    public void authorize(GSSContext context, String host)
	throws AuthorizationException {
	logger.debug("Authorization: NONE");
    }
    
}
