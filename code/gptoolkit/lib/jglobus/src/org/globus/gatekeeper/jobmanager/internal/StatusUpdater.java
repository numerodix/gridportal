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

import java.util.LinkedList;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.globus.util.GlobusURL;
import org.globus.util.http.HTTPResponseParser;
import org.globus.gram.internal.GRAMConstants;

import org.globus.gsi.GSIConstants;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.auth.SelfAuthorization;

import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSContext;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;

import org.apache.log4j.Logger;

public class StatusUpdater implements Runnable {

    private static final int SO_TIMEOUT = 60 * 1000;
    private static final int PAUSE = 5000;
    private static final int MAX_TRIES = 3;

    private boolean stop = false;

    private LinkedList list = null;

    private JobManagerProtocol _myProtocol = null;

    private Logger _logger;

    public StatusUpdater() {
	list = new LinkedList();
	_myProtocol = JobManagerProtocol.getInstance("");
    }

    public void setLogger(Logger logger) {
        _logger = logger;
    }
    
    public void start() {
	Thread t = new Thread(this);
	t.start();
    }

    public void stop() {
	stop = true;
	synchronized(list) {
            list.notify();
        }
    }

    class Status {
	public GSSCredential _cred;
	public GlobusURL _url;
	public int _status;
	public int _failureCode;
	public String _jmurl;

	public Status(GSSCredential cred, GlobusURL url,
		      int status, int failureCode, String jmurl) {
	    _cred = cred;
	    _url = url;
	    _status = status;
	    _failureCode = failureCode;
	    _jmurl = jmurl;
	}
    }

    public void updateStatus(GSSCredential cred, GlobusURL url, 
			     int status, int failureCode, String jmurl) {
	synchronized(list) {
	    list.add(new Status(cred, url, status, failureCode, jmurl));
	    list.notify();
	}
    }

    private boolean isDone() {
	synchronized(list) {
	    return (stop && list.isEmpty());
	}
    }
    
    /**
     * connect to a server listening for the update.  Sends the update and if
     * it replies an okay message than
     */
    public void run() {

	stop = false;

	_logger.info("[statusUpdater] running...");

	GSSManager manager = ExtendedGSSManager.getInstance();
	GssSocketFactory factory = GssSocketFactory.getDefault();
	
	while(!isDone()) {

	    Status req = null;
	    synchronized(list) {
		try {
		    while( list.isEmpty() ) {
			list.wait();
			if (stop) break;
		    }
		} catch(InterruptedException e) {
		    // interrupted!
		    break;
		}
		if (isDone()) break;
		req = (Status)list.removeFirst();
	    }

	    for (int tries=0;tries<MAX_TRIES;tries++) {

		if (_logger.isDebugEnabled()) {
		    _logger.debug("[statusUpdater]: status update: " + req._url.getURL() + " " + 
				  req._status + " " +
				  req._failureCode);
		}
		
		GssSocket s = null;
		OutputStream os = null;
		InputStream is = null;
		
		try {

		    ExtendedGSSContext context = 
			(ExtendedGSSContext)manager.createContext(null,
								  GSSConstants.MECH_OID,
								  req._cred,
								  GSSContext.DEFAULT_LIFETIME);
		    
		    context.setOption(GSSConstants.GSS_MODE,
				      GSIConstants.MODE_SSL);

		    context.requestCredDeleg(false);

		    s = (GssSocket)factory.createSocket(req._url.getHost(), 
							req._url.getPort(),
							context);
		    s.setSoTimeout(SO_TIMEOUT);
		    s.setAuthorization(SelfAuthorization.getInstance());
		    
		    os = s.getOutputStream();
		    is = s.getInputStream();
		    
		    String report = _myProtocol.getStatusUpdateMessage(req._url.getURL(),
								       req._jmurl,
								       req._url.getHost(),
								       req._status,
								       req._failureCode);
		    
		    os.write(report.getBytes());
		    os.flush();
		    
		    HTTPResponseParser cr = new HTTPResponseParser(is);
		    
		    if (cr.isOK()) {
			if (_logger.isDebugEnabled()) {
			    _logger.debug("[statusUpdater]: client received status update: " + req._status);
			}

			// THIS IS THE END CONDITION OF THE THREAD AND LOOP
			if (req._status == GRAMConstants.STATUS_DONE ||
			    req._status == GRAMConstants.STATUS_FAILED) {
			    stop = true;
			}
			break;
		    }
		    
		} catch (IOException e) {
		    _logger.debug("[statusUpdater]: failed. trying again...", e);
		    if (tries+1 < MAX_TRIES) {
			try { Thread.sleep(PAUSE); } catch(Exception ee) {}
		    }
		} catch (Exception e) {
		    _logger.debug("Unexpected error.", e);
		} finally {
		    if (os != null) {
			try { os.close(); } catch(Exception e) {}
		    }
		    if (is != null) {
			try { is.close(); } catch(Exception e) {}
		    }
		    if (s != null) {
			try { s.close(); } catch(Exception e) {}
		    }
		}
		
	    }
	}

	_logger.info("[statusUpdater] done.");
    }
}
