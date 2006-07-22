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

import java.util.Properties;

import org.ietf.jgss.GSSCredential;

/**
 * JobManager interface provides the common methods that is used by all extensions
 * of JobManager
 */
public interface JobManager {

    public void setCredentials(GSSCredential credentials);
    
    public GSSCredential getCredentials();

    public String getID();

    public void setID(String id);

    public Properties getSymbolTable();

    public void request(String rsl) 
	throws JobManagerException;
    
    /**
     * Retrieves the current state of the process.
     * @return the current status value
     */
    public int getStatus();
    
    /**
     * Retrieves the failure code of the running process.
     * By default it is equal to zero
     * @return the current failure code
     */
    public int getFailureCode();
    
    /**
     * Cancels the job.
     */
    public void cancel() 
	throws JobManagerException;

    /**
     * Sends a signal to the JobManager.
     */
    public void signal(int signal, String argument) 
	throws JobManagerException;
    
    
    public void addJobStatusListener(JobStatusListener listener)
	throws JobManagerException;
    
    public void removeJobStatusListener(JobStatusListener listenter)
	throws JobManagerException;
    
    public void removeJobStatusListenerByID(String id)
	throws JobManagerException;
    
}
