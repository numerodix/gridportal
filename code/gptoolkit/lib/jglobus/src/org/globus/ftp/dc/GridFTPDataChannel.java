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
package org.globus.ftp.dc;

import org.globus.ftp.GridFTPSession;
import org.globus.ftp.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
   GridFTPDataChannel, unlike SimpleDataChannel, does not own the associated socket and does not destroy it when the transfer completes. It is the facade who is responsible for socket lifetime management. This approach allows for data channel reuse.
 **/
public class GridFTPDataChannel extends SimpleDataChannel {

	private static Log logger =
		LogFactory.getLog(GridFTPDataChannel.class.getName());

	// utility alias to session
	protected GridFTPSession gSession;

	public GridFTPDataChannel(Session session, SocketBox socketBox) {
		super(session, socketBox);
		gSession = (GridFTPSession) session;
		transferThreadFactory = new GridFTPTransferThreadFactory();
	}

	// todo: reimplement close()?
}
