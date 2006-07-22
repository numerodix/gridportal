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
package org.globus.gsi.proxy.ext;

import org.globus.gsi.bc.BouncyCastleX509Extension;

/** 
 * Represents ProxyCertInfo X.509 extension.
 */
public class GlobusProxyCertInfoExtension extends BouncyCastleX509Extension {

    public GlobusProxyCertInfoExtension(ProxyCertInfo value) {
	super(ProxyCertInfo.OLD_OID.getId(), true, null);
	if (value == null) {
	    throw new IllegalArgumentException("value == null");
	}
	setValue(value);
    }

    public void setOid(String oid) {
	throw new RuntimeException("Oid cannot be changed");
    }

    public void setCritical(boolean critical) {
	throw new RuntimeException("Critical property cannot be changed");
    }
}
