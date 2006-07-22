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
package org.globus.gatekeeper.jobmanager;

import org.globus.gatekeeper.ServiceException;
import org.globus.gram.internal.GRAMProtocolErrorConstants;

import org.globus.gram.GramException;

public class JobManagerException extends ServiceException implements GRAMProtocolErrorConstants {

    private int _errorCode = -1;

    public JobManagerException() {
    }
    
    public JobManagerException(int errorCode) {
	super(GramException.getMessage(errorCode));
	_errorCode = errorCode;
    }

    public JobManagerException(int errorCode, String msg, Throwable ex) {
	super(msg, ex);
	_errorCode = errorCode;
    }

    public JobManagerException(int errorCode, String msg) {
	super(msg);
	_errorCode = errorCode;
    }
    
    public JobManagerException(int errorCode, Throwable ex) {
	this(errorCode, null, ex);
    }

    public JobManagerException(String msg) {
	super(msg);
    }

    public int getErrorCode() {
	return _errorCode;
    }
    
}
