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
package org.globus.myproxy;

public interface MyProxyConstants {

    /** The version string (MYPROXYv2) to send at the start of
     * communication with the MyProxy server. */
    public static final String MYPROXY_PROTOCOL_VERSION = "MYPROXYv2";

    /** MyProxy passwords must be 6 characters or longer. */
    public static final int MIN_PASSWORD_LENGTH         = 6;

    static final String VERSION    = "VERSION=" + MYPROXY_PROTOCOL_VERSION;
    static final String COMMAND    = "COMMAND=";
    static final String USERNAME   = "USERNAME=";
    static final String PASSPHRASE = "PASSPHRASE=";
    static final String LIFETIME   = "LIFETIME=";
    static final String CRED_NAME  = "CRED_NAME=";
    static final String RETRIEVER  = "RETRIEVER=";
    static final String RENEWER    = "RENEWER=";
    static final String CRED_DESC  = "CRED_DESC=";
    static final String NEW_PHRASE = "NEW_PHRASE=";
    static final String CRLF       = "\n";

}
