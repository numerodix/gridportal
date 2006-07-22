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

import org.globus.ftp.Session;
import org.globus.ftp.GridFTPSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GridFTPDataChannelFactory implements DataChannelFactory {

    protected static Log logger =
        LogFactory.getLog(GridFTPDataChannelFactory.class.getName());

    public DataChannel getDataChannel(Session session, SocketBox socketBox) {
	if (! (session instanceof GridFTPSession)) {
	    throw new IllegalArgumentException(
					       "session should be a GridFTPSession");
	}

	logger.debug("starting secure data channel");
	return new GridFTPDataChannel(session, socketBox);

    }
}
