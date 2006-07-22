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
package org.globus.gsi.proxy;

import java.security.cert.X509Certificate;

import org.globus.gsi.proxy.ext.ProxyCertInfo;

/**
 * A restricted proxy policy handler interface. All policy handlers
 * must implement this interface.
 */
public interface ProxyPolicyHandler {
   
    /**
     * @param proxyCertInfo the <code>ProxyCertInfo</code> extension
     *        found in the restricted proxy certificate.
     * @param certPath the certificate path being validated.
     * @param index the index of the certificate in the certPath that is
     *        being validated - the index of the restricted proxy 
     *        certificate.
     * @exception ProxyPathValidatorException if policy
     *            validation fails.
     */
    public void validate(ProxyCertInfo proxyCertInfo,
			 X509Certificate[] certPath,
			 int index)
	throws ProxyPathValidatorException;
}
