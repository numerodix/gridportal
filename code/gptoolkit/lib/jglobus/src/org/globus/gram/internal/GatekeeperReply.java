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

import org.globus.util.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GatekeeperReply extends HttpResponse {

    private static Log logger =
	LogFactory.getLog(GatekeeperReply.class.getName());

    public int protocolVersion   = -1;
    public int status            = -1;
    public String jobManagerUrl  = null;
    public int failureCode       = -1;
    public int jobFailureCode    = -1;

    public GatekeeperReply(InputStream in) throws IOException {
	super(in);    
	charsRead = 1;
	if (contentLength > 0) myparse();
    }

    protected void myparse() throws IOException {
	
	String line, tmp;
	
        while(charsRead < contentLength) {
            line = readLine(input);
	    if (line.length() == 0) break;

	    if (logger.isTraceEnabled()) {
		logger.trace(line);
	    }

            tmp = getRest(line.trim());   
	    
	    if (line.startsWith("protocol-version:")) {
	      protocolVersion = Integer.parseInt(tmp);
	    } else if (line.startsWith("status:")) {
		status = Integer.parseInt(tmp);
	    } else if (line.startsWith("job-manager-url:")) {
		jobManagerUrl = tmp;
	    } else if (line.startsWith("failure-code:")) {
		failureCode = Integer.parseInt(tmp);
	    } else if (line.startsWith("job-failure-code:")) {
		jobFailureCode = Integer.parseInt(tmp);
	    }
	}
    }

    public String toString() {
	StringBuffer buf = new StringBuffer();

	buf.append(super.toString());
	
	buf.append("Protocol-version : " + protocolVersion + "\n");
	buf.append("Status           : " + status);
	if (jobManagerUrl != null) {
	    buf.append("\nJob-manager-url  : " + jobManagerUrl);
	}
	if (failureCode >= 0) {
	    buf.append("\nFailure-code     : " + failureCode);
	}
	if (jobFailureCode >= 0) {
            buf.append("\nJob failure code     : " + jobFailureCode);
        }
	
	return buf.toString();
    }
}
	



