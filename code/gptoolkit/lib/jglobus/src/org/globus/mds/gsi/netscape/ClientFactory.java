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

import com.netscape.sasl.SaslClientFactory;
import com.netscape.sasl.SaslClient;
import com.netscape.sasl.SaslException;

import java.util.Hashtable;

import org.globus.mds.gsi.common.GSIMechanism;

import javax.security.auth.callback.CallbackHandler;

/**
 * Client factory for Globus GSI.
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
  
  public String[] getMechanismNames() {
    return (String[])myMechs.clone();
  }
}
