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
package org.globus.io.streams;

import java.io.IOException;

import org.globus.ftp.GridFTPClient;
import org.globus.ftp.Session;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.exception.FTPException;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.HostAuthorization;

import org.ietf.jgss.GSSCredential;

public class GridFTPInputStream extends FTPInputStream {

    public GridFTPInputStream(GSSCredential cred,
			      String host, 
			      int port, 
			      String file) 
	throws IOException, FTPException {
	this(cred, HostAuthorization.getInstance(),
	     host, port, 
	     file, true, Session.TYPE_IMAGE, true);
    }

    public GridFTPInputStream(GSSCredential cred,
			      Authorization auth,
			      String host, 
			      int port, 
			      String file,
			      boolean reqDCAU) 
	throws IOException, FTPException {
	this(cred, auth, host, port, 
	     file, true, Session.TYPE_IMAGE, reqDCAU);
    }

    public GridFTPInputStream(GSSCredential cred,
			      Authorization auth,
			      String host, 
			      int port, 
			      String file,
			      boolean passive,
			      int type,
			      boolean reqDCAU) 
	throws IOException, FTPException {
	GridFTPClient gridFtp = new GridFTPClient(host, port);
	gridFtp.setAuthorization(auth);
	gridFtp.authenticate(cred);
	
	if (gridFtp.isFeatureSupported("DCAU")) {
	    if (!reqDCAU) {
		gridFtp.setDataChannelAuthentication(DataChannelAuthentication.NONE);
	    }
	} else {
	    gridFtp.setLocalNoDataChannelAuthentication();
	}

	ftp = gridFtp;
	
	get(passive, type, file);
    }

}
