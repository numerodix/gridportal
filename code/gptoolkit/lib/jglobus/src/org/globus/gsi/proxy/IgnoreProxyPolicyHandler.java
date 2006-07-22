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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple restricted proxy policy handler that logs the
 * proxy policy language oid. It can be used for debugging purposes.
 */
public class IgnoreProxyPolicyHandler implements ProxyPolicyHandler {

    private static Log logger = 
	LogFactory.getLog(IgnoreProxyPolicyHandler.class.getName());

    public void validate(ProxyCertInfo proxyCertInfo,
			 X509Certificate[] certPath,
			 int index)
	throws ProxyPathValidatorException {
	logger.info("ProxyPolicy ignored: " + proxyCertInfo.getProxyPolicy().getPolicyLanguage().getId());
    }
    
}
