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
package org.globus.mds.gsi.netscape;

import com.netscape.sasl.SaslClient;
import com.netscape.sasl.SaslException;

import javax.security.auth.callback.CallbackHandler; // JAAS

import java.util.Map;

import org.globus.mds.gsi.common.GSIMechanism;

import org.ietf.jgss.GSSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GSIMech extends GSIMechanism implements SaslClient {

    private static Log logger = 
	LogFactory.getLog(GSIMech.class.getName());

    public GSIMech(String authzID, String protocol, String serverName,
		   Map props, CallbackHandler cbh) 
	throws SaslException {

	if (props != null) {
	    // Max receive buffer size
	    String prop = (String)props.get(MAX_BUFFER);
	    if (prop != null) {
		try {
		    recvMaxBufSize = Integer.parseInt(prop);
		} catch (NumberFormatException e) {
		    throw new SaslException("Property must be string representation of integer: " + MAX_BUFFER);
		}
	    }
	    
	    // Max send buffer size
	    prop = (String)props.get(MAX_SEND_BUF);
	    if (prop != null) {
		try {
		    sendMaxBufSize = Integer.parseInt(prop);
		} catch (NumberFormatException e) {
		    throw new SaslException("Property must be string representation of integer: " + MAX_SEND_BUF);
		}
	    }
	    
	}
	
	try {
	    init(serverName, props);
	} catch(Exception e) {
	    throw new SaslException("Failed to initialize.", e);
	}
    }
    
    /**
     * Netscape specific function.
     */
    public byte[] createInitialResponse() throws SaslException {
	try {
	    return context.initSecContext(null, 0, 0);
	} catch (GSSException e) {
	    throw new SaslException("createInitialRespose failed", e);
	}
    }
    
    /**
     * Processes the challenge data.
     * 
     * The server sends a challenge data using which the client must
     * process using GSS_Init_sec_context.
     * As per RFC 2222, when GSS_S_COMPLETE is returned, we do
     * an extra handshake to determine the negotiated security protection
     * and buffer sizes.
     *
     * @param challengeData A non-null byte array containing the
     * challenge data from the server.
     * @return A non-null byte array containing the response to be
     * sent to the server.
     */
    public byte[] evaluateChallenge(byte[] challengeData) throws SaslException {
	
	if (challengeData == null) {
	    logger.debug("SASL received null challenge data. Returning empty array.");
	    return new byte[0];
	}

	byte [] token = null;
	try {
	    token = exchangeData(challengeData);
	} catch (GSSException e) {
	    throw new SaslException("evaluateChanllenge failed", e);
	} catch (Exception e) {
	    throw new SaslException("evaluateChanllenge failed", e);
	}
	return token;

    }
  
}
