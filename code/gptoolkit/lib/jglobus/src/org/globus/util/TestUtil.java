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

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class TestUtil {

    private Properties props;

    public TestUtil(String config) throws Exception {
	Thread t = Thread.currentThread();
	InputStream in = null;
	try {
	    in = t.getContextClassLoader().getResourceAsStream(config);
	    
	    if (in == null) {
		throw new IOException("Test configuration file not found: " +
				      config);
	    }

	    props = new Properties();
	    props.load(in);
	} finally {
	    if (in != null) {
		in.close();
	    }
	}
    }

    public String get(String propName) {
	return props.getProperty(propName);
    }

    public int getAsInt(String propName) {
	String value = props.getProperty(propName);
	return Integer.parseInt(value);
    }
    
}
