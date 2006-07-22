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

import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSCredential;

/**
 * GSSAPI client authorization.
 */
public abstract class GSSAuthorization extends Authorization {
    
    /**
     * Returns expected <code>GSSName</code> used for authorization purposes.
     * Can returns null for self authorization.
     *
     * @param cred credentials used
     * @param host host address of the peer.
     * @exception GSSException if unable to create the name.
     */
    public abstract GSSName getExpectedName(GSSCredential cred, String host) 
	throws GSSException;
    
}
