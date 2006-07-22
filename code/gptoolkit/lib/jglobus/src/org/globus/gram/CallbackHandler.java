/*
 * $Id: CallbackHandler.java,v 1.29 2005/04/18 15:35:52 gawor Exp $
 */

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
package org.globus.gram;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Hashtable;

import org.globus.net.BaseServer;
import org.globus.gsi.gssapi.auth.SelfAuthorization;
import org.globus.gsi.gssapi.auth.AuthorizationException;
import org.globus.gram.internal.CallbackResponse;
import org.globus.gram.internal.GRAMProtocol;

import org.ietf.jgss.GSSCredential;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The <code>Server</code> class acts as a basic multi-threaded HTTPS
 * server.
 *
 * @version $Revision: 1.29 $
 */
public class CallbackHandler extends BaseServer {

    /** Registered jobs */
    private Hashtable _jobs;

    /**
     * Construct a GRAM callback handler with default user
     * credentials. Port will be dynamically assigned.
     */
    public CallbackHandler() 
	throws IOException {
	super(null, 0);
	init();
    }

    /**
     * Construct a GRAM callback handler with specifed credentials
     * and given port.
     *
     * @param cred
     *         credentials to use. if null default
     *         user credentials will be used
     * @param port 
     *         server port to listen on. if set to 0
     *         the port will be dynamically assigned
     */
    public CallbackHandler(GSSCredential cred, int port) 
	throws IOException {
	super(cred, port);
	init();
    }

    private void init() {
	_jobs = new Hashtable();
	super.initialize();
	setAuthorization(SelfAuthorization.getInstance());
    }

    /**
     * Registers gram job to listen for status updates
     * @param job gram job
     */
    public void registerJob(GramJob job) {
	String id = job.getIDAsString();
	_jobs.put(id, job);
    }
    
    /**
     * Unregisters gram job from listening to status updates
     * @param job gram job
     */
    public void unregisterJob(GramJob job) {
	String id = job.getIDAsString();
	_jobs.remove(id);
    }

    protected GramJob getJob(String url) {
	return (GramJob)_jobs.get(url);
    }
  
    /**
     * Returns number of registered jobs
     * @return int number of jobs
     */
    public int getRegisteredJobsSize() {
	return _jobs.size();
    }

    public String getURL() {
        if (url == null) {
            StringBuffer buf = new StringBuffer();
            buf.append(getProtocol()).
                append("://").
                append(getHost()).
                append(":").
                append(String.valueOf(getPort())).
		append("/").
		append(String.valueOf(System.currentTimeMillis()));
            url = buf.toString();
        }
        return url;
    }

    protected void handleConnection(Socket socket) {
	GramCallbackHandler gcb = new GramCallbackHandler(this, 
							  socket);
	(new Thread(gcb)).start();
    }
    
}

class GramCallbackHandler implements Runnable {

    private static Log logger =
	LogFactory.getLog(GramCallbackHandler.class.getName());

    private CallbackHandler handler;
    private Socket socket;
    
    public GramCallbackHandler(CallbackHandler handler, 
			       Socket socket) {
	this.handler = handler;
	this.socket = socket;
    }
    
    /**
     * Listen on the server socket for a client, start another thread to
     * keep listening on the server socket, then deal with the client.
     */
    public void run() {

	BufferedWriter out = null;

	try {
	    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    try {

		CallbackResponse hd = 
		    new CallbackResponse( socket.getInputStream() );
		
		if (hd.protocolVersion != GRAMProtocol.GRAM_PROTOCOL_VERSION) {
		    throw new Exception("Gram callback protocol version mismatch");
		}

		GramJob job = handler.getJob( hd.jobManagerUrl );
		if (job == null) {
		    throw new Exception("Not registered with this handler: " + 
					hd.jobManagerUrl);
		}

		job.setError( hd.failureCode );
		job.setStatus( hd.status );
		
		if (job.getStatus() == GramJob.STATUS_DONE || 
		    job.getStatus() == GramJob.STATUS_FAILED) {
		    handler.unregisterJob(job);
		}
	    
		try {
		    out.write(GRAMProtocol.OKReply());
		    out.flush();
		} catch(IOException ignoreE) {
		    logger.debug("Ignoring IOException");
		}

	    } catch(AuthorizationException ex) {
		logger.debug("Authorization failed", ex);
		out.write(GRAMProtocol.ErrorReply(401, "Authorization Failed"));
		out.flush();
	    } catch (Exception ex) {
		logger.debug("General error", ex);
		out.write(GRAMProtocol.ErrorReply(400, ex.getMessage()));
		out.flush();
	    }
	} catch (IOException e) {
	    logger.debug("IO Error", e);
	} finally {
	    try {
		socket.close();
	    } catch (IOException e) { }
	}
    }
    
}
