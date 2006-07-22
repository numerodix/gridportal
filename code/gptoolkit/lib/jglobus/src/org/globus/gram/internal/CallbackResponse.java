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
package org.globus.gram.internal;

import java.io.InputStream;
import java.io.IOException;

public class CallbackResponse extends GatekeeperReply {
    
    protected String httpMethod  = null;
    protected String callbackURL = null;
    
    public CallbackResponse(InputStream in) throws IOException {
	super(in);
    }

    public void parseHttp(String line) {
	
	int p1 = line.indexOf(" ");
	if (p1 == -1) {
	    return;
	}

	httpMethod = line.substring(0, p1);

	int p2 = line.indexOf(" ", p1+1);
	if (p2 == -1) {
	    return;
	}

	callbackURL = line.substring(p1+1, p2);
	
	int p3 = line.indexOf(" ", p2+1);
	if (p3 == -1) {
	    return;
	}
	
	httpType    = line.substring(p2+1);
    }
    
    public String toString() {
	StringBuffer buf = new StringBuffer();
	
	buf.append("HttpMethod : " + httpMethod + "\n");
	buf.append("URL        : " + callbackURL + "\n");
	buf.append(super.toString());
	
	return buf.toString();
    }
}
	


