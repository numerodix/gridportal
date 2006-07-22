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
package org.globus.io.gass.client.internal;

import org.globus.util.http.HTTPProtocol;

/** This is a pure Java implementation of the Globus GASS protocol. Normally 
 * one does not need to use this class directly but instead uses the Client 
 * class.
 */
public class GASSProtocol {
 
    /** the default user agent string  */
    private static final String USER_AGENT = "Java-Globus-GASS-HTTP/1.1.0";
    
    /** the default gass append url   */
    private static final String APPEND_URI = "/globus-bins/GASSappend?";

    private static final String TYPE       = "application/octet-stream";
    
    /** This method concatenates a properly formatted header for performing
     * Globus Gass GETs with the given information.
     *
     * @param path the path of the file to get 
     * @param host the host which contains the file to get
     *
     * @return <code>String</code> the properly formatted header to be sent to a
     * gass server
     */
    public static String GET(String path, String host) {
	return HTTPProtocol.createGETHeader("/" + path, host, USER_AGENT);
    }
    
    /** This method concatenates a properly formatted header for performing 
     * Globus Gass PUTs with the given information.
     *
     * @param path the path of the remote file to put to
     * @param host the host of the remote file to put to
     * @param length the length of data which will be sent (the size of the file)
     * @param append append mode
     *
     * @return <code>String</code> the properly formatted header to be sent to a
     * gass server
     */
    public static String PUT(String path, String host, long length, boolean append) {
	String newPath = null;
	
	if (append) {
	    newPath = APPEND_URI + "/" + path;
	} else {
	    newPath = "/" + path;
	}
	
	return HTTPProtocol.createPUTHeader(newPath, host, USER_AGENT, 
					    TYPE, length, append);
    }   

    public static String SHUTDOWN(String path, String host) {
        return HTTPProtocol.createPUTHeader(path, host, USER_AGENT,
                                            TYPE, 0, false);
    }
}
