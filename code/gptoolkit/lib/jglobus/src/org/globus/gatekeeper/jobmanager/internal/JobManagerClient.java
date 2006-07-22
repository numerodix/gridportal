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

import org.globus.gatekeeper.jobmanager.JobManagerService;
import org.globus.gatekeeper.jobmanager.JobManagerException;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * JobManagerClient is a Thread that has a connection to a client and will handle
 * the request that the client sends to the JobManager.
 * supported requests are:<p>
 * status<p>
 * register <callbackurl> <job-state-mask><p>
 * unregister <callbackurl><p>
 * cancel <p>
 */
public class JobManagerClient extends Thread {

    private Socket _socket = null;
    private JobManagerService _jobManager = null;
    private InputStream _input = null;
    private OutputStream _output = null;

    private JobManagerProtocol _jobManagerProtocol = null;

    /**
     * Creates an instance of the HandleClientRequest and immediately
     * starts running the Thread to handle the client's request.
     * @param socket must be connected to the client.
     * @param jm the JobManager for which the client is connected to.
     */
    public JobManagerClient(Socket socket, 
			    JobManagerService jm) {
	_socket = socket;
	_jobManager = jm;
	_jobManagerProtocol = JobManagerProtocol.getInstance("GRAM1.0");
	start();
    }

    /**
     * executes an internal method to handle the client request and response.
     */
    public void run(){
	try {
	    _input = _socket.getInputStream();
            _output = _socket.getOutputStream();
	    
	    _jobManagerProtocol.handleRequest(_jobManager, _input);
	    sendOKMessage();
	    
	} catch(JobManagerException e) {
	    sendFailureMessage(e);
	} catch(IOException e) {
	    e.printStackTrace();
	} catch(Exception e) {
	    sendFailureMessage(e);
	} finally {
	    close();
	}
    }
    
    private void close() {
        if (_output != null) {
            try { _output.close(); } catch(Exception e) {}
        }
        if (_input != null) {
            try { _input.close(); } catch(Exception e) {}
        }
        if (_socket != null) {
            try { _socket.close(); } catch(Exception e) {}
        }
    }

    private void write(String msg) {
        if (msg == null) return;
        try {
            _output.write(msg.getBytes());
        } catch(Exception e) {
        }
    }
    
    /**
     * sends an okay message to the client
     */
    private void sendOKMessage(){
	write(_jobManagerProtocol.getRequestReply(_jobManager.getStatus(),
						  _jobManager.getFailureCode()));
    }
    
    /**
     * sends a failure message to the client
     */
    private void sendFailureMessage(Exception e){
	e.printStackTrace();
	write(_jobManagerProtocol.getErrorMessage(e));
    }
    
}
