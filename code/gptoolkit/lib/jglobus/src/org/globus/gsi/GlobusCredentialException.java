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
package org.globus.gsi;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.globus.common.ChainedException;

/**
 * Encapsulates the exceptions caused
 * by various errors in/problems with Globus proxies.
 */
public class GlobusCredentialException extends ChainedException {
    
    public static final int FAILURE = -1;
    public static final int EXPIRED = 1;
    public static final int DEFECTIVE = 2;
    public static final int IO_ERROR = 3;
    public static final int SEC_ERROR = 3;

    private static ResourceBundle resources;
    
    static {
	try {
	    resources = ResourceBundle.getBundle("org.globus.gsi.errors");
	} catch (MissingResourceException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private int errorCode = FAILURE;
    
    public GlobusCredentialException(int errorCode,
				     String msgId,
				     Throwable root) {
	this(errorCode, msgId, null, root);
    }

    public GlobusCredentialException(int errorCode,
				     String msgId,
				     Object [] args) {
	this(errorCode, msgId, args, null);
    }
    
    public GlobusCredentialException(int errorCode,
				     String msgId,
				     Object [] args,
				     Throwable root) {
	super(getMessage(msgId, args), root);
	this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
	return this.errorCode;
    }

    private static String getMessage(String msgId, Object[] args) {
	try {
	    return MessageFormat.format(resources.getString(msgId), args);
	} catch (MissingResourceException e) {
	    //msg = "No msg text defined for '" + key + "'";
	    throw new RuntimeException("bad" + msgId);
	}
    }

}
