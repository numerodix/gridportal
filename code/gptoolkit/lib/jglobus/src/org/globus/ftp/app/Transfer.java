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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.globus.ftp.HostPort;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.HostPortList;
import org.globus.ftp.RetrieveOptions;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.exception.ClientException;

import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;

/**
   Represents a single act of a third party transfer of one file.
   The transfer is performed in the constructor.
   This class will not be very efficient for transferring many files,
   because each transfer builds its own control channel connection.
   Instead, it is appropriate to use this class instances for transfers of single files,
   or for testing server's capabilities.
   @see TransferParams
 */
public class Transfer {

    static Log logger = LogFactory.getLog(Transfer.class.getName());

    public static void main(String[] args) {

	String sourceServer = null, sourceDir = null, sourceFile = null, 
	    destServer = null, destDir= null, destFile= null;
	int sourcePort = 0, destPort = 0;

	try {
	    if ( args.length < 8) {
		throw new Exception();
	    }
	     sourceServer = args[0];
	     sourcePort = Integer.parseInt(args[1]);
	     sourceDir = args[2];
	     sourceFile = args[3];
	     destServer = args[4];
	     destPort =  Integer.parseInt(args[5]);
	     destDir = args[6];
	     destFile = args[7];
	} catch (Exception e) {
	    System.err.println("\nUsage:"); 
	    System.err.println("Transfer \\");
	    System.err.println("sourceServer sourcePort sourceDir sourceFile \\");
	    System.err.println("destServer destPort destDir destFile");
	    System.exit(-1);
	}
	
	try {
	    logger.info("starting");
	    
	    TransferParams params = new TransferParams();
	    
	    
	    Transfer transfer = new Transfer(sourceServer,
					     sourcePort,
					     sourceDir,
					     sourceFile,
					     destServer,
					     destPort,
					     destDir,
					     destFile,
					     params);
	} catch (Exception e) {
	    System.err.println("Transfer failed: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(-1);
	}
	
    }

    /**
       Constructor, performing a single third party transfer from
       (grid)ftp://sourceServer:sourcePort/sourceDir/sourceFile
       to (grid)ftp://destServer:destPort/destDir/destFile.
       Any protocol related parameters should be passed in the params
       object. If params is null, default GridFTP parameters are assumed.
     */
    public Transfer(String sourceServer,
		    int sourcePort,
		    String sourceDir,
		    String sourceFile,
		    String destServer,
		    int destPort,
		    String destDir,
		    String destFile,
		    TransferParams params) 
	throws IOException, 
	       ServerException,
	       ClientException {
	this(sourceServer, sourcePort, (String)null, sourceDir, sourceFile,
	     destServer, destPort, (String)null, destDir, destFile,
	     params);
    }

    /**
       Constructor, performing a single third party transfer from
       (grid)ftp://sourceServer:sourcePort/sourceDir/sourceFile
       to (grid)ftp://destServer:destPort/destDir/destFile.
       Any protocol related parameters should be passed in the params
       object. If params is null, default GridFTP parameters are assumed.
     */
    public Transfer(String sourceServer,
		    int sourcePort,
		    String sourceSubject,
		    String sourceDir,
		    String sourceFile,
		    String destServer,
		    int destPort,
		    String destSubject,
		    String destDir,
		    String destFile,
		    TransferParams params) 
	throws IOException, 
	       ServerException,
	       ClientException {

	this(sourceServer,
	     sourcePort,
	     getAuthorization(sourceSubject),
	     sourceDir + "/" + sourceFile,
	     destServer,
	     destPort,
	     getAuthorization(destSubject),
	     destDir + "/" + destFile,
	     params);
    }

    /**
       Constructor, performing a single third party transfer from
       (grid)ftp://sourceServer:sourcePort/absoluteSourceFile
       to (grid)ftp://destServer:destPort/absoluteDestFile.
       Any protocol related parameters should be passed in the params
       object. If params is null, default GridFTP parameters are assumed.
     */
    public Transfer(String sourceServer,
		    int sourcePort,
		    Authorization sourceSubject,
		    String absoluteSourceFile,
		    String destServer,
		    int destPort,
		    Authorization destSubject,
		    String absoluteDestFile,
		    TransferParams params) 
	throws IOException,
	       ServerException,
	       ClientException{

	if (params == null) {
	    params = new TransferParams(); //with default values
	}

	GridFTPClient source = new GridFTPClient(sourceServer, sourcePort);
	source.setAuthorization(sourceSubject);

	GridFTPClient dest = new GridFTPClient(destServer, destPort);
	dest.setAuthorization(destSubject);

	setParams(source, params);
	setParams(dest, params);

	GridFTPClient active, passive;
	if (params.serverMode != params.SERVER_PASSIVE) { // default 
	    active = source;
	    passive = dest;
	} else { // non default
	    active = dest;
	    passive = source;
	}

	if (!params.doStriping) {
	    HostPort hp = passive.setPassive();
	    active.setActive(hp);
	} else {
	    HostPortList hpl = passive.setStripedPassive();
	    active.setStripedActive(hpl);
	}
	
	if (params.transferMode != params.MODE_EBLOCK) {
	    source.transfer(absoluteSourceFile, 
			    dest, 
			    absoluteDestFile, 
			    false,
			    params.markerListener);
	} else {
	    source.extendedTransfer(absoluteSourceFile, 
				    dest, 
				    absoluteDestFile, 
				    params.markerListener);
	}
    }
    

    private static Authorization getAuthorization(String subject) {
	if (subject == null) {
	    return HostAuthorization.getInstance();
	} else {
	    return new IdentityAuthorization(subject);
	}
    }

    private void setParams(GridFTPClient client, final TransferParams params)
	throws IOException,
	       ServerException{
	client.authenticate(params.credential); //okay if null

	if (params.transferType != params.SERVER_DEFAULT) {
	    client.setType(params.transferType);
	}

	if (params.transferMode != params.SERVER_DEFAULT) {
	    client.setMode(params.transferMode);
	}

	if (params.parallel != params.SERVER_DEFAULT) {
	    client.setOptions(new RetrieveOptions(params.parallel));
	}

	if (params.protectionBufferSize != params.SERVER_DEFAULT) {
	    client.setProtectionBufferSize(
	        params.protectionBufferSize);
	}

	if (params.dataChannelAuthentication != null) {
	    client.setDataChannelAuthentication(
	        params.dataChannelAuthentication);
	}
	
	if (params.dataChannelProtection != params.SERVER_DEFAULT) {
	    client.setDataChannelProtection(
	        params.dataChannelProtection);
	}

	if (params.TCPBufferSize != params.SERVER_DEFAULT) {
	    client.setTCPBufferSize(params.TCPBufferSize);
	}
    }
}

