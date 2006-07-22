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
    Indicates data channel problems. Thrown by local server at layer 2.
**/
public class DataChannelException extends FTPException {

    //public static final int UNSPECIFIED = 0;
    public static final int UNDEFINED_SERVER_MODE = 1;
    public static final int BAD_SERVER_MODE = 2;
    private static String[] codeExplained;
    static {
	codeExplained = new String[]
	{"Unspecified category.",
	 "Undefined server mode (active or passive?)",
	 "setPassive() must match store() and setActive() - retrieve() "
	};
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

    public DataChannelException(int code, String message) {
	super(code, message);
	customMessage = message;
    }

    public DataChannelException(int code) {
	super(code);
    }
 
}
