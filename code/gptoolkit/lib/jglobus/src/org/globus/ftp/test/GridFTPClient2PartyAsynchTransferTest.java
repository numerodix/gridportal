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
package org.globus.ftp.test;

import org.globus.ftp.Session;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.InputStreamDataSink;
import org.globus.ftp.OutputStreamDataSource;
import org.globus.ftp.vanilla.TransferState;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
   Test GridFTPClient.get() and put()
 **/
public class GridFTPClient2PartyAsynchTransferTest extends GridFTPClient2PartyTransferTest {

    protected static Log logger = 
	LogFactory.getLog(GridFTPClient2PartyAsynchTransferTest.class.getName());

    public GridFTPClient2PartyAsynchTransferTest(String name) {
	super(name);
    }

    public static void main (String[] args) throws Exception{
	junit.textui.TestRunner.run(suite());	
    }

    public static Test suite ( ) {
	return new TestSuite(GridFTPClient2PartyAsynchTransferTest.class);
    }

    public void testGetPassive() throws Exception {
	testGet(Session.SERVER_ACTIVE);
    }

    public void testGetActive() throws Exception {
	//testGet(Session.SERVER_PASSIVE);
    }

    public void testPutPassive() throws Exception {
	testPut(Session.SERVER_ACTIVE);
    }

    public void testPutActive() throws Exception {
	//testPut(Session.SERVER_PASSIVE);
    }


    protected void get(GridFTPClient client, 
		       int localServerMode,
		       int transferType,
		       int transferMode,
		       DataChannelAuthentication dcau,
		       int prot,
		       String fullLocalFile,
		       String fullRemoteFile) 
	throws Exception{
	client.authenticate(getCredential()); /* use default cred */
	client.setProtectionBufferSize(16384);
	client.setType(transferType);
	client.setMode(transferMode);
	client.setDataChannelAuthentication(dcau);
 	client.setDataChannelProtection(prot);
	if (localServerMode == Session.SERVER_ACTIVE) {
	    client.setPassive();
	    client.setLocalActive();
	} else {
	    if (TestEnv.localServerPort == TestEnv.UNDEFINED) {
		client.setLocalPassive(); 
	    } else {
		client.setLocalPassive(TestEnv.localServerPort, 
				       FTPServerFacade.DEFAULT_QUEUE);
	    }

	    client.setActive();
	}

	InputStreamDataSink sink =
	    new InputStreamDataSink();

	TransferState s = client.asynchGet(fullRemoteFile,
					   sink,
					   null);

	FileOutputStream out = new FileOutputStream(fullLocalFile);
	InputStream in = sink.getInputStream();
	byte [] buff = new byte[2048];
	int bytes = 0;
	while ( (bytes = in.read(buff)) != -1 ) {
	    out.write(buff, 0, bytes);
	    logger.debug("wrote: " + bytes);
	}
	out.close();
	in.close();

	s.waitForEnd();
    }

    /**
       This method performs the actual transfer
     **/
    protected void put(GridFTPClient client, 
		       int localServerMode,
		       int transferType,
		       int transferMode,
		       DataChannelAuthentication dcau,
		       int prot,
		       String fullLocalFile,
		       String fullRemoteFile) 
	throws Exception{
	client.authenticate(getCredential()); /* use default cred */
	client.setProtectionBufferSize(16384);
	client.setType(transferType);
	client.setMode(transferMode);
 	client.setDataChannelAuthentication(dcau);
 	client.setDataChannelProtection(prot);

	if (localServerMode == Session.SERVER_ACTIVE) {
	    client.setPassive();
	    client.setLocalActive();
	} else {
	    if (TestEnv.localServerPort == TestEnv.UNDEFINED) {
		client.setLocalPassive(); 
	    } else {
		client.setLocalPassive(TestEnv.localServerPort, 
				       FTPServerFacade.DEFAULT_QUEUE);
	    }
	    client.setActive();
	}

	logger.debug("sending file " + fullLocalFile);
	OutputStreamDataSource source = new OutputStreamDataSource(2048);
	
	TransferState s = client.asynchPut(fullRemoteFile,
					   source,
					   null);
	
	FileInputStream in = new FileInputStream(fullLocalFile);
	OutputStream out = source.getOutputStream();
	byte [] buff = new byte[2048];
	int bytes = 0;
	while ( (bytes = in.read(buff)) != -1 ) {
	    out.write(buff, 0, bytes);
	    logger.debug("wrote: " + bytes);
	}
	out.close();
	in.close();

	s.waitForEnd();
    }
    
} 
