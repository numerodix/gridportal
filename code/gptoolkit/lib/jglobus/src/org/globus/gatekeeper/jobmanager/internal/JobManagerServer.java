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

import java.net.Socket;
import java.io.IOException;

import org.globus.net.BaseServer;
import org.globus.gatekeeper.jobmanager.JobManagerService;

import org.ietf.jgss.GSSCredential;

 /**
  * JobManagerServer extends the BaseServer abstract class to run as a server
  * listening for clients who will request information from the JobManager or
  * invoke actions for the JobManager.
  */
public class JobManagerServer extends BaseServer {

    protected JobManagerService _jobmanager = null;

    /**
     * initializes and starts the JobManagerServer with default credentials
     */
    public JobManagerServer() 
	throws IOException{
	super();
    }
    
    /**
     * initializes and starts the JobManagerServer
     * @param cred the credentials used by this server to authenticate itself with
     * clients
     */
    public JobManagerServer(GSSCredential cred) 
	throws IOException{
	super(cred, 0);
    }
    
    /**
     * initializes and starts the JobManagerServer
     * @param cred the credentials used by this server to authenticate itself with
     * clients
     * @param port
     */
    public JobManagerServer(GSSCredential cred, int port) 
	throws IOException {
	super(cred, port);
    }
    
    /**
     * sets the JobManager which will be used by this server
     * @param jm the jobmanager that will be used to request information or
     * invoke actions
     */
    public void setJobManager(JobManagerService jm){
	_jobmanager = jm;
    }
    
    /**
     * Sets the corresponding credentials for the server in order to verify
     * that it is serving the specific JobManager (proof of identity).
     * @param cred the credentials which will be used by this Server must be
     * the same as the JobManager
     */
    public void setCredentials(GSSCredential cred){
	this.credentials = cred;
    }
    
    /**
     * Method called after a connection has been established between the
     * client and the server. Handles the client request.
     * @param socket a connected socket to the client
     */
    protected void handleConnection(Socket socket) {
	new JobManagerClient(socket, 
			     _jobmanager);
    }
}
