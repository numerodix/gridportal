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
    thrown by PerformanceMarker, mostly during construction.
**/
public class PerfMarkerException extends FTPException {

    /**
       
     **/
    public static final int NO_SUCH_PARAMETER = 1;

    private static String[] codeExplained;
    static {
	codeExplained = new String[]
	{"Unspecified category.",
	 "Marker does not contain the requested parameter."
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

    public PerfMarkerException(int code, String message) {
	super(code, message);
	customMessage = message;
    }

    public PerfMarkerException(int code) {
	super(code);
    }
 
}
