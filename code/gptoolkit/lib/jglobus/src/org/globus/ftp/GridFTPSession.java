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

import org.globus.ftp.exception.ClientException;

import org.ietf.jgss.GSSCredential;

/**
   Represents parameters of an FTP session between a client and a server.
 **/
public class GridFTPSession extends Session {

    /**
     * Indicates Extended Block Mode for data transfer. Used with 
     * {@link GridFTPClient#setMode(int) GridFTPClient.setMode()}.
     */
    public static final int MODE_EBLOCK = 3;

    /**
      server in extended passive mode
    **/
    public static final int SERVER_EPAS = 3;
    /**
      server in extended active mode
    **/
    public static final int SERVER_EACT = 4;

    /**
     * Indicates that the data channel will carry the raw data of the file
     * transfer, with no security applied. 
     * Used with {@link GridFTPClient#setDataChannelProtection(int)
     * setDataChannelProtection()}. 
     */
    public static final int PROTECTION_CLEAR = 1;

    /**
     * Indicates that the data will be integrity protected. 
     * Used with {@link GridFTPClient#setDataChannelProtection(int)
     * setDataChannelProtection()}. 
     */
    public static final int PROTECTION_SAFE = 2;

    /**
     * Indicates that the data will be confidentiality protected 
     * (Currently, not support by GridFTP servers).
     * Used with {@link GridFTPClient#setDataChannelProtection(int)
     * setDataChannelProtection()}.
     */
    public static final int PROTECTION_CONFIDENTIAL = 3;

    /**
     * Indicates that the data will be integrity and confidentiality protected.
     * Used with {@link GridFTPClient#setDataChannelProtection(int)
     * setDataChannelProtection()}. 
     */
    public static final int PROTECTION_PRIVATE = 4;

    /* default in gridftp - not in gsiftp */
    public DataChannelAuthentication dataChannelAuthentication = 
	DataChannelAuthentication.SELF;

    public int dataChannelProtection = 
	PROTECTION_CLEAR;

    public GSSCredential credential = null;
    
    public int parallel = 1;

    public int TCPBufferSize = SERVER_DEFAULT;

    /**
       This concerns local server.
       if in SERVER_EPAS mode, the server listener socket list is stored
       here. If in SERVER_PASV mode, the server listener sockets
       is stored in serverAddress variable.
     **/
    public HostPortList serverAddressList = null;

    /**
     * Sets maxWait to twice the
     * time of DEFAULT_MAX_WAIT
     */
    public GridFTPSession() {
	maxWait = 2 * DEFAULT_MAX_WAIT;
    }

    /**
       In addition to the inherited functionality,
       this method also (1) checks if extended active / passive
       server modes are set correctly, 
       (2) checks if Mode E is needed, and if so,
       checks whether it has been set. If not, ClientException is thrown.
     **/
    public void matches(Session other) 
	throws ClientException{

	compareTransferParams(other);
	compareServerMode(other);
	
	if (needsGridFTP() && transferMode != MODE_EBLOCK) {
	    throw new ClientException(ClientException.BAD_MODE,
                                      "Extended block mode necessary");
	}
	
	if (other instanceof GridFTPSession && 
	    ((GridFTPSession)other).needsGridFTP() &&
	    transferMode != MODE_EBLOCK) {
	    throw new ClientException(ClientException.BAD_MODE,
				      "Extended block mode necessary");
	}
    }

    // called by inherited matches() method 
    protected void compareServerMode(Session other) 
	throws ClientException{

	if (transferMode != MODE_EBLOCK) {

	    super.compareServerMode(other);

	} else {
	    if (serverMode == SERVER_DEFAULT 
		&& other.serverMode == SERVER_DEFAULT) {
	    
		// this is OK
	    
	    } else {
		//active and passive side had already been set;
		//make sure that it has been done correctly.
		//in mode E, source must be active and dest passive
		
		if (! ((serverMode == SERVER_EACT && other.serverMode == SERVER_EPAS)
		       || (serverMode == SERVER_EPAS && other.serverMode == SERVER_EACT)
		       || (serverMode == SERVER_ACTIVE && other.serverMode == SERVER_PASSIVE)
		       || (serverMode == SERVER_PASSIVE && other.serverMode == SERVER_ACTIVE))){
		    throw new ClientException(ClientException.BAD_SERVER_MODE,
					      "One server must be active"
					      + " and other must be passive");
		}
	    }
	}
    } // compareServerMode

    /**
       @return true if this session requires GridFTP extensions; false
       if it only requires vanilla FTP.
     **/
    public boolean needsGridFTP() {
	return (parallel > 1 ||
		transferMode == MODE_EBLOCK ||
		(serverMode == GridFTPSession.SERVER_EPAS ||
		 serverMode == GridFTPSession.SERVER_EACT));
    }
}
