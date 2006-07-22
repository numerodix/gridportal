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

/** Not used.
    This exception should be thrown on an attempt of accessing
    functionality that has not been implemented.
**/
public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
	super("This method has not been implemented.");
    }
}
