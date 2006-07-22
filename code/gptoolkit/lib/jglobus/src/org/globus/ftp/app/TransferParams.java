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
package org.globus.ftp.app;

import org.globus.ftp.GridFTPSession;
import org.globus.ftp.MarkerListener;

/**
   Represents parameters of a transfer. This class should be used in conjunction
   with class Transfer. You first configure a TransferParams object by setting values directly
   (parameters are public, there's no getter/setter methods). Then 
   pass this object to a Transfer constructor that will perform the transfer as required.
   <p>
   In general, the meaning of a parameter values is as follows. A value of
   SERVER_DEFAULT (in case of integer values) or null (in case of objects) indicate
   that this parameter should not be explicitly set to the server, 
   and the server will use its defaults.
   Any other value will be explicitly requested from the server
   on the control channel.
   <p>
   For example, if transferMode is set to SERVER_DEFAULT, client will not directly
   request any transfer mode, and the server should use its default which is stream mode.
   If transferMode is set to MODE_STREAM, stream mode will be explicitly requested
   from the server, so it will also use stream mode.
   To request extended block mode, set transferMode to MODE_EBLOCK.
   <p>
   Below is a list of parameters that can be set to TransferParams and can affect the transfer:
   <ul>
   <li> credential - user's credential; use null for default user's credential
   <li> transferMode - transfer mode
   <li> transferType - transfer type
   <li> serverMode - SERVER_ACTIVE (default) if the <b>sending</b> server should be active, otherwise SERVER_PASSIVE (this is illegal with Mode E)
   <li> parallel - parallelism (integer number of parallel streams)
   <li> doStriping - set to true to request striping, otherwise false (default)
   <li> protectionBufferSize - protection buffer size; in GridFTP must be set to something
   <li> dataChannelAuthentication - DataChannelAuthentication.SELF (default) or DataChannelAuthentication.NONE
   <li> dataChannelProtection - PROTECTION_CLEAR (default), PROTECTION_SAFE, ...
   <li> TCPBufferSize - TCP buffer size, default is usually 64 KB
   <li> markerListener - an object listening for performance and restart markers, null by default
   </ul>
   @see Transfer
 */
public class TransferParams extends GridFTPSession {

    public boolean doStriping = false;
    public MarkerListener markerListener;

    /** 
	This constructor sets parameters to the GridFTP defaults.
	If possible, a parameter is not explicitly set, but server defaults are assumed.
     */
    public TransferParams() {
	credential = null;                      // use default credential
	transferMode = MODE_EBLOCK;             // must be set; server default would be MODE_STREAM
	transferType = TYPE_IMAGE;
	serverMode = SERVER_DEFAULT;
	parallel = SERVER_DEFAULT;              // server default = SERVER_ACTIVE
	doStriping = false;
	protectionBufferSize = 16384;           // must set to something
	dataChannelAuthentication =null;        // server default = DataChannelAuthentication.SELF;
	dataChannelProtection = SERVER_DEFAULT; // server default = PROTECTION_CLEAR;
	TCPBufferSize = SERVER_DEFAULT;
	markerListener = null;
    }
}
