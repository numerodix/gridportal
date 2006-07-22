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
package org.globus.mds.gsi.jndi;

import java.security.Provider;
import java.security.Security;


public final class SaslProvider extends Provider {

    private static SaslProvider provider = new SaslProvider();

    public SaslProvider() {
        super("Globus", 1.0, "Globus Security Sasl Provider");
        setProperty("SaslClientFactory.GSS-GSI",  
                    "org.globus.mds.gsi.jndi.SaslClientFactoryWrapper");
        setProperty("SaslClientFactory.GSS-OWNYQ6NTEOAUVGWG",
                    "org.globus.mds.gsi.jndi.SaslClientFactoryWrapper");
    }

    public static SaslProvider getInstance() {
        return provider;
    }

    public static void addProvider() {
        Security.addProvider(getInstance());
    }
}
