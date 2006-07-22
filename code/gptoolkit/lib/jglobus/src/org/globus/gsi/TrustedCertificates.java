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

import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Iterator;
import java.io.File;
import java.io.FilenameFilter;

import org.globus.common.CoGProperties;
import org.globus.util.TimestampEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TrustedCertificates {
    
    private static Log logger =
	LogFactory.getLog(TrustedCertificates.class.getName());

    public static final CertFilter certFileFilter = new CertFilter();

    private static TrustedCertificates trustedCertificates = null;
    
    private Map certSubjectDNMap;
    private Map certFileMap;
    private boolean changed;
    
    protected TrustedCertificates() {}
    
    public TrustedCertificates(X509Certificate [] certs) {
	this.certSubjectDNMap = new HashMap();
	for (int i=0;i<certs.length;i++) {
	    this.certSubjectDNMap.put(certs[i].getSubjectDN().toString(), 
				      certs[i]);
	}
    }

    public X509Certificate[] getCertificates() {
	if (this.certSubjectDNMap == null) {
	    return null;
	}
	Collection certs = this.certSubjectDNMap.values();
	X509Certificate [] retCerts = new X509Certificate[certs.size()];
	Iterator iterator = certs.iterator();
	int i = 0;
	while (iterator.hasNext()) {
	    retCerts[i++] = (X509Certificate)iterator.next();
	}
	return retCerts;
    }
    
    public X509Certificate getCertificate(String subject) {
	if (this.certSubjectDNMap == null) {
	    return null;
	}
	return (X509Certificate)this.certSubjectDNMap.get(subject);
    }

    /** 
     * Loads X509 certificates from specified locations. The locations
     * can be either files or directories. The directories will be
     * automatically traversed and all files in the form of 
     * <i>hashcode.number</i> will be loaded automatically.
     *
     * @param locations a list of certificate files/directories to load 
     *                  the certificates from. The locations are comma 
     *                  separated.
     *
     * @return <code>java.security.cert.X509Certificate</code> an array 
     *         of loaded certificates
     */    
    public static X509Certificate[] loadCertificates(String locations) {
	TrustedCertificates tc = TrustedCertificates.load(locations);
	return (tc == null) ? null : tc.getCertificates();
    }

    public static TrustedCertificates load(String locations) {
	TrustedCertificates tc = new TrustedCertificates();
	tc.reload(locations);
	return tc;
    }
    
    public static FilenameFilter getCertFilter() {
	return certFileFilter;
    }
    
    public static class CertFilter implements FilenameFilter {
	public boolean accept(File dir, String file) {
	    int length = file.length();
	    if (length > 2 && 
		file.charAt(length-2) == '.' &&
		file.charAt(length-1) >= '0' && 
		file.charAt(length-1) <= '9') return true;
	    return false;
	}
    }
    
    public synchronized void reload(String locations) {
	if (locations == null) {
	    return;
	}

	this.changed = false;

	StringTokenizer tokens = new StringTokenizer(locations, ",");
	File caFile            = null;

	Map newCertFileMap = new HashMap();
	Map newCertSubjectDNMap = new HashMap();

	while(tokens.hasMoreTokens()) {
	    caFile = new File(tokens.nextToken().toString().trim());

	    if (!caFile.canRead()) {
                logger.debug("Cannot read: " + caFile.getAbsolutePath());
                continue;
            }
            
	    if (caFile.isDirectory()) {
		String[] caCertFiles = caFile.list(getCertFilter());
                if (caCertFiles == null) {
                    logger.debug("Cannot load certificates from " + 
                                 caFile.getAbsolutePath() + " directory.");
                } else {
                    logger.debug("Loading certificates from " + 
                                 caFile.getAbsolutePath() + " directory.");
                    for (int i = 0; i < caCertFiles.length; i++) {
                        String caFilename = caFile.getPath() + 
                            File.separatorChar + caCertFiles[i];
                        File caFilenameFile = new File(caFilename);
                        if (caFilenameFile.canRead()) {
                            loadCert(caFilename,
                                     caFilenameFile.lastModified(),
                                     newCertFileMap, newCertSubjectDNMap);
                        } else {
                            logger.debug("Cannot read: " + 
                                         caFilenameFile.getAbsolutePath());
                        }
                    }
                }
	    } else {
		loadCert(caFile.getAbsolutePath(),
			 caFile.lastModified(),
			 newCertFileMap, newCertSubjectDNMap);
	    }
	}
	
	// in case certificates were removed
	if (!this.changed && 
	    this.certFileMap != null && 
	    this.certFileMap.size() != newCertFileMap.size()) {
	    this.changed = true;
	}

	this.certFileMap = newCertFileMap;
	this.certSubjectDNMap = newCertSubjectDNMap;
    }

    /**
     * Method loads a certificate provided a mapping for it is<br>
     * a) Not already in the HashMap
     * b) In the HashMap, but
     *    - mapped to null object
     *    - the CertEntry has a modified time that is older that latest time
     */
    private void loadCert(String certPath, 
			  long latestLastModified, 
			  Map newCertFileMap,
			  Map newCertSubjectDNMap) {
	X509Certificate cert = null;
	
	if (this.certFileMap == null) {
	    this.certFileMap = new HashMap();
	}
	
	TimestampEntry certEntry = 
	    (TimestampEntry)this.certFileMap.get(certPath);
	try {
	    if (certEntry == null) {
		logger.debug("Loading " + certPath + " certificate.");
		cert = CertUtil.loadCertificate(certPath);
		certEntry = new TimestampEntry(cert, latestLastModified);
		this.changed = true;
	    } else if (latestLastModified > certEntry.getLastModified()) {
		logger.debug("Reloading " + certPath + " certificate.");
		cert = CertUtil.loadCertificate(certPath);
		certEntry.setValue(cert);
		certEntry.setLastModified(latestLastModified);
		this.changed = true;
	    } else {
		logger.debug("Certificate " + certPath + " is up-to-date.");
		cert = (X509Certificate)certEntry.getValue();
	    }
	    newCertFileMap.put(certPath, certEntry);
	    newCertSubjectDNMap.put(cert.getSubjectDN().getName(), cert);
	} catch (Exception e) {
	    logger.error("Certificate " + certPath + " failed to load.", e);
	}
    }
    
    /**
     * Indicates if the last reload caused new certificates to be loaded or
     * existing certificates to be reloaded or any certificates removed
     */
    public boolean isChanged() {
	return this.changed;
    }

    /**
     * Obtains the default set of trusted certificates. 
     *
     * @return TrustedCertificates object.
     */
    public static synchronized TrustedCertificates getDefaultTrustedCertificates() {
	return getDefault();
    }

    /**
     * Sets the default set of trusted certificates to use.
     *
     * @param trusted the new set of trusted certificates to use.
     */    
    public static void setDefaultTrustedCertificates(TrustedCertificates trusted) {
	trustedCertificates = trusted;
    }
    
    /**
     * Obtains the default set of trusted certificates.
     *
     * @return TrustedCertificates object. 
     */
    public static synchronized TrustedCertificates getDefault() {
	if (trustedCertificates == null) {
	    trustedCertificates = new TrustedCertificates();
	}
	trustedCertificates.reload(CoGProperties.getDefault().getCaCertLocations());
	return trustedCertificates;
    }
    
    public String toString() {
	if (this.certSubjectDNMap == null) {
	    return  "cert list is empty";
	} else {
	    return this.certSubjectDNMap.toString();
	}
    }

}
