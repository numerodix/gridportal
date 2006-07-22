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
package org.globus.gsi.gssapi.net;

import java.net.Socket;
import java.io.IOException;

import org.ietf.jgss.GSSContext;

public abstract class GssSocketFactory {
  
    private static GssSocketFactory factory = null;

    public synchronized static GssSocketFactory getDefault() {
	if (factory == null) {
	    String className = System.getProperty("org.globus.gsi.gssapi.net.provider");
	    if (className == null) {
		className = "org.globus.gsi.gssapi.net.impl.GSIGssSocketFactory";
	    }
	    try {
		Class clazz = Class.forName(className);
		if (!GssSocketFactory.class.isAssignableFrom(clazz)) {
		    throw new RuntimeException("Invalid GssSocketFactory provider class");
		}
		factory = (GssSocketFactory)clazz.newInstance();
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
	return factory;
    }

    public abstract Socket createSocket(Socket s, 
					String host,
					int port, 
					GSSContext context);

    public abstract Socket createSocket(String host, 
					int port, 
					GSSContext context)
	throws IOException;
    
}
