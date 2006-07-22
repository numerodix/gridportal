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

import org.globus.ftp.app.Transfer;
import org.globus.ftp.app.TransferParams;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;

/*
  Tests for app.Transfer class. they only tell if transfer succeeds or fails. 
  To really know if the commands are being sent correctly, enable control channel
  debugging and monitor commands being sent:
  LogFactory.getLog(org.globus.ftp.vanilla.FTPControlChannel.class.getName()).setLevel(Level.INFO);
 */

public class TransferAppTest extends TestCase {

    private static Log logger = LogFactory.getLog(TransferAppTest.class.getName());

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
	return new TestSuite(TransferAppTest.class);
    }

    public TransferAppTest(String name) {
	super(name);
    }

    public void testDefault() throws Exception{
	logger.debug("default parameters");
	testA2B(new TransferParams());
    }

    public void testStreamImage() throws Exception{
	logger.debug("transfer mode = STREAM, transfer type = IMAGE");
	TransferParams params = new TransferParams();
	params.transferMode = params.MODE_STREAM;
	params.transferType = params.TYPE_IMAGE;
	testA2B(params);
    }

    public void testStreamAscii() throws Exception{
	logger.debug("transfer mode = STREAM, transfer type = ASCII");
	TransferParams params = new TransferParams();
	params.transferMode = params.MODE_STREAM;
	params.transferType = params.TYPE_ASCII;
	testA2B(params);
    }

    public void testStreamAsciiPasv() throws Exception{
	logger.debug("transfer mode = STREAM, sender passive");
	TransferParams params = new TransferParams();
	params.transferMode = params.MODE_STREAM;
	params.serverMode = params.SERVER_PASSIVE;
	testA2B(params);
    }

    public void testParallel5() throws Exception{
	logger.debug("parallelism = 5");
	TransferParams params = new TransferParams();
	params.parallel = 5;
	testA2B(params);
    }

    public void testParallel3Strip() throws Exception{
	logger.debug("parallelism = 3, striping");
	TransferParams params = new TransferParams();
	params.parallel = 3;
	params.doStriping = true;
	testA2B(params);
    }

    public void testParallel5Pasv() throws Exception{
	logger.debug("parallelism = 5, sender passive (should fail)");
	TransferParams params = new TransferParams();
	params.parallel = 5;
	params.serverMode = params.SERVER_PASSIVE;
	// this will fail; in mode E sender cannot be passive
	testFailA2B(params);
    }

    public void testParallel3StripPasv() throws Exception{
	logger.debug("parallelism = 3, striping, sender passive (should fail)");
	TransferParams params = new TransferParams();
	params.parallel = 3;
	params.doStriping = true;
	params.serverMode = params.SERVER_PASSIVE;
	// this will fail; in mode E sender cannot be passive
	testFailA2B(params);
    }

    public void testPBSZ() throws Exception{
	logger.debug("protection buffer size = 10000");
	TransferParams params = new TransferParams();
	params.protectionBufferSize = 10000;
	testA2B(params);
    }

    public void testAuthNone() throws Exception{
	logger.debug("data channel auth = none");
	TransferParams params = new TransferParams();
	params.dataChannelAuthentication = org.globus.ftp.DataChannelAuthentication.NONE;
	testA2B(params);
    }

    public void testAuthSelf() throws Exception{
	logger.debug("data channel auth = self");
	TransferParams params = new TransferParams();
	params.dataChannelAuthentication = org.globus.ftp.DataChannelAuthentication.SELF;
	testA2B(params);
	}

    public void testProtClear() throws Exception{
	logger.debug("data channel protection = clear");
	TransferParams params = new TransferParams();
	params.dataChannelProtection = params.PROTECTION_CLEAR;
	testA2B(params);
	}

    public void testProtPrivate() throws Exception{
	logger.debug("data channel protection = private");
	TransferParams params = new TransferParams();
	params.dataChannelProtection = params.PROTECTION_PRIVATE;
	testA2B(params);
	}

    public void testTCPBufferSmall() throws Exception{
	logger.debug("TCPBufferSize = 12345");
	TransferParams params = new TransferParams();
	params.TCPBufferSize = 12345;
	testA2B(params);
	}

    public void testTCPBufferLarge() throws Exception{
	logger.debug("TCPBufferSize = 100000");
	TransferParams params = new TransferParams();
	params.TCPBufferSize = 100000;
	testA2B(params);
	}

    /**
       Test transfer from server A to server B
     */
    private void testA2B(TransferParams myParams) throws Exception {
	Transfer transfer = new Transfer(
					 //source
					 TestEnv.serverAHost,
					 TestEnv.serverAPort,
					 TestEnv.serverASubject,
					 TestEnv.serverADir,
					 TestEnv.serverAFile,
					 //dest
					 TestEnv.serverBHost,
					 TestEnv.serverBPort,
					 TestEnv.serverBSubject,
					 TestEnv.serverBDir,
					 TestEnv.serverBFile,
					 //params
					 myParams);
    }

    /**
       Test transfer from server A to server B which is expected to fail
     */
    private void testFailA2B(TransferParams myParams) {
	boolean failed = false;
	try {
	    testA2B(myParams);
	} catch (Exception e) {
	    failed = true;
	}
	assertTrue(failed == true);
    }
}
