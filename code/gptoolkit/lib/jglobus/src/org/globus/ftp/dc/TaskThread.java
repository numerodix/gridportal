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
package org.globus.ftp.dc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskThread implements Runnable {
    
    static final int MAX_TASK_QUEUE = 100;

    protected static Log logger = 
        LogFactory.getLog(TaskThread.class.getName());

    protected Buffer buffer;
    protected boolean stop;
    protected Thread thread;

    public TaskThread() {
        buffer = new Buffer(MAX_TASK_QUEUE);
    }
    
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void runTask(Task task) {
        start();
        try {
            buffer.put(task);
        } catch (Exception e) {
        }
    }

    public Task getNextTask() {
        try {
            return (Task)buffer.get();
        } catch (Exception e) {
            return null;
        }
    }

    public void run() {
        stop = false;
        Exception exception;
        Task task;
        while(!stop) {
            task = getNextTask();
            if (task == null) break;
            exception = null;
            try {
                logger.debug("executing task: " + task.toString());
                task.execute();
                logger.debug("finished task: " + task.toString());
            } catch (Exception e) {
                exception = e;
            }
            task.setComplete(exception);
        }
    }

    public synchronized void stop() {
        stop = true;
        buffer.release();
    }

    public void join() {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    class Buffer {
        
        protected Object[] buf;
        protected int in = 0;
        protected int out= 0;
        protected int count= 0;
        protected int size;

        public Buffer(int size) {
            this.size = size;
            buf = new Object[size];
        }

        public synchronized void put(Object o) 
            throws InterruptedException {
            while (count==size) {
                wait();
                if (stop) return;
            }
            buf[in] = o;
            ++count;
            in=(in+1) % size;
            notify();
        }

        public synchronized Object get() 
            throws InterruptedException {
            while (count==0) {
                wait();
                if (stop) return null;
            }
            Object o =buf[out];
            buf[out]=null;
            --count;
            out=(out+1) % size;
            notify();
            return (o);
        }

        public synchronized void release() {
            notify();
        }
            
    }

}
