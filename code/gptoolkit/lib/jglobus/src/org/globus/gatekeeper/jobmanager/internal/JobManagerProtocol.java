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
package org.globus.gatekeeper.jobmanager.internal;

import java.io.InputStream;
import java.io.IOException;

import org.globus.util.http.HTTPProtocol;
import org.globus.util.http.HTTPRequestParser;
import org.globus.gatekeeper.BadRequestException;
import org.globus.gatekeeper.ServiceRequest;
import org.globus.gatekeeper.jobmanager.JobManagerService;
import org.globus.gatekeeper.jobmanager.JobManagerException;
import org.globus.gram.internal.GRAMProtocol;

public class JobManagerProtocol extends HTTPProtocol {

    private static JobManagerProtocol protocol = null;

    private static final String STATUS                = "status: ";
    private static final String JOB_MANAGER_URL       = "job-manager-url: ";
    private static final String FAILURE_CODE          = "failure-code: ";

    public static JobManagerProtocol getInstance(String prot) {
	if (protocol == null) {
	    protocol = new JobManagerProtocol();
	}
	return protocol;
    }
    
    public JobRequestParser handleJobRequest(ServiceRequest request) {
	if (request instanceof HTTPRequestParser) {
	    return new JobRequestParser( ((HTTPRequestParser)request).getReader() );
	} else {
	    return new JobRequestParser( request.getInputStream() );
	}
    }
    
    public void handleRequest(JobManagerService jobManager, InputStream in)
	throws IOException, JobManagerException {
	new JobManagerRequest(jobManager, in);
    }
    
    public String getErrorMessage(Exception e) {
	if (e instanceof JobManagerException) {
	    return getRequestReply( ((JobManagerException)e).getErrorCode(), null);
	} else if (e instanceof BadRequestException) {
	    return getBadRequestErrorReply();
	} else {
	    return getRequestReply(47 /*Gatekeeper misconfigured*/, null);
	}
    }
    
    public String getRequestReply(int status, String jobManagerUrl) {
	StringBuffer msg  = new StringBuffer();
	
	msg.append(GRAMProtocol.PROTOCOL_VERSION_LINE)
	    .append(STATUS)
	    .append(String.valueOf(status))
	    .append(CRLF);
	
	if (jobManagerUrl != null){
	    msg.append(JOB_MANAGER_URL)
		.append(jobManagerUrl)
		.append(CRLF);
	}
	
	return getOKReply(GRAMProtocol.APPLICATION, msg.toString());
    }

    public String getRequestReply(int status,
				  int failureCode) {
        StringBuffer msg  = new StringBuffer();

        msg.append(GRAMProtocol.PROTOCOL_VERSION_LINE)
            .append(STATUS)
            .append(String.valueOf(status))
            .append(CRLF)
            .append(FAILURE_CODE)
            .append(failureCode)
            .append(CRLF);

        return getOKReply(GRAMProtocol.APPLICATION, msg.toString());
    }

    public String getStatusUpdateMessage(String callbackUrl,
					 String jobManagerUrl,
					 String host,
					 int status,
					 int failureCode) {
	StringBuffer msg  = new StringBuffer();

	msg.append(GRAMProtocol.PROTOCOL_VERSION_LINE)
	    .append(JOB_MANAGER_URL)
	    .append(jobManagerUrl)
	    .append(CRLF)
	    .append(STATUS)
	    .append(String.valueOf(status))
	    .append(CRLF)
	    .append(FAILURE_CODE)
	    .append(failureCode)
	    .append(CRLF);
	
	return createHTTPHeader(callbackUrl, 
				host, 
				GRAMProtocol.APPLICATION,
				msg);
    }
}
