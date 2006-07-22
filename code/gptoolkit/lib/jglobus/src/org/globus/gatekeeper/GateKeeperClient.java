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

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gatekeeper.internal.GateKeeperProtocol;
import org.globus.gatekeeper.internal.GateKeeperRequest;

import org.apache.log4j.Logger;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSContext;


/**
 * GateKeeperClient is a thread which handles individual client request.  All
 * communications between the client and the gatekeeper are handled by this object.
 */
public class GateKeeperClient extends Thread {

    private GateKeeperServer _service = null;
    private InputStream _is = null;
    private OutputStream _os = null;
    private Socket _s   = null;
    private GateKeeperProtocol _gatekeeperProtocol = null;

    private Logger _logger;

    public GateKeeperClient(GateKeeperServer service,
			    Socket s) {
        _s = s;
	_service = service;
	_gatekeeperProtocol = GateKeeperProtocol.getInstance("GRAM1.0");
    }

    public void setLogger(Logger logger) {
	_logger = logger;
    }

    public void run() {
	try {
	    _is = _s.getInputStream();
	    _os = _s.getOutputStream();
	    
	    GssSocket socket = (GssSocket)_s;

	    GSSContext context = socket.getContext();

	    String globusID = context.getSrcName().toString();

	    if (_logger.isInfoEnabled()) {
		_logger.info("Authenticated globus user: " + globusID);
	    }

	    // make sure the grid map file is up-to-date
	    _service.getGridMap().refresh();

	    // perform authentication
	    String userID = _service.getGridMap().getUserID(globusID);
	    
	    if (userID == null) {
		throw new AuthorizationException();
	    } else {
		if (_logger.isInfoEnabled()) {
		    _logger.info("Authorized as local user: " + userID);
		}
	    }
	    
	    GSSCredential creds = context.getDelegCred();

	    if (_logger.isInfoEnabled()) {
		if (creds == null) {
		    _logger.info("Delegation not performed.");
		} else {
		    _logger.info("Delegation performed.");
		}
	    }

	    GateKeeperRequest req = _gatekeeperProtocol.parseRequest(_is);

	    boolean ping = req.isPing();
	    
	    if (ping) {
		// ping
		handlePing(creds, req);
	    } else if (creds != null && !ping) {
		// submit a request
		handleRequest(creds, req);
	    } else {
		// error - will be logged
		throw new BadRequestException();
	    }
	} catch(GateKeeperException e) {
	    // gram protocol map exception into right message
	    _logger.error("Client request failed.", e);
	    write(_gatekeeperProtocol.getErrorMessage(e));
	} catch(IOException e) {
	    _logger.error("IOError", e);
	} catch(Exception e) {
	    _logger.error("Unexpected error.", e);
	    write(_gatekeeperProtocol.getErrorMessage(e));
	} finally {
	    close();
	}
    }

    private void write(String msg) {
        if (msg == null) return;
        try { _os.write(msg.getBytes()); } catch(Exception e) {}
    }

    private void close() {
	if (_os != null) {
	    try { _os.close(); } catch(Exception e) {}
	}
	if (_is != null) {
	    try { _is.close(); } catch(Exception e) {}
	}
	if (_s != null) {
	    try { _s.close(); } catch(Exception e) {}
	}
	if (_logger.isInfoEnabled()) {
	    _logger.info("Client disconnected: " + _s.getInetAddress() +
			 ":" + _s.getPort());
	}
    }

    // ------------------------------------

    protected void handlePing(GSSCredential creds, GateKeeperRequest cr) 
	throws GateKeeperException {
	if (_logger.isInfoEnabled()) {
	    _logger.info("Ping request for: " + cr.getService());
	}

	// just lookup the service
	Service service = _service.getService(cr.getService());
	
	write(_gatekeeperProtocol.getPingSuccessMessage());
	_logger.info("Ping successfull.");
    }

    protected void handleRequest(GSSCredential creds, GateKeeperRequest cr)
	throws GateKeeperException {
	if (_logger.isInfoEnabled()) {
	    _logger.info("Service requested: " + cr.getService());
	}
	
	Service service = _service.getService(cr.getService());
	
	// set credentials
	service.setCredentials(creds);
	
	String msg = null;
	try {
	    // throw exception if any error
	    service.request(cr);
	    msg = service.getRequestSuccessMessage();
	    _logger.info("Service request successfull: " + service.getHandle());
	} catch(ServiceException e) {
	    msg = service.getRequestFailMessage(e);
	    _logger.info("Service request failed.", e);
	} finally {
	    write(msg);
	}
    }
    
}
