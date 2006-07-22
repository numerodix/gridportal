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

import com.sun.security.sasl.preview.SaslClientFactory;
import com.sun.security.sasl.preview.SaslClient;
import com.sun.security.sasl.preview.SaslException;

import java.util.Map;
import java.util.Hashtable;

import org.globus.mds.gsi.common.GSIMechanism;

import javax.security.auth.callback.CallbackHandler; // JAAS

/**
 * Client factory for Globus GSI.
 * The name of the mechanims should be changed, as GSSAPI specifically
 * appies to Kerberos V5. 
 */
public class ClientFactory implements SaslClientFactory {
    
    private static final String myMechs[] = { GSIMechanism.NAME };
    
    private static final int GSI = 0;
    
    public ClientFactory() {
    }
    
    public SaslClient createSaslClient(String[] mechs,
				       String authorizationId,
				       String protocol,
				       String serverName,
				       Hashtable props,
				       CallbackHandler cbh)
	throws SaslException {
	return createSaslClient(mechs, 
				authorizationId,
				protocol,
				serverName,
				(Map)props,
				cbh);
    }
    
    public SaslClient createSaslClient(String[] mechs,
				       String authorizationId,
				       String protocol,
				       String serverName,
				       Map props,
				       CallbackHandler cbh) 
	throws SaslException {
	
	for (int i = 0; i < mechs.length; i++) {
	    if (mechs[i].equals(myMechs[GSI])) {
		return new GSIMech(authorizationId, 
				   protocol, 
				   serverName,
				   props,
				   cbh);
	    }
	}
	return null;
    }
    
    /**
     * Returns an array of names of mechanisms that match the
     * specified mechanism selection policies.
     */
    public String[] getMechanismNames(Map props) {
        return (String[])myMechs.clone();
    }

    /**
     * Returns an array of names of mechanisms that match the 
     * specified mechanism selection policies.
     */
    public String[] getMechanismNames(Hashtable props) {
	return (String[])myMechs.clone();
    }
    
    /**
     * This function is replaced with the above one
     * in the latest RFC.
     */
    public String[] getMechanismNames() {
	return getMechanismNames(null);
    }
    
}
