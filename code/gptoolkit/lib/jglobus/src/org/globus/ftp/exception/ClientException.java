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


/** Indicates a local client side problem that has 
    not been caused by remote server nor the local data channel. 
**/
public class ClientException extends FTPException {


    //public static final int UNSPECIFIED = 0;

    public static final int NOT_AUTHORIZED = 1;
    public static final int PBSZ_DIFFER = 2;
    public static final int TRANSFER_MODE_DIFFER = 3;
    public static final int TRANSFER_TYPE_DIFFER = 4;
    public static final int BAD_SERVER_MODE = 5;
    public static final int REPLY_TIMEOUT = 6;
    public static final int THREAD_KILLED = 7;
    public static final int BAD_MODE = 8;
    public static final int MARK_NOT_SUPPORTED = 9;
    public static final int CALL_PASSIVE_FIRST = 10;
    public static final int LOCAL_TRANSFER = 11;
    public static final int UNKNOWN_HOST = 12;
    public static final int BAD_TYPE = 13;
    public static final int SOCKET_OP_FAILED = 14;

    private static String[] codeExplained;
    static {
	codeExplained = new String[]
	{"Unspecified category.",
	 "Server authorization has not been performed.",
	 "Servers have been set up with different protection buffer sizes.",
	 "Servers have been set up with different transfer modes.",
	 "Servers have been set up with different transfer types.",
	 "One server should be have been set active and the other passive.",
	 "Reply wait timeout.",
	 "Transfer thread has been killed.",
	 "Server has been set to wrong transfer mode.",
	 "Client's BufferedReader implementation does not support mark().",
	 "Set one server to passive before setting other to active.",
	 "Local transfer problem.",
	 "Cannot connect - unknown remote host.",
	 "Server has been set to wrong transfer type.",
	 "Socket operation failed."
	};
    }

    public String getCodeExplanation(int code) {
	if (codeExplained.length > code)
	    return codeExplained[code];
	else return "";
    }


    protected int code = UNSPECIFIED;

    public ClientException(int code, String message) {
	super(code, message);
    }

    public ClientException(int code) {
	super(code);
    }
 
}
