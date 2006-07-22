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
package org.globus.gsi.gssapi.net.example;

import org.globus.gsi.gssapi.net.GssSocket;

public class GetOpts extends org.globus.gsi.gssapi.example.GetOpts {

    public int wrapMode = GssSocket.SSL_MODE;

    public GetOpts(String usage, String helpMsg) {
	super(usage, helpMsg);
    }

    protected int parseArg(String[] args, int i) {
	if (args[i].equalsIgnoreCase("-wrap-mode")) {
	    String arg = args[++i];
	    if (arg.equalsIgnoreCase("ssl")) {
		wrapMode = GssSocket.SSL_MODE;
	    } else if (arg.equalsIgnoreCase("gsi")) {
		wrapMode = GssSocket.GSI_MODE;
	    } else {
		error("Invalid -wrap-mode argument: " + arg);
	    }
	    return 1;
	} else {
	    return super.parseArg(args, i);
	}
    }

}
