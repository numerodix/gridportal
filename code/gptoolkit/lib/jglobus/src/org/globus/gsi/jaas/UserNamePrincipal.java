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

/**
 * An user name principal. Represents the user name (the account) the user 
 * is mapped in the gridmap file.
 */
public class UserNamePrincipal 
    extends SimplePrincipal {
    
    public UserNamePrincipal(String globusDn) {
	super(globusDn);
    }
    
}
