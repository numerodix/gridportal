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
package org.globus.gsi;

import java.io.IOException;
import java.security.cert.X509Certificate;

import org.globus.gsi.bc.BouncyCastleUtil;

/** 
 * Represents an X.509 extension. It is used to create X.509 extensions
 * and pass them in a <code>X509ExtensionSet</code> during certificate
 * generation.
 */
public class X509Extension {
   
    protected boolean critical;
    protected byte[] value;
    protected String oid;

    /**
     * Creates a X509Extension object with specified oid.
     * The extension has no value and is marked as noncritical.
     *
     * @param oid the oid of the extension
     */
    public X509Extension(String oid) {
	this(oid, false, null);
    }

    /**
     * Creates a X509Extension object with specified oid and value.
     * The extension is marked as noncritical.
     *
     * @param oid the oid of the extension
     * @param value the actual value of the extension (not octet string 
     *        encoded). The value can be null.
     */
    public X509Extension(String oid, byte[] value) {
	this(oid, false, value);
    }

    /**
     * Creates a X509Extension object with specified oid, critical property,
     * and value.
     *
     * @param oid the oid of the extension
     * @param critical the critical value.
     * @param value the actual value of the extension (not octet string 
     *        encoded). The value can be null.
     */
    public X509Extension(String oid, boolean critical, byte[] value) {
	if (oid == null) {
	    throw new IllegalArgumentException("oid == null");
	}
	this.oid = oid;
	this.critical = critical;
	this.value = value;
    }

    /**
     * Sets the oid of this extension.
     *
     * @param oid the oid of this extension. Cannot not null.
     */
    public void setOid(String oid) {
	if (oid == null) {
	    throw new IllegalArgumentException("oid == null");
	}
	this.oid = oid;
    }

    /**
     * Returns the oid of this extension.
     *
     * @return the oid of this extension. Always non-null.
     */
    public String getOid() {
	return this.oid;
    }

    /**
     * Sets the extension as critical or noncritical.
     *
     * @param critical the critical value.
     */
    public void setCritical(boolean critical) {
	this.critical = critical;
    }

    /**
     * Determines whether or not this extension is critical.
     *
     * @return true if extension is critical, false otherwise.
     */
    public boolean isCritical() {
	return this.critical;
    }

    /**
     * Sets the actual value of the extension (not octet string encoded).
     *
     * @param value the actual value of the extension. Can be null.
     */
    public void setValue(byte [] value) {
	this.value = value;
    }

    /**
     * Returns the actual value of the extension (not octet string encoded)
     *
     * @return the actual value of the extension (not octet string encoded).
     *         Null if value not set.
     */
    public byte[] getValue() {
	return this.value;
    }
    
    /**
     * Returns the actual value of the extension.
     *
     * @param cert the certificate that contains the extensions to retrieve.
     * @param oid the oid of the extension to retrieve.
     * @return the actual value of the extension (not octet string encoded)
     * @exception IOException if decoding the extension fails.
     */
    public static byte[] getExtensionValue(X509Certificate cert, String oid) 
	throws IOException {
	if (cert == null) {
	    throw new IllegalArgumentException("cert == null");
	}
	if (oid == null) {
	    throw new IllegalArgumentException("oid == null");
	}
	
	byte [] value = cert.getExtensionValue(oid);
	if (value == null) {
	    return null;
	}
	
	return BouncyCastleUtil.getExtensionValue(value);
    }
}
