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
 * Implements a simple identity authorization mechanism.
 * The given identify is compared with the peer's identity.
 */
public class IdentityAuthorization 
    extends GSSAuthorization {

    private static Log logger =
        LogFactory.getLog(IdentityAuthorization.class.getName());

    protected String _identity;
    
    /**
     * Constructor used by superclasses.
     */
    protected IdentityAuthorization() {
    }

    /**
     * Creates a new instance of this class with given
     * expected identity.
     *
     * @param identity the expected identity. Must not be null.
     */
    public IdentityAuthorization(String identity) {
	setIdentity(identity);
    }

    /**
     * Sets the expected identity for the authorization 
     * check.
     *
     * @param identity the expected identity. Must not be null.
     */
    public void setIdentity(String identity) {
	if (identity == null) {
	    throw new IllegalArgumentException("Identity cannot be null");
	}
	_identity = identity;
    }
    
    /**
     * Returns the expected identity.
     *
     * @return the expected identity.
     */
    public String getIdentity() {
	return _identity;
    }

    public GSSName getExpectedName(GSSCredential cred, String host) 
	throws GSSException {
        GSSManager manager = ExtendedGSSManager.getInstance();
        return manager.createName(_identity, null);
    }

    /**
     * Performs identity authorization. The given identity is compared
     * with the peer's identity.
     *
     * @param context the security context
     * @param host host address of the peer.
     * @exception AuthorizationException if the peer's
     *            identity does not match the expected identity.
     */
    public void authorize(GSSContext context, String host)
	throws AuthorizationException {
	logger.debug("Authorization: IDENTITY");

	try {
	    GSSName expected = getExpectedName(null, host);
	
	    GSSName target = null;
	    if (context.isInitiator()) {
		target = context.getTargName();
	    } else {
		target = context.getSrcName();
	    }
	    
	    if (!expected.equals(target)) {
		generateAuthorizationException(expected, target);
	    }
	} catch (GSSException e) {
	    throw new AuthorizationException("Authorization failure", e);
	}
    }
    
}
