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
package org.globus.util;

public class TimestampEntry {

    private Object value;
    private long lastModified;

    public TimestampEntry(Object value, long lastModified) {
	this.value = value;
	this.lastModified = lastModified;
    }
    
    public Object getValue() {
	return this.value;
    }
    
    public void setValue(Object value) {
	this.value = value;
    }
    
    public long getLastModified() {
	return this.lastModified;
    }
    
    public void setLastModified(long lastModified) {
	this.lastModified = lastModified;
    }
}
