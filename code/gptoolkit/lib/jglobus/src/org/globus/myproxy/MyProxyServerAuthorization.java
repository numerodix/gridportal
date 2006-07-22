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
package org.globus.myproxy;

import org.ietf.jgss.GSSContext;

import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.globus.gsi.gssapi.auth.AuthorizationException;

/**
 * Implements the MyProxy server authorization mechanism.
 */
public class MyProxyServerAuthorization
    extends Authorization {
    
    private HostAuthorization authzHostService, authzMyProxyService;

    public MyProxyServerAuthorization() {
        this.authzMyProxyService = new HostAuthorization("myproxy");
        this.authzHostService = HostAuthorization.getInstance();
    }

    /**
     * Performs MyProxy server authorization checks. The hostname of
     * the server is compared with the hostname specified in the
     * server's (topmost) certificate in the certificate chain. The
     * hostnames must match exactly (in case-insensitive way). The
     * service in the certificate may be "host" or "myproxy".
     * <code>AuthorizationException</code> if the authorization fails.
     * Otherwise, the function completes normally.
     *
     * @param context the security context.
     * @param host host address of the peer.
     * @exception AuthorizationException if the peer is
     *            not authorized to access/use the resource.
     */
    public void authorize(GSSContext context, String host) 
        throws AuthorizationException {
        try {
            this.authzMyProxyService.authorize(context, host);
        } catch (AuthorizationException e) {
            this.authzHostService.authorize(context, host);
        }
    }
}
