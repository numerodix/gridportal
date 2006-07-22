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
package org.globus.net.protocol.httpg;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.lang.reflect.Constructor;

public class Handler extends URLStreamHandler {

    private static final String CLASS =
        "org.globus.net.GSIHttpURLConnection";

    private static final Class[] PARAMS = 
        new Class[] { URL.class };
        
    private static Constructor constructor = null;

    private static synchronized Constructor initConstructor() {
        if (constructor == null) {
            ClassLoader loader = 
                Thread.currentThread().getContextClassLoader();
            try {
                Class clazz = Class.forName(CLASS, true, loader);
                constructor = clazz.getConstructor(PARAMS);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load url handler: " +
                                           e.getMessage());
            }
        }
        return constructor;
    }
    
    protected URLConnection openConnection(URL u) {
        if (constructor == null) {
            initConstructor();
        }
        try {
            return (URLConnection)constructor.newInstance(new Object[] {u});
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate url handler: " +
                                       e.getMessage());
        }
    }
    
    protected int getDefaultPort() {
	return 8443;
    }

    protected void setURL(URL u, String protocol, String host, int port,
			  String authority, String userInfo, String path,
			  String query, String ref) {
	if (port == -1) {
	    port = getDefaultPort();
	}
	super.setURL(u, protocol, host, port, authority, userInfo,
		     path, query, ref);
    }

}
