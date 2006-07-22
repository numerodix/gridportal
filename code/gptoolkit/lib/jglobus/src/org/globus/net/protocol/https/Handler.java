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
package org.globus.net.protocol.https;

import java.net.URL;
import java.net.URLConnection;

public class Handler extends org.globus.net.protocol.httpg.Handler {
    
    protected URLConnection openConnection(URL u) {
        URLConnection conn = super.openConnection(u);
        conn.setRequestProperty("gssMode", "ssl");
        return conn;
    }

}
