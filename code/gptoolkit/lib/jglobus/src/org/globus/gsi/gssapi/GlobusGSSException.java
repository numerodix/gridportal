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
package org.globus.gsi.gssapi;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.ietf.jgss.GSSException;

public class GlobusGSSException extends GSSException {

    public static final int
	PROXY_VIOLATION = 5,
	BAD_ARGUMENT = 7,
	BAD_NAME = 25,
	CREDENTIAL_ERROR = 27,
	TOKEN_FAIL = 29,
	DELEGATION_ERROR = 30,
	BAD_MIC = 33,
	UNKNOWN_OPTION = 37;

    public static final int
	BAD_OPTION_TYPE = 100,
	BAD_OPTION = 101,
	UNKNOWN = 102;

    private static ResourceBundle resources;
    
    static {
	try {
	    resources = ResourceBundle.getBundle("org.globus.gsi.gssapi.errors");
	} catch (MissingResourceException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }
    
    private Throwable exception;

    public GlobusGSSException(int majorCode, 
			      Throwable exception) {
	super(majorCode);
	this.exception = exception;
    }

    public GlobusGSSException(int majorCode, 
			      int minorCode,
			      String minorString,
			      Throwable exception) {
	super(majorCode, minorCode, minorString);
	this.exception = exception;
    }

    public GlobusGSSException(int majorCode,
			      int minorCode,
			      String key) {
	this(majorCode, minorCode, key, (Object[])null);
    }
    
    public GlobusGSSException(int majorCode,
			      int minorCode,
			      String key,
			      Object [] args) {
	super(majorCode);
	
	String msg = null;
	try {
	    msg = MessageFormat.format(resources.getString(key), args);
	} catch (MissingResourceException e) {
	    //msg = "No msg text defined for '" + key + "'";
	    throw new RuntimeException("bad" + key);
	}
	
	setMinor(minorCode, msg);
	this.exception = null;
    }

    
    /**
     * Prints this exception's stack trace to <tt>System.err</tt>.
     * If this exception has a root exception; the stack trace of the
     * root exception is printed to <tt>System.err</tt> instead.
     */
    public void printStackTrace() {
        printStackTrace( System.err );
    }

    /**
     * Prints this exception's stack trace to a print stream.
     * If this exception has a root exception; the stack trace of the
     * root exception is printed to the print stream instead.
     * @param ps The non-null print stream to which to print.
     */
    public void printStackTrace(java.io.PrintStream ps) {
        if ( exception != null ) {
            String superString = getLocalMessage();
            synchronized ( ps ) {
                ps.print(superString);
                ps.print((superString.endsWith(".") ? 
                          " Caused by " : ". Caused by "));
                exception.printStackTrace( ps );
            }
        } else {
            super.printStackTrace( ps );
        }
    }
    
    /**
     * Prints this exception's stack trace to a print writer.
     * If this exception has a root exception; the stack trace of the
     * root exception is printed to the print writer instead.
     * @param pw The non-null print writer to which to print.
     */
    public void printStackTrace(java.io.PrintWriter pw) {
        if ( exception != null ) {
            String superString = getLocalMessage();
            synchronized (pw) {
                pw.print(superString);
                pw.print((superString.endsWith(".") ? 
                          " Caused by " : ". Caused by "));
                exception.printStackTrace( pw );
            }
        } else {
            super.printStackTrace( pw );
        }
    }

    public String getMessage() {
        String answer = super.getMessage();
        if (exception != null && exception != this) {
            String msg = exception.getMessage();
            if (msg == null) {
                msg = exception.getClass().getName();
            }
            answer += " [Caused by: " + msg + "]";
        }
        return answer;
    }
    
    private String getLocalMessage() {
        String message = super.getMessage();
        return (message == null) ? getClass().getName() : message;
    }

}
