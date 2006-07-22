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
import org.globus.ftp.RetrieveOptions;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.FileRandomIO;
import org.globus.ftp.DataSink;
import org.globus.ftp.DataSource;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.RandomAccessFile;

import org.ietf.jgss.GSSCredential;

/** Test data channel reuse with varying parallelism*/
public class DataChannelReuseVaryingParTest extends TestCase {

    private static Log logger =
	LogFactory.getLog(DataChannelReuseVaryingParTest.class.getName());
    
    // for incoming transfers
    protected String remoteSrcFile1 =
	TestEnv.serverADir + "/" + TestEnv.serverAFile;
    protected String remoteSrcFile2 =
	TestEnv.serverADir + "/" + TestEnv.serverAFile;
    protected String remoteSrcFile3 =
	TestEnv.serverADir + "/" + TestEnv.serverAFile;
    
    protected String localDestFile1 =
	TestEnv.localDestDir + "/" + TestEnv.serverAFile;
    protected String localDestFile2 =
	TestEnv.localDestDir + "/" + TestEnv.serverAFile;
    protected String localDestFile3 =
	TestEnv.localDestDir + "/" + TestEnv.serverAFile;
    
    // for outgoing transfers
    protected String localSrcFile1 =
	TestEnv.localSrcDir + "/" + TestEnv.localSrcFile;
    protected String localSrcFile2 =
	TestEnv.localSrcDir + "/" + TestEnv.localSrcFile;
    protected String localSrcFile3 =
	TestEnv.localSrcDir + "/" + TestEnv.localSrcFile;
    
    protected String remoteDestFile1 =
	TestEnv.serverBDir + "/" + TestEnv.localSrcFile;
    protected String remoteDestFile2 =
	TestEnv.serverBDir + "/" + "somefile";
    protected String remoteDestFile3 =
	TestEnv.serverBDir + "/" + TestEnv.localSrcFile;
    
    public DataChannelReuseVaryingParTest(String name) {
	super(name);
    }
    
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
	return new TestSuite(DataChannelReuseVaryingParTest.class);
    }
    
    
    /*
     * Incoming connection, with changing parallelism
     */
    public void testGetVarPar() throws Exception {
	logger.info("data channel reuse with get and changing parallelism");

	GridFTPClient client = new GridFTPClient(TestEnv.serverAHost, TestEnv.serverAPort);
	client.setAuthorization(TestEnv.getAuthorization(TestEnv.serverASubject));

	setParamsModeE(client, null);

	client.setOptions(new RetrieveOptions(4));
	client.setPassiveMode(false);
	
	DataSink sink1 =
	    new FileRandomIO(new RandomAccessFile(localDestFile1, "rw"));
	client.get(remoteSrcFile1, sink1, null);
	sink1.close();
	
	client.setOptions(new RetrieveOptions(2));
	client.setPassiveMode(false);   

	DataSink sink2 =
	    new FileRandomIO(new RandomAccessFile(localDestFile2, "rw"));
	client.get(remoteSrcFile1, sink2, null);
	sink2.close();
	
	client.setOptions(new RetrieveOptions(7));
	client.setPassiveMode(false);   
	
	DataSink sink3 =
	    new FileRandomIO(new RandomAccessFile(localDestFile3, "rw"));
	client.get(remoteSrcFile1, sink3, null);
	sink3.close();
	
	client.setOptions(new RetrieveOptions(1));
	client.setPassiveMode(false);   

	// using file1 again
	DataSink sink4 =
	    new FileRandomIO(new RandomAccessFile(localDestFile1, "rw"));
	client.get(remoteSrcFile1, sink4, null);
	sink4.close();
	
	client.close();
    }

    /*
     * Outgoing connection and changing parallelism
     */
    public void testPutVarPar() throws Exception {
	logger.info("data channel reuse with put and changing parallelism");

	GridFTPClient client = new GridFTPClient(TestEnv.serverBHost, TestEnv.serverBPort);
	client.setAuthorization(TestEnv.getAuthorization(TestEnv.serverBSubject));

	setParamsModeE(client, null); 

	client.setOptions(new RetrieveOptions(4));
	client.setPassiveMode(true);
	
	// transfer
	DataSource source1 =
	    new FileRandomIO(new RandomAccessFile(localSrcFile1, "r"));
	client.put(remoteDestFile1, source1, null);
	source1.close();
	// check that everything got transferred
	long size = client.getSize(remoteDestFile1);               	    
	File f = new File(localSrcFile1);
	logger.debug("comparing size: " + size + " <-> " + f.length() );
	assertEquals(f.length(), size);

	client.setOptions(new RetrieveOptions(2));
	client.setPassiveMode(true);

	DataSource source2 =
	    new FileRandomIO(new RandomAccessFile(localSrcFile2, "r"));
	client.put(remoteDestFile2, source2, null);
	source2.close();
	size = client.getSize(remoteDestFile2);               	    
	f = new File(localSrcFile2);
	logger.debug("comparing size: " + size + " <-> " + f.length() );
	assertEquals(f.length(), size);
	
	client.setOptions(new RetrieveOptions(7));
	client.setPassiveMode(true);

	DataSource  source3 = 
	    new FileRandomIO(new RandomAccessFile(localSrcFile3, "rw"));
	client.put(remoteDestFile3, source3, null);
	source3.close();
	size = client.getSize(remoteDestFile3);               	    
	f = new File(localSrcFile3);
	logger.debug("comparing size: " + size + " <-> " + f.length() );
	assertEquals(f.length(), size);
	
	client.setOptions(new RetrieveOptions(1));
	client.setPassiveMode(true);

	// transfer file1 again
	DataSource source4 =
	    new FileRandomIO(new RandomAccessFile(localSrcFile1, "r"));
	client.put(remoteDestFile1, source4, null);
	source4.close();
	// check that everything got transferred
	size = client.getSize(remoteDestFile1);               	    
	f = new File(localSrcFile1);
	logger.debug("comparing size: " + size + " <-> " + f.length() );
	assertEquals(f.length(), size);
	
	client.close();
    }


    private void setParamsModeE(GridFTPClient client, GSSCredential cred)
	throws Exception {
	client.authenticate(cred);
	client.setProtectionBufferSize(16384);
	client.setType(GridFTPSession.TYPE_IMAGE);
	client.setMode(GridFTPSession.MODE_EBLOCK);
	client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
	client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
    }
    
}
