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

/** 
    Indicates that operation failed because of conditions 
    on the server, independent from the client. For instance, 
    the server did not understand command, or could not read file. 
    Note that here "server" can mean either a remote server, 
    or the local internal server (FTPServerFacade).
**/
public class ServerException extends FTPException {

    /**
       Server refused performing the request
     **/
    public static final int SERVER_REFUSED = 1;
    /** 
	The communication from the server was not understood,
	possibly because of incompatible protocol.
     **/
    public static final int WRONG_PROTOCOL = 2;
    public static final int UNSUPPORTED_FEATURE = 3;
    public static final int REPLY_TIMEOUT = 4;
	public static final int PREVIOUS_TRANSFER_ACTIVE = 5;

    private static String[] codeExplained;
    static {
	codeExplained = new String[]
	{"Unspecified category.",
	 "Server refused performing the request.",
	 "The server uses unknown communication protool.",
	 "Server does not support feature.",
	 "Reply wait timeout.",
	 "Refusing to start transfer before previous transfer completes"};
    }

    public String getCodeExplanation(int code) {
	if (codeExplained.length > code)
	    return codeExplained[code];
	else return "";
    }


    protected int code = UNSPECIFIED;

    //this message is not just explanation of the code.
    //it is a custom message informing of particular 
    //conditions of the error.
    protected String customMessage;

    public ServerException(int code, String message) {
	super(code, message);
	customMessage = message;
    }

    public ServerException(int code) {
	super(code);
    }
 
    /**
       Constructs server exception with FTPReplyParseException
       nested in it.
     **/
    public static ServerException embedFTPReplyParseException(
					        FTPReplyParseException rpe,
						String message) {
	ServerException se = new ServerException(
						 WRONG_PROTOCOL,
						 message);
	se.setRootCause(rpe);
	return se;
    }

    public static ServerException embedFTPReplyParseException(
					        FTPReplyParseException rpe) {
	return embedFTPReplyParseException(rpe, "");
    }


    /**
       Constructs server exception with UnexpectedReplyCodeException
       nested in it.
     **/
    public static ServerException embedUnexpectedReplyCodeException(
			       		    UnexpectedReplyCodeException urce,
					    String message) {
	ServerException se = new ServerException(
						 SERVER_REFUSED,
						 message);
	se.setRootCause(urce);
	return se;
    }
    public static ServerException embedUnexpectedReplyCodeException(
			       		    UnexpectedReplyCodeException urce)
 {
	return embedUnexpectedReplyCodeException(urce, "");
    }
}
