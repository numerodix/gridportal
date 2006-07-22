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
package org.globus.ftp;

/**
   Options to the command RETR, as defined in GridFTP.
   In client-server transfers, this implementation does not 
   support starting/min/max parallelism. All three values must be equal.
   In third party transfers, this is not necessary.
 */
public class RetrieveOptions extends Options {

    protected int startParallelism;
    protected int minParallelism;
    protected int maxParallelism;
	
    public RetrieveOptions() {
	this(1);
    }

    /**
       @param parallelism required min, max, and starting parallelism 
    */
    public RetrieveOptions(int parallelism) {
	super("RETR");
	this.startParallelism = parallelism;
	this.minParallelism = parallelism;
	this.maxParallelism = parallelism;
    }

    /**
       Use only in third party mode.
     */
    public void setStartingParallelism(int startParallelism) {
	this.startParallelism = startParallelism;
    }

    /**
       Use only in third party mode.
     */
    public void setMinParallelism(int minParallelism) {
	this.minParallelism = minParallelism;
    }
    
    /**
       Use only in third party mode.
     */
    public void setMaxParallelism(int maxParallelism) {
	this.maxParallelism = maxParallelism;
    }

    public int getStartingParallelism() {
	return this.startParallelism;
    }

    public int getMinParallelism() {
	return this.minParallelism;
    }
    
    public int getMaxParallelism() {
	return this.maxParallelism;
    }
    
    public String getArgument() {
	return "Parallelism=" + startParallelism + "," +
	    minParallelism + "," + maxParallelism + ";";
    }

}

    
