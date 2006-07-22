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

import java.io.Serializable;

public class PasswordCredential implements Serializable {

    private char[] password;
    
    public PasswordCredential(String password) {
	this.password = password.toCharArray();
    }
    
    public String getPassword() {
	return new String(this.password);
    }
    
    public boolean equals(Object another) {
	if (!(another instanceof PasswordCredential)) {
	    return false;
	}
	String pass = ((PasswordCredential)another).getPassword();
	if (this.password == null) {
	    return (pass == null);
	} else {
	    return (new String(this.password)).equals(pass);
	}
    }
    
    public String toString() {
	return getPassword();
    }
}
