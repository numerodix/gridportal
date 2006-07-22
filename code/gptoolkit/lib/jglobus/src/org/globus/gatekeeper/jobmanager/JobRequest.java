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

import java.io.File;

import org.globus.rsl.RslAttributes;

public class JobRequest {
    
    public static final int 
	JOBTYPE_SINGLE   = 0,
	JOBTYPE_MULTIPLE = 1,
	JOBTYPE_MPI      = 2,
	JOBTYPE_CONDOR   = 3;

    private String _executable;
    private String _stdin;
    private String _stdout;
    private String _stderr;
    private File _directory;
    private long _minMemory, _maxMemory;
    private boolean _dryrun;

    private int 
	_count,
	_hostCount, 
	_maxTime, 
	_maxCpuTime,
	_maxWallTime,
	_jobType;
    
    private RslAttributes _rsl;
    
    public JobRequest() {
    }

    public RslAttributes getRsl() {
	return _rsl;
    }

    public void setRslAttributes(RslAttributes rsl) {
	_rsl = rsl;
    }

    public void setExecutable(String exec) {
	_executable = exec;
    }

    public String getExecutable() {
	return _executable;
    }

    // stdin 
    public void setStdin(String stdin) {
        _stdin = stdin;
    }

    public String getStdin() {
	return _stdin;
    }

    // stdout
    public void setStdout(String stdout) {
        _stdout = stdout;
    }

    public String getStdout() {
        return _stdout;
    }

    // stderr
    public void setStderr(String stderr) {
        _stderr = stderr;
    }

    public String getStderr() {
        return _stderr;
    }

    public void setDirectory(File dir) {
	_directory = dir;
    }

    public File getDirectory() {
	return _directory;
    }

    public void setCount(int count) {
	if (count < 1) {
	    throw new IllegalArgumentException("Count must be greater then 0");
	}
	_count = count;
    }

    public int getCount() {
	return _count;
    }

    public void setMinMemory(long memory) {
	_minMemory = memory;
    }

    public long getMinMemory() {
	return _minMemory;
    }

    public void setMaxMemory(long memory) {
        _maxMemory = memory;
    }

    public long getMaxMemory() {
        return _maxMemory;
    }

    public void setDryRun(boolean dryrun) {
	_dryrun = dryrun;
    }

    public boolean isDryRun() {
	return _dryrun;
    }

    public void setHostCount(int count) {
	if (count < 1) {
            throw new IllegalArgumentException("Host count must be greater then 0");
        }
        _hostCount = count;
    }
    
    public int getHostCount() {
        return _hostCount;
    }

    public void setMaxWallTime(int time) {
        _maxWallTime = time;
    }

    public int getMaxWallTime() {
        return _maxWallTime;
    }

    public void setMaxTime(int time) {
        _maxTime = time;
    }

    public int getMaxTime() {
        return _maxTime;
    }

    public void setMaxCpuTime(int time) {
        _maxCpuTime = time;
    }

    public int getMaxCpuTime() {
        return _maxCpuTime;
    }
    
    public void setJobType(int jobType) {
	_jobType = jobType;
    }

    public int getJobType() {
	return _jobType;
    }

}
