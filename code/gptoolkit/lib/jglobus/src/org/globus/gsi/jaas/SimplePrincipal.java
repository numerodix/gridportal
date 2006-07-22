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
package org.globus.gsi.jaas;

import java.security.Principal;

/**
 * Simple string-based principal.
 */
public class SimplePrincipal 
    implements Principal {
    
    private String name;
    
    public SimplePrincipal() {
    }

    public SimplePrincipal(String name) {
	this.name = name;
    }
    
    public String getName() {
	return this.name;
    }
    
    public int hashCode() {
	return (this.name == null) ? 0 : this.name.hashCode();
    }

    public boolean equals(Object another) {
	if (!(another instanceof Principal)) {
	    return false;
	}
	String anotherName = ((Principal)another).getName();
	if (this.name == null) {
	    return (this.name == anotherName);
	} else {
	    return this.name.equals(anotherName);
	}
    }
    
    public String toString() {
	return getName();
    }

}
