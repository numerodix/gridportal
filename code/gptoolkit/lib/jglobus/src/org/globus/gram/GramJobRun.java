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
package org.globus.gram;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class GramJobRun extends GramJob implements Runnable {
    String resourceManagerContact = null;

    private GramJobRun( GSSCredential cred, String rsl ) { 
	super( cred, rsl ); 
    }
    
    private GramJobRun( String rsl ) { 
	super( rsl ); 
    }

    public GramJobRun(	String rsl,
    			String resourceManagerContact ) {
	super( rsl );
    	this.resourceManagerContact = resourceManagerContact;
    }

    public void run() {
	try {
    	    request( resourceManagerContact );
	} catch( GSSException gpe ) {
	    System.err.println( "Error: " + gpe.getMessage() );
	    setStatus( STATUS_FAILED );
	} catch( GramException ge ) {
	    System.err.println( "Error: " + ge.getMessage() );
	    setStatus( STATUS_FAILED );
	}
    }
}
