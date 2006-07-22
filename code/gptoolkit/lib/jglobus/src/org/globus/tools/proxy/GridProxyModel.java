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
package org.globus.tools.proxy;

import java.security.cert.X509Certificate;

import org.globus.common.CoGProperties;
import org.globus.gsi.GlobusCredential;

public abstract class GridProxyModel {
    
    protected X509Certificate userCert;
    protected CoGProperties props = null;
    
    public abstract GlobusCredential createProxy(String pwd)
	throws Exception;
    
    public CoGProperties getProperties() {
	if (props == null) {
	    props = CoGProperties.getDefault();
	}
	return props;
    }

    public boolean getLimited() {
	return false;
    }
    
}
