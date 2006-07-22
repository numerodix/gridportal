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
package org.globus.gram;

/**
 * This class represents a specific type of GramException.
 * This exception is thrown when a two phase commit request is
 * made to GRAM 1.5 compatibile service.
 */
public class WaitingForCommitException extends GramException {
    
    public WaitingForCommitException() {
	super(WAITING_FOR_COMMIT);
    }
    
}
