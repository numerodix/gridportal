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
package org.globus.gsi.bc;

import java.io.IOException;

import org.bouncycastle.asn1.DEREncodable;

import org.globus.gsi.X509Extension;

/** 
 * A convenience class for creating X.509 extensions from 
 * <code>DEREncodable</code> objects.
 */
public class BouncyCastleX509Extension extends X509Extension {

    public BouncyCastleX509Extension(String oid) {
	this(oid, false, null);
    }
    
    public BouncyCastleX509Extension(String oid, DEREncodable value) {
	this(oid, false, value);
    }
    
    public BouncyCastleX509Extension(String oid, boolean critical, 
				     DEREncodable value) {
	super(oid, critical, null);
	setValue(value);
    }
    
    protected void setValue(DEREncodable value) {
	if (value == null) {
	    return;
	}
    	try {
	    setValue(BouncyCastleUtil.toByteArray(value.getDERObject()));
	} catch (IOException e) {
	    throw new RuntimeException("Failed to convert to byte array: " +
				       e.getMessage());
	}
    }
}
