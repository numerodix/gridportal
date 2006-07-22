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
package org.globus.util;

public class CircularBuffer {
	
    protected Object[] buf;
    protected int in = 0;
    protected int out= 0;
    protected int count= 0;
    protected int size;
    
    protected boolean interruptPut = false;
    protected boolean interruptGet = false;
    protected boolean closePut = false;

    public CircularBuffer(int size) {
	this.size = size;
	buf = new Object[size];
    }
    
    public synchronized boolean isEmpty() {
	return (this.count == 0);
    }

    public synchronized boolean put(Object o) 
	throws InterruptedException {
	if (this.interruptPut) {
	    return false;
	}
	while (count==size) {
	    wait();
	    if (this.interruptPut) {
		return false;
	    }
	}
	buf[in] = o;
	++count;
	in=(in+1) % size;
	notify();
	return true;
    }
    
    public synchronized Object get() 
	throws InterruptedException {
	if (this.interruptGet) {
	    return null;
	}
	while (count==0) {
	    if (this.closePut) {
		return null;
	    }
	    wait();
	    if (this.interruptGet) {
		return null;
	    }
	}
	Object o =buf[out];
	buf[out]=null;
	--count;
	out=(out+1) % size;
	notify();
	return (o);
    }
    
    public synchronized void closePut() {
	this.closePut = true;
	notifyAll();
    }

    public synchronized boolean isPutClosed() {
	return this.closePut;
    }

    public synchronized void interruptBoth() {
	this.interruptGet = true;
	this.interruptPut = true;
	notifyAll();
    }
    
    public synchronized void interruptGet() {
	this.interruptGet = true;
	notifyAll();
    }

    public synchronized void interruptPut() {
	this.interruptPut = true;
	notifyAll();
    }

    public synchronized boolean isGetInterrupted() {
	return this.interruptGet;
    }

    public synchronized boolean isPutInterrupted() {
	return this.interruptPut;
    }
    
}
