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
package org.globus.gsi.jaas;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

import javax.security.auth.Subject;

/**
 * Standard JAAS implementation of the JAAS Subject helper API.
 * This implementation (because of a problem in JAAS) can cut off
 * the Subject object from the thread context.
 */
public class StandardSubject extends JaasSubject {
    
    protected StandardSubject() {
	super();
    }
    
    public Subject getSubject() {
	return Subject.getSubject(AccessController.getContext());
    }
    
    public Object runAs(Subject subject, PrivilegedAction action) {
	return Subject.doAs(subject, action);
    }
    
    public Object runAs(Subject subject, PrivilegedExceptionAction action)
	throws PrivilegedActionException {
	return Subject.doAs(subject, action);
    }
}
