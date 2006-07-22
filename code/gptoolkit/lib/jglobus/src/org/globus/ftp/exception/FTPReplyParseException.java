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
   Indicates that the reply received from server failed to parse.
 */
public class FTPReplyParseException extends FTPException {

    //public static final int UNSPECIFIED = 0;
    public static final int STRING_TOO_SHORT = 1;
    public static final int FIRST_3_CHARS = 2;
    public static final int UNEXPECTED_4TH_CHAR = 3;
    public static final int MESSAGE_UNPARSABLE = 4;
    private static String[] codeExplained;
    static {
	codeExplained =new String[]{
	    "Unspecified exception.",
	    "Reply string too short.",
	    "First 3 characters are not digits.",
	    "Unexpected 4th character.",
	    "Reply message unparsable"
	};
    }

    public String getCodeExplanation(int code) {

	if (codeExplained.length > code)
	    return codeExplained[code];
	else return "";
    }

    public FTPReplyParseException(int code) {
	super(code);
    }

    public FTPReplyParseException(int code, String message) {
	super(code, message);
    }

}



