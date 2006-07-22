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
package org.globus.gatekeeper;

import org.ietf.jgss.GSSCredential;

/**
 * Provides a simple interface is accessed by the
 * gatekeeper to forward service requests and handle
 * service-specific protocol replies.
 */
public interface Service {

    /**
     * Sets the credentials for the service.
     */
    public void setCredentials(GSSCredential cred);

    /**
     * Invokes the service with given request.
     */
    public void request(ServiceRequest request) 
	throws ServiceException;
    
    /**
     * Retrieves a handle to this service.
     */
    public String getHandle();
    
    public void setArguments(String [] args) 
	throws ServiceException;
    
    // these are for protocol specific stuff
    
    public String getRequestSuccessMessage();
    
    public String getRequestFailMessage(Exception e);
    
}
