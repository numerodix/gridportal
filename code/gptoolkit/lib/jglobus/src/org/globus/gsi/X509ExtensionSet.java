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

import java.util.Set;
import java.util.Hashtable;

/** 
 * Represents a set of X.509 extensions.
 */
public class X509ExtensionSet {
   
    private Hashtable extensions;

    /**
     * Creates an empty X509ExtensionSet object.
     */
    public X509ExtensionSet() {
	this.extensions = new Hashtable();
    }

    /**
     * Adds a X509Extension object to this set.
     *
     * @param extension the extension to add
     * @return an extension that was removed with the same oid as the
     *         new extension. Null, if none existed before.
     */
    public X509Extension add(X509Extension extension) {
	if (extension == null) {
	    throw new IllegalArgumentException("extension == null");
	}
	return (X509Extension)this.extensions.put(extension.getOid(),
						  extension);
    }
    
    /**
     * Retrieves X509Extension by given oid.
     *
     * @param oid the oid of the extension to retrieve.
     * @return the extension with the specified oid. Can be null if
     *         there is no extension with such oid.
     */
    public X509Extension get(String oid) {
	if (oid == null) {
	    throw new IllegalArgumentException("oid == null");
	}
	return (X509Extension)this.extensions.get(oid);
    }

    /**
     * Removes X509Extension by given oid.
     *
     * @param oid the oid of the extension to remove.
     * @return extension that was removed. Null, if extension with the
     *         specified oid does not exist in this set.
     */
    public X509Extension remove(String oid) {
	if (oid == null) {
	    throw new IllegalArgumentException("oid == null");
	}
	return (X509Extension)this.extensions.remove(oid);
    }
    
    /**
     * Returns the size of the set.
     *
     * @return the size of the set.
     */
    public int size() {
	return this.extensions.size();
    }
    
    /**
     * Returns if the set is empty.
     *
     * @return true if the set if empty, false otherwise.
     */
    public boolean isEmpty() {
	return this.extensions.isEmpty();
    }
    
    /**
     * Removes all extensions from the set.
     */
    public void clear() {
	this.extensions.clear();
    }
    
    /**
     * Returns a set view of the OIDs of the extensions contained in this
     * extension set.
     *
     * @return the set with oids.
     */
    public Set oidSet() {
	return this.extensions.keySet();
    }
    
}
