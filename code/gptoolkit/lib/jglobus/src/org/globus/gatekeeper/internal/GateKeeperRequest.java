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
package org.globus.gatekeeper.internal;

import java.io.InputStream;
import java.io.IOException;

import org.globus.gatekeeper.ServiceRequest;
import org.globus.util.http.HTTPRequestParser;

public class GateKeeperRequest extends HTTPRequestParser implements ServiceRequest {
    
    private static final String PING = "ping";

    public GateKeeperRequest(InputStream in)
	throws IOException {
	super(in);
    }

    public InputStream getInputStream() {
	return getReader().getInputStream();
    }
    
    public boolean isPing() {
	return (_service != null && _service.regionMatches(true, 0, PING, 0, 4));
    }
    
    public String getService() {
	if (_service == null) return null;
	
	if (_service.charAt(0) == '/') {
	    return _service.substring(1);
	} else if (_service.regionMatches(true, 0, PING, 0, 4)) {
	    return _service.substring(5);
	} else {
	    return _service;
	}
    }
    
}
