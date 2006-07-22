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
package org.globus.io.urlcopy;

import org.globus.common.ChainedException;

/**
 * Encapsulates the exceptions caused by various errors
 * in the url-copy library.
 */
public class UrlCopyException extends ChainedException {
    
    public UrlCopyException(String msg) {
	super(msg);
    }
    
    public UrlCopyException(String msg, Throwable ex) {
	super(msg, ex);
    }
    
}
