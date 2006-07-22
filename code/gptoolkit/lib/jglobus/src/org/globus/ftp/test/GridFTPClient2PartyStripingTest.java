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

import org.globus.ftp.GridFTPClient;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.Session;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.RetrieveOptions;
import org.globus.ftp.FileRandomIO;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSinkStream;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.HostPortList;
import org.globus.ftp.DataSink;
import org.globus.ftp.vanilla.FTPServerFacade;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

/**
   Test GridFTPClient.stripedGet() and stripedPut()
 **/
public class GridFTPClient2PartyStripingTest 
    extends GridFTPClient2PartyParallelTest {

    public GridFTPClient2PartyStripingTest(String name) {
	super(name);
    }

    public static void main (String[] args) throws Exception{
	junit.textui.TestRunner.run(suite());	
    }

    public static Test suite ( ) {
	return new TestSuite(GridFTPClient2PartyStripingTest.class);
    }

    //
    // overriden methods
    //

    protected void title() {
	logger.info("Testing:");
	logger.info("striped 2 party transfer\n\n");
    }

    /**
       This demonstrates striped file storage.
     **/
    protected void get(GridFTPClient client, 
		       int localServerMode,
		       int transferType,
		       int transferMode,
		       DataChannelAuthentication dcau,
		       int prot,
		       String fullLocalFile,
		       String fullRemoteFile) 
	throws Exception{
	client.authenticate(null); /* use default cred */
	client.setProtectionBufferSize(16384);
	client.setType(transferType);
	client.setMode(transferMode);
	// adding parallelism
	logger.info("parallelism: " + parallelism);
	client.setOptions(new RetrieveOptions(parallelism));
	client.setDataChannelAuthentication(dcau);
 	client.setDataChannelProtection(prot);

	// in extended block mode, receiving side must be passive
	assertTrue(localServerMode == Session.SERVER_PASSIVE);

	HostPortList hpl = null;
	if (TestEnv.localServerPort == TestEnv.UNDEFINED) {
	    hpl = client.setLocalStripedPassive(); 
	} else {
	    hpl = client.setLocalStripedPassive(TestEnv.localServerPort, 
						FTPServerFacade.DEFAULT_QUEUE);
	}
	client.setStripedActive(hpl);

	DataSink sink = null;
	if (transferMode == GridFTPSession.MODE_EBLOCK) {
	    sink = new FileRandomIO(new RandomAccessFile(fullLocalFile, 
							 "rw"));
	} else {
	    sink = new DataSinkStream(new FileOutputStream(fullLocalFile));
	}

	client.get(fullRemoteFile, sink, null);
    }
    
    /**
       This demonstrates striped file retrieval.
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
	client.authenticate(null); /* use default cred */
	client.setProtectionBufferSize(16384);
	client.setType(transferType);
	client.setMode(transferMode);
	// adding parallelism
	logger.info("parallelism: " + parallelism);
	client.setOptions(new RetrieveOptions(parallelism));
 	client.setDataChannelAuthentication(dcau);
 	client.setDataChannelProtection(prot);

	assertTrue(localServerMode == Session.SERVER_ACTIVE);

	client.setStripedPassive();
	client.setLocalStripedActive();

	logger.debug("sending file " + fullLocalFile);
	DataSource source = null;
	if (transferMode == GridFTPSession.MODE_EBLOCK) {
	    source = new FileRandomIO(new RandomAccessFile(fullLocalFile, 
							   "r"));
	} else {
	    source = new DataSourceStream(new FileInputStream(fullLocalFile));
	}

	client.put(fullRemoteFile, source, null);
    }
    
}
