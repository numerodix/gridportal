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
import javax.security.sasl.SaslClientFactory;

import java.util.Map;
import java.util.Hashtable;

import javax.security.auth.callback.CallbackHandler; // JAAS

public class SaslClientFactoryWrapper implements SaslClientFactory {

    private com.sun.security.sasl.preview.SaslClientFactory factory;

    public SaslClientFactoryWrapper() {
        this(new ClientFactory());
    }

    public SaslClientFactoryWrapper(com.sun.security.sasl.preview.SaslClientFactory factory) {
        this.factory = factory;
    }


    public SaslClient createSaslClient(String[] mechs,
                                       String authorizationId,
                                       String protocol,
                                       String serverName,
                                       Map props,
                                       CallbackHandler cbh) 
        throws SaslException {
        try {
            com.sun.security.sasl.preview.SaslClient client =
                this.factory.createSaslClient(mechs, 
                                              authorizationId,
                                              protocol, 
                                              serverName,
                                              toHashtable(props),
                                              cbh);
            return (client == null) ? null : new SaslClientWrapper(client);
        } catch (com.sun.security.sasl.preview.SaslException e) {
            throw new SaslException("", e);
        }
    }
    
    public String[] getMechanismNames(Map props) {
        return this.factory.getMechanismNames(toHashtable(props));
    }

    private static Hashtable toHashtable(Map props) {
        if (props == null) {
            return null;
        }
        if (props instanceof Hashtable) {
            return (Hashtable)props;
        } else {
            return new Hashtable(props);
        }
    }
    
}
