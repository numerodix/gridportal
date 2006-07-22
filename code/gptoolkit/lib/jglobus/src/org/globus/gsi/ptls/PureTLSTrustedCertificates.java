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
package org.globus.gsi.ptls;

import java.util.Vector;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.globus.gsi.TrustedCertificates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A PureTLS-optimized version of the <code>TrustedCertificates</code> code.
 */
public class PureTLSTrustedCertificates extends TrustedCertificates {

    private static Log logger = 
	LogFactory.getLog(PureTLSTrustedCertificates.class.getName());

    private static PureTLSTrustedCertificates ptlsCerts = null;

    private TrustedCertificates tc;
    private Vector certList = null;

    protected PureTLSTrustedCertificates() {
    }

    public PureTLSTrustedCertificates(TrustedCertificates tc) {
	this.tc = tc;
    }
    
    protected void setTrustedCertificates(TrustedCertificates tc) {
	this.tc = tc;
    }

    public X509Certificate[] getCertificates() {
	return this.tc.getCertificates();
    }
    
    public X509Certificate getCertificate(String subject) {
	return this.tc.getCertificate(subject);
    }

    public synchronized void reload(String locations) {
	this.tc.reload(locations);
    }

    /**
     * Returns the trusted certificates as a Vector of X509Cert objects.
     */
    public synchronized Vector getX509CertList() {
	if (this.tc.isChanged() || this.certList == null) {
	    try {
		this.certList = 
		    PureTLSUtil.certificateChainToVector(getCertificates());
	    } catch (GeneralSecurityException e) {
		logger.debug("Failed to convert certificates", e);
	    }
	}
	return this.certList;
    }
    
    public static synchronized PureTLSTrustedCertificates 
	getDefaultPureTLSTrustedCertificates() {
	if (ptlsCerts == null) {
	    ptlsCerts = new PureTLSTrustedCertificates();
	}
	ptlsCerts.setTrustedCertificates(TrustedCertificates.getDefault());
	return ptlsCerts;
    }
    
}
