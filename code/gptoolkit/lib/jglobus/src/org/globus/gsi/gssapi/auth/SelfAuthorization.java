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

import org.gridforum.jgss.ExtendedGSSManager;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSCredential;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performs the identity authorization check. The identity
 * is obtained from specified Globus credentials.
 */
public class SelfAuthorization 
    extends GSSAuthorization {

    private static Log logger =
	LogFactory.getLog(SelfAuthorization.class.getName());

    private static SelfAuthorization authorization;
    
    /**
     * Returns a single instance of this class.
     *
     * @return the instance of this class.
     */
    public synchronized static SelfAuthorization getInstance() {
	if (authorization == null) {
	    authorization = new SelfAuthorization();
	}
	return authorization;
    }

    public GSSName getExpectedName(GSSCredential cred, String host) 
	throws GSSException {
        if (cred == null) {
            GSSManager manager = ExtendedGSSManager.getInstance();
            cred = manager.createCredential(GSSCredential.INITIATE_AND_ACCEPT);
        }
        return cred.getName();
    }

    /**
     * Performs self authorization.
     */
    public void authorize(GSSContext context, String host)
	throws AuthorizationException {
	logger.debug("Authorization: SELF");
	
	try {
	    if (!context.getSrcName().equals(context.getTargName())) {
		GSSName expected = null;
		GSSName target = null;
		if (context.isInitiator()) {
		    expected = context.getSrcName();
		    target = context.getTargName();
		} else {
		    expected = context.getTargName();
		    target = context.getSrcName();
		}
		generateAuthorizationException(expected, target);
	    }
	} catch (GSSException e) {
	    throw new AuthorizationException("Authorization failure", e);
	}
    }
    
}
