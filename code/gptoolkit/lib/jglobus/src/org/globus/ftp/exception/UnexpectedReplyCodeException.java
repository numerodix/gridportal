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
package org.globus.ftp.exception;

import org.globus.ftp.vanilla.Reply; 

/**
   Indicates that the received reply had different code than
   it had been expected.
 */
public class UnexpectedReplyCodeException extends FTPException {

    private Reply reply;

    public UnexpectedReplyCodeException(int code, String msg, Reply r) {
	super(code,msg);
	this.reply = r;
    }

    public UnexpectedReplyCodeException(Reply r) {
	super(FTPException.UNSPECIFIED, 
	      "Unexpected reply: " + r);
	this.reply = r;
    }

    public Reply getReply() {
	return reply;
    }

 }
