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

import org.globus.ftp.vanilla.Reply;

/**
   Local server communicate with client with a  simplified control channel.
   This is a local, minimal version of Reply, free of overhead
   caused by parsing during construction.
 **/
public class LocalReply extends Reply {

    private static final String MESSAGE = "this LocalReply does not have a message";
    
    public LocalReply(int code) {
	this.message = MESSAGE;
	this.isMultiline = false;
	this.code = code;
	this.category = code / 100;
    }

    public LocalReply(int code, String message) {
	this.message = message;
	this.isMultiline = false;
	this.code = code;
	this.category = code / 100;
    }

}

