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
import org.globus.ftp.FileRandomIO;
import org.globus.ftp.DataSink;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.RandomAccessFile;

import org.ietf.jgss.GSSCredential;

public class MultipleTransfersTest extends TestCase {
 
    private static Log logger = 
	LogFactory.getLog(MultipleTransfersTest.class.getName());

    public MultipleTransfersTest(String name) {
	super(name);
    }

    public static void main (String[] args) throws Exception{
	junit.textui.TestRunner.run(suite());	
    }

    public static Test suite ( ) {
	return new TestSuite(MultipleTransfersTest.class);
    }

    public void test2PartyMultipleTransfers() 
	throws Exception {
	logger.info("GridFTP client - client-server - multiple files - stream mode");

	GridFTPClient client = new GridFTPClient(TestEnv.serverAHost, TestEnv.serverAPort);
	client.setAuthorization(TestEnv.getAuthorization(TestEnv.serverASubject));

	String srcFile1 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	String srcFile2 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	String srcFile3 = TestEnv.serverADir + "/" + TestEnv.serverAFile;

	File destFile1 = new File(TestEnv.localDestDir + "/" + TestEnv.serverAFile);
	File destFile2 = new File(TestEnv.localDestDir + "/" + TestEnv.serverAFile);
	File destFile3 = new File(TestEnv.localDestDir + "/" + TestEnv.serverAFile);


	setParamsModeS(client, null); /* use default cred */

	client.setPassive();
	client.setLocalActive();
	client.get(srcFile1, destFile1);

	client.setPassive();
	client.setLocalActive();
	client.get(srcFile2, destFile2);

	client.setPassive();
	client.setLocalActive();
	client.get(srcFile3, destFile3);

	client.close();
    }

    /* do not reuse data channels
     */
    public void test2PartyMultipleTransfersModeE() 
	throws Exception {
	logger.info("GridFTP client - client-server - multiple files - stream mode - no d.c. reuse");

	GridFTPClient client = new GridFTPClient(TestEnv.serverAHost, TestEnv.serverAPort);
	client.setAuthorization(TestEnv.getAuthorization(TestEnv.serverASubject));

	String srcFile1 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	String srcFile2 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	String srcFile3 = TestEnv.serverADir + "/" + TestEnv.serverAFile;

	String destFile1 = TestEnv.localDestDir + "/" + TestEnv.serverAFile;
	String destFile2 = TestEnv.localDestDir + "/" + TestEnv.serverAFile;
	String destFile3 = TestEnv.localDestDir + "/" + TestEnv.serverAFile;


	setParamsModeE(client, null); /* use default cred */
	client.setOptions(new RetrieveOptions(TestEnv.parallelism));


	DataSink  sink1 = new FileRandomIO(new RandomAccessFile(destFile1, "rw"));
	client.setLocalPassive();
	client.setActive();
	client.get(srcFile1, sink1, null);

	DataSink  sink2 = new FileRandomIO(new RandomAccessFile(destFile2, "rw"));
	client.setLocalPassive();
	client.setActive();
	client.get(srcFile2, sink2, null);

	DataSink  sink3 = new FileRandomIO(new RandomAccessFile(destFile3, "rw"));
	client.setLocalPassive();
	client.setActive();
	client.get(srcFile3, sink3, null);

	client.close();
    }

    /*
      Only tests that nothing unusual happens when transferring several files,
      and that server does not return error.
      Does not check if files get transferred correctly.
      Check the server logs to see that no unnecessary commands are sent.
     */
    public void test3PartyMultipleTransfers() 
	throws Exception {
	
	logger.info("GridFTP client - 3 party - multiple files - stream mode");
	try {
	    
	    GridFTPClient source = new GridFTPClient(TestEnv.serverAHost, TestEnv.serverAPort);
	    source.setAuthorization(TestEnv.getAuthorization(TestEnv.serverASubject));

	    String SrcFile1 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	    String SrcFile2 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	    String SrcFile3 = TestEnv.serverADir + "/" + TestEnv.serverAFile;

	    GridFTPClient dest = new GridFTPClient(TestEnv.serverBHost, TestEnv.serverBPort);
	    dest.setAuthorization(TestEnv.getAuthorization(TestEnv.serverBSubject));

	    String DestFile1 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;
	    String DestFile2 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;
	    String DestFile3 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;

	    setParamsModeS(source, null); /* use default cred */
	    setParamsModeS(dest, null); /* use default cred */

	    source.transfer(SrcFile1, dest, DestFile1, false,  null);
	    source.setActive(dest.setPassive());
	    source.transfer(SrcFile2, dest, DestFile2, false,  null);
	    source.setActive(dest.setPassive());
	    source.transfer(SrcFile1, dest, DestFile1, false,  null);
	    source.setActive(dest.setPassive());
	    source.transfer(SrcFile3, dest, DestFile3, false,  null);
	    source.setActive(dest.setPassive());
	    source.transfer(SrcFile1, dest, DestFile1, false,  null);

	} catch (Exception e) {
	    logger.error("", e);
	    fail(e.toString());
	} 
    }

    private void setParamsModeS(GridFTPClient client, GSSCredential cred)
	throws Exception{
	client.authenticate(cred);
	client.setProtectionBufferSize(16384);
	client.setType(GridFTPSession.TYPE_IMAGE);
	client.setMode(GridFTPSession.MODE_STREAM);
    }



    /** try third party transfer.
       no exception should be thrown.
    **/
    public void test3PartyMultipleTransfersModeE() 
	throws Exception {
	
	logger.info("GridFTPClient - 3 party - multiple files - mode E");
	try {
	    
	    
	    GridFTPClient source = new GridFTPClient(TestEnv.serverAHost, TestEnv.serverAPort);
	    source.setAuthorization(TestEnv.getAuthorization(TestEnv.serverASubject));

	    String SrcFile1 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	    String SrcFile2 = TestEnv.serverADir + "/" + TestEnv.serverAFile;
	    String SrcFile3 = TestEnv.serverADir + "/" + TestEnv.serverAFile;

	    GridFTPClient dest = new GridFTPClient(TestEnv.serverBHost, TestEnv.serverBPort);
	    dest.setAuthorization(TestEnv.getAuthorization(TestEnv.serverBSubject));

	    String DestFile1 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;
	    String DestFile2 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;
	    String DestFile3 = TestEnv.serverBDir + "/" + TestEnv.serverBFile;

	    setParamsModeE(source, null); /* use default cred */
	    setParamsModeE(dest, null); /* use default cred */
	    source.setOptions(new RetrieveOptions(TestEnv.parallelism));

	    source.setActive(dest.setPassive());

	    source.transfer(SrcFile1, dest, DestFile1,  false, null);
	    source.transfer(SrcFile2, dest, DestFile2,  false, null);
	    source.transfer(SrcFile3, dest, DestFile3,  false, null);

	} catch (Exception e) {
	    logger.error("", e);
	    fail(e.toString());
	} 
    }

    private void setParamsModeE(GridFTPClient client, GSSCredential cred)
	throws Exception{
	client.authenticate(cred);
	client.setProtectionBufferSize(16384);
	client.setType(GridFTPSession.TYPE_IMAGE);
	client.setMode(GridFTPSession.MODE_EBLOCK);
    }


}
