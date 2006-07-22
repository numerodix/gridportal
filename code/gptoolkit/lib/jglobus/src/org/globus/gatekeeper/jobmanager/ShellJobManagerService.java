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
package org.globus.gatekeeper.jobmanager;

import org.globus.gatekeeper.ServiceException;

public class ShellJobManagerService extends JobManagerService {

    public ShellJobManagerService() {
        super( new ShellJobManager() );
    }
    
    public void setArguments(String [] args)
        throws ServiceException {
	
	ShellJobManager jobManager = (ShellJobManager)_jobManager;
	
	for (int i=0;i<args.length;i++) {
	    
	    if (args[i].equalsIgnoreCase("-type")) {
		jobManager.setType( args[++i] );
	    } else if (args[i].equalsIgnoreCase("-libexec")) {
		jobManager.setLibExecDirectory( args[++i] );
	    }
	    
	}
    }

}


