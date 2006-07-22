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
package org.globus.ftp.dc;

public abstract class Task {

    protected boolean complete = false;
    protected Exception exception;

    public abstract void execute() throws Exception;

    public void stop() {
    }

    public synchronized void setComplete(Exception e) {
	complete = true;
	exception = e;
	notify();
    }

    public synchronized Exception waitFor() {
	while (!complete) {
	    try {
		wait();
	    } catch (Exception e) {
		return e;
	    }
	}
	return exception;
    }
    
}
