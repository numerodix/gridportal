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

import java.net.URL;
import java.net.URLConnection;

import org.globus.gsi.GSIConstants;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.GSSAuthorization;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;

public abstract class GSIURLConnection extends URLConnection {

    public static final String GSS_MODE_PROPERTY = 
        "gssMode";

    protected GSSCredential credentials;
    protected Authorization authorization;
    protected int delegationType;
    protected Integer gssMode;

    /**
     * Subclasses must overwrite.
     */
    protected GSIURLConnection(URL url) {
	super(url);
	this.delegationType = GSIConstants.DELEGATION_NONE;
	this.authorization = null; // no authorization?
    }

    public abstract void disconnect();

    public void setGSSMode(Integer mode) {
        this.gssMode = mode;
    }
    
    public Integer getGSSMode() {
        return this.gssMode;
    }

    public void setCredentials(GSSCredential credentials) {
        this.credentials = credentials;
    }

    public GSSCredential getCredentials() {
        return credentials;
    }

    public void setAuthorization(Authorization auth) {
        authorization = auth;
    }

    public Authorization getAuthorization() {
        return authorization;
    }
    
    public void setDelegationType(int delegationType) {
	this.delegationType = delegationType;
    }

    public int getDelegationType() {
	return delegationType;
    }

    protected GSSName getExpectedName() throws GSSException {
        if (this.authorization instanceof GSSAuthorization) {
            GSSAuthorization auth = (GSSAuthorization)this.authorization;
            return auth.getExpectedName(this.credentials,
                                        this.url.getHost());
        } else {
            return null;
        }
    }

    public void setRequestProperty(String key, String value) {
        if (key.equals(GSS_MODE_PROPERTY)) {
            if (value.equals("ssl")) {
                setGSSMode(GSIConstants.MODE_SSL);
            } else if (value.equals("gsi")) {
                setGSSMode(GSIConstants.MODE_GSI);
            } else {
                setGSSMode(null);
            }
        } else {
            super.setRequestProperty(key, value);
        }
    }
    
    
}
