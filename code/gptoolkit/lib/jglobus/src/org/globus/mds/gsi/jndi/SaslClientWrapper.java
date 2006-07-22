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
package org.globus.mds.gsi.jndi;


import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

import javax.security.auth.callback.CallbackHandler;  // from JAAS

import java.util.Map;

import org.globus.mds.gsi.common.GSIMechanism;

import org.ietf.jgss.GSSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SaslClientWrapper implements SaslClient {
   
    private com.sun.security.sasl.preview.SaslClient client;

    public SaslClientWrapper(com.sun.security.sasl.preview.SaslClient client) {
        this.client = client;
    }

    public boolean hasInitialResponse() {
        return this.client.hasInitialResponse();
    }
    
    public byte[] evaluateChallenge(byte[] challengeData) throws SaslException {
        try {
            return this.client.evaluateChallenge(challengeData);
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new SaslException("", e);
        }
    }

    public byte[] wrap(byte[] outgoing,
                       int offset,
                       int len)
        throws SaslException {
        try {
            return this.client.wrap(outgoing, offset, len);
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new SaslException("", e);
        }
    }

    public byte[] unwrap(byte[] incoming,
                         int offset,
                         int len) 
        throws SaslException {
        try {
            return this.client.unwrap(incoming, offset, len);
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new SaslException("", e);
        }
    }

    public void dispose() 
        throws SaslException {
        try {
            this.client.dispose();
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new SaslException("", e);
        }
    }
    
    public Object getNegotiatedProperty(String propName) {
        try {
            return client.getNegotiatedProperty(propName);
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isComplete() {
        return this.client.isComplete();
    }

    public String getMechanismName() {
        return this.client.getMechanismName();
    }
    
}
