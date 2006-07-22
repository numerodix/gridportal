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

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Tail implements Runnable {

    private static final int CHUNK_SIZE = 2048;

    private byte [] buffer;
    private boolean _stop = false;
    private List list = null;
    private Thread _thread;

    private Log _logger;
    
    public Tail() {
	buffer = new byte[CHUNK_SIZE];
	list = Collections.synchronizedList(new LinkedList());
    }
    
    public void setLogger(Log logger) {
	_logger = logger;
    }

    public void start() {
	_thread = new Thread(this);
	_thread.start();
    }

    class FileWatcher {
	private RandomAccessFile _ras;
	private OutputStream _out;
	private long _pos;
	
	public FileWatcher(File file, OutputStream out, int pos) 
	    throws IOException {
	    _ras = new RandomAccessFile(file, "r");
	    _out = out;
	    _pos = pos;
	}

	public void init() 
	    throws IOException {
	    _ras.seek(_pos);
	}
	
	public long getDiff() 
	    throws IOException {
	    return _ras.length() - _pos;
	}

	public void moveBuffer(byte [] buffer, int size) 
	    throws IOException {
	    _ras.readFully(buffer, 0, size);
	    _pos += size;

	    if (_logger.isDebugEnabled()) {
                _logger.debug("[tail] output size: " + size);
            }
	    
	    _out.write(buffer, 0, size);
	}

	public void close() {
	    try {
		_ras.close();
	    } catch(Exception e) {}
	    try {
		_out.close();
	    } catch (Exception e) {}
	}
    }

    public void join() 
	throws InterruptedException {
	_thread.join();
    }

    public void addFile(File file, OutputStream out, int pos) 
	throws IOException {
	list.add(new FileWatcher(file, out, pos));
    }

    public void run() {
	
	_logger.debug("[tail] running...");
	
	long len;
	int size;
	
	Iterator iter = null;
	FileWatcher watcher = null;

	try {
	    iter = list.iterator();
	    while(iter.hasNext()) {
		watcher = (FileWatcher)iter.next();
		watcher.init();
	    }

	    while(!isDone()) {
		
		try {
		    Thread.sleep(2000);
		} catch(Exception e) {
		}

		iter = list.iterator();
		while(iter.hasNext()) {
		    watcher = (FileWatcher)iter.next();

		    len = watcher.getDiff();
		    if (len <= 0) continue;

		    while(len > 0) {
			size = (len > CHUNK_SIZE) ? CHUNK_SIZE : (int)len;
			watcher.moveBuffer(buffer, size);
			len -= size;
		    }
		}
	    }
	} catch(IOException e) {
	    _logger.debug("Unexpected error.", e);
	} finally {
	    close();
	}
	
	_logger.debug("[tail] done.");
    }
    
    private boolean isDone() 
	throws IOException {
	if (!_stop) return false;
	Iterator iter = null;
        FileWatcher watcher = null;
	
	iter = list.iterator();
	while(iter.hasNext()) {
	    watcher = (FileWatcher)iter.next();
	    if (watcher.getDiff() > 0) return false;
	}
	
	return true;
    }
    
    private void close() {
        Iterator iter = null;
        FileWatcher watcher = null;
        iter = list.iterator();
        while(iter.hasNext()) {
            watcher = (FileWatcher)iter.next();
            watcher.close();
        }
    }
    
    public void stop() {
	_logger.debug("[tail] stop called");
	_stop = true;
    }

}
