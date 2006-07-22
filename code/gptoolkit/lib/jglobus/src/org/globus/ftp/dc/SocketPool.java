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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Represents a set of open sockets that are being cached for subsequent transfers.
 *  CheckIn() a socket to add it to the pool. Other threads can use it. CheckOut() a socket to mark it busy; it will remain in the pool but noone 
 *  else can check it out. Finally, you can remove a socket from the pool, in which case the pool will remove it from all its references.
 */
public class SocketPool {

    private static Log logger = LogFactory.getLog(SocketPool.class.getName());

    protected Hashtable allSockets = new Hashtable();
    protected Hashtable freeSockets = new Hashtable();
    protected Hashtable busySockets = new Hashtable();

    /**
     * Constructor for SocketPool.
     */
    public SocketPool() {
    }

    /** add socketBox to the pool. Depending on its state, it will be added to free or busy sockets. */
    public synchronized void add(SocketBox sb) {
        
        int status = ((ManagedSocketBox) sb).getStatus();
        
        if (allSockets.containsKey(sb)) {
            throw new IllegalArgumentException("This socket already exists in the socket pool.");
        }
        
        allSockets.put(sb, sb);
        
        if (status == ManagedSocketBox.FREE) {
            if (freeSockets.containsKey(sb)) {
                throw new IllegalArgumentException("This socket already exists in the pool of free sockets.");
            }

            logger.debug("adding a free socket");
            freeSockets.put(sb, sb);
        } else {
            if (busySockets.containsKey(sb)) {
                throw new IllegalArgumentException("This socket already exists in the pool of busy sockets.");
            }
            
            logger.debug("adding a busy socket");
            busySockets.put(sb, sb);
        }
    }

    /** remove socketBox from the pool, remove all references to it*/
    public synchronized void remove(SocketBox sb) {

        int status = ((ManagedSocketBox) sb).getStatus();

        if (!allSockets.containsKey(sb)) {
            throw new IllegalArgumentException("This socket does not seem to exist in the socket pool.");
        }

        allSockets.remove(sb);

        if (status == ManagedSocketBox.FREE) {
            if (!freeSockets.containsKey(sb)) {
                throw new IllegalArgumentException("This socket is marked free, but does not exist in the pool of free sockets.");
            }
            freeSockets.remove(sb);

        } else {
            if (!busySockets.containsKey(sb)) {
                throw new IllegalArgumentException("This socket is marked busy, but does not exist in the pool of busy sockets.");
            }
            busySockets.remove(sb);
        }
    }

    /** checks out the next free socket and returns it, or returns null if there aren't any. 
     * Before calling this method, the socket needs to be first add()ed to the pool.
     * */
    public synchronized SocketBox checkOut() {
        Enumeration e = freeSockets.keys();

        if (e.hasMoreElements()) {
            SocketBox sb = (SocketBox)e.nextElement();

            if (busySockets.containsKey(sb)) {
                throw new IllegalArgumentException("This socket is marked free, but already exists in the pool of busy sockets.");
            }
            
            ((ManagedSocketBox) sb).setStatus(ManagedSocketBox.BUSY);
            freeSockets.remove(sb);
            busySockets.put(sb, sb);
            
            return sb;
        } else {
            return null;
        }
    }

    /** Before calling this method, the socket needs to be first add()ed to the pool and checked out. Note: checking in a
     * socket that is not reusable will cause its removal from the pool. */
    public synchronized void checkIn(SocketBox sb) {
        
        if (((ManagedSocketBox) sb).getStatus() != ManagedSocketBox.BUSY) {
            throw new IllegalArgumentException("The socket      is already marked free, cannot check it in twice.");
        }
        
        if (!busySockets.containsKey(sb)) {
            throw new IllegalArgumentException("This socket does not exist in the pool of busy sockets.");
        }
        
        if (freeSockets.containsKey(sb)) {
            throw new IllegalArgumentException("This socket already exists in the pool of free sockets.");
        }
        
        if (! ((ManagedSocketBox)sb).isReusable()) {
            
            throw new IllegalArgumentException("This socket is not reusable; cannot check in.");
            
        }
        
        ((ManagedSocketBox) sb).setStatus(ManagedSocketBox.FREE);
        busySockets.remove(sb);
        freeSockets.put(sb, sb);
        
    }

    /** @return number of all cached sockets */
    public int count() {
        return allSockets.size();
    }
    
    /** @return number of free sockets */
    public int countFree() {
        return freeSockets.size();
    }

    /** @return number of busy sockets */
    public int countBusy() {
        return busySockets.size();
    }

    /** @return true if there is at least 1 free socket */
    public boolean hasFree() {
        return (countFree() > 0);
    }
    
    /** Apply the suplied callback to all socketBoxes.*/
    public synchronized void applyToAll(SocketOperator op) throws Exception {
        Enumeration keys = allSockets.keys();
        while (keys.hasMoreElements()) {
            SocketBox myBox = (SocketBox) keys.nextElement();
            op.operate(myBox);
        }
    }

    /**
     * Forcibly close all sockets, and remove them from the pool.
     * */
    public synchronized void flush() throws IOException {
        
        Enumeration keys = allSockets.keys();
        // close all sockets before removing them
        while (keys.hasMoreElements()) {
            SocketBox myBox = (SocketBox) keys.nextElement();
            if (myBox != null) {
                myBox.setSocket(null);
            }
        }
        
        allSockets.clear();
        freeSockets.clear();
        busySockets.clear();
    }

}
