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

import javax.security.auth.Subject;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

/**
 * Generic JAAS Subject helper API that provides abstraction layer on top of 
 * vendor-specific JAAS Subject extensions implementations. 
 * Most vendors defined their own JAAS Subject helper classes because of the 
 * <a href="http://publib7b.boulder.ibm.com/wasinfo1/en/info/aes/ae/rsec_jaasauthor.html">
 * Subject propagation issue</a> in JAAS.
 */
public abstract class JaasSubject {

    private static JaasSubject subject;
    
    protected JaasSubject() {}
    
    /**
     * Gets current implementation of the <code>JaasSubject</code> API.
     * The method attempts to load a <code>JaasSubject</code> implementation
     * by loading a class specified by the 
     * "<i>org.globus.jaas.provider</i>" system property. If the property
     * is not set the default Globus implementation is loaded.
     */
    public static synchronized JaasSubject getJaasSubject() {
	if (subject == null) {
	    String className = System.getProperty("org.globus.jaas.provider");
	    if (className == null) {
		className = "org.globus.gsi.jaas.GlobusSubject";
	    }
	    try {
		Class clazz = Class.forName(className);
		if (!JaasSubject.class.isAssignableFrom(clazz)) {
		    throw new RuntimeException("Invalid JaasSubject provider class: '" + 
					       className + "'");
		}
		subject = (JaasSubject)clazz.newInstance();
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException("Unable to load '" + className + "' class: " +
					   e.getMessage());
	    } catch (InstantiationException e) {
		throw new RuntimeException("Unable to instantiate '" + className + "' class: " +
					   e.getMessage());
	    } catch (IllegalAccessException e) {
		throw new RuntimeException("Unable to instantiate '" + className + "' class: " +
					   e.getMessage());
	    }
	}
	return subject;
    }

    // SPI 
    /**
     * SPI method. 
     */
    public abstract Subject getSubject();
    
    /**
     * SPI method. 
     */
    public abstract Object runAs(Subject subject, PrivilegedAction action);

    /**
     * SPI method. 
     */
    public abstract Object runAs(Subject subject, PrivilegedExceptionAction action)
	throws PrivilegedActionException;
    
    // API
    
    /**
     * A convenience method, calls 
     * <code>JaasSubject.getJaasSubject().runAs()<code/>.
     */
    public static Object doAs(Subject subject, PrivilegedExceptionAction action) 
	throws PrivilegedActionException {
	return JaasSubject.getJaasSubject().runAs(subject, action);
    }
    
    /**
     * A convenience method, calls 
     * <code>JaasSubject.getJaasSubject().runAs()<code/>.
     */
    public static Object doAs(Subject subject, PrivilegedAction action) {
	return JaasSubject.getJaasSubject().runAs(subject, action);
    }
    
    /**
     * A convenience method, calls 
     * <code>JaasSubject.getJaasSubject().getSubject()<code/>.
     */
    public static Subject getCurrentSubject() {
	return JaasSubject.getJaasSubject().getSubject();
    }
    
}
