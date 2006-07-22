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

import org.globus.common.ChainedGeneralSecurityException;

/**
 */
public class ProxyPathValidatorException 
    extends ChainedGeneralSecurityException {
    
    public static final int FAILURE = -1;

    // proxy constraints violation
    public static final int PROXY_VIOLATION = 1;

    // unsupported critical extensions
    public static final int UNSUPPORTED_EXTENSION = 2;

    // proxy or CA path length exceeded
    public static final int PATH_LENGTH_EXCEEDED = 3;

    // unknown CA
    public static final int UNKNOWN_CA = 4;

    // unknown proxy policy
    public static final int UNKNOWN_POLICY = 5;

    // cert revoked
    public static final int REVOKED = 6;

    // limited proxy not accepted
    public static final int LIMITED_PROXY_ERROR = 7;

    private X509Certificate cert;

    private int errorCode = FAILURE;
    
    public ProxyPathValidatorException(int errorCode) {
	this(errorCode, null);
    }

    public ProxyPathValidatorException(int errorCode,
				       Throwable root) {
	this(errorCode, "", root);
    }

    public ProxyPathValidatorException(int errorCode,
				       String msg,
				       Throwable root) {
	super(msg, root);
	this.errorCode = errorCode;
    }

    public ProxyPathValidatorException(int errorCode,
				       X509Certificate cert,
				       String msg) {
	super(msg, null);
	this.errorCode = errorCode;
	this.cert = cert;
    }
    
    public int getErrorCode() {
	return this.errorCode;
    }
    

    /**
     * Returns the certificate that was being validated when
     * the exception was thrown.
     *
     * @return the <code>Certificate</code> that was being validated when
     * the exception was thrown (or <code>null</code> if not specified)
     */
    public X509Certificate getCertificate() {
	return this.cert;
    }
    
}
