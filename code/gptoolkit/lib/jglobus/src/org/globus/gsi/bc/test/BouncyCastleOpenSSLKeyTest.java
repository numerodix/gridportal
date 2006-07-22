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
package org.globus.gsi.bc.test;

import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;

import org.globus.gsi.CertUtil;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class BouncyCastleOpenSSLKeyTest extends TestCase {

    private static final String pwd = "testpwd";

    public BouncyCastleOpenSSLKeyTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(BouncyCastleOpenSSLKeyTest.class);
    }

    private KeyPair getKeyPair() throws Exception {
        CertUtil.init();
        
	int bits = 512;
        
	KeyPairGenerator keyGen = null;
	keyGen = KeyPairGenerator.getInstance("RSA", "BC");
	keyGen.initialize(bits);
        
        return keyGen.genKeyPair();
    }

    public void testEncrypt() throws Exception {
        KeyPair keyPair = getKeyPair();

	OpenSSLKey key = new BouncyCastleOpenSSLKey(keyPair.getPrivate());
	
	assertTrue(!key.isEncrypted());

	key.encrypt(pwd);

	assertTrue(key.isEncrypted());
    }

    public void testEncryptAES() throws Exception {
        KeyPair keyPair = getKeyPair();

	OpenSSLKey key = new BouncyCastleOpenSSLKey(keyPair.getPrivate());
	
	assertTrue(!key.isEncrypted());

        key.setEncryptionAlgorithm("AES-128-CBC");

	key.encrypt(pwd);

	assertTrue(key.isEncrypted());

        key.writeTo(System.out);
    }

    private String toString(OpenSSLKey key) throws Exception {
        StringWriter writer = new StringWriter();
        key.writeTo(writer);
        writer.close();
        String s = writer.toString();
        System.out.println(s);
        return s;
    }

    public void testDecryptedToString() throws Exception {
        KeyPair keyPair = getKeyPair();
	OpenSSLKey inKey = new BouncyCastleOpenSSLKey(keyPair.getPrivate());
	assertTrue(!inKey.isEncrypted());
        
        ByteArrayInputStream in = null;
        in = new ByteArrayInputStream(toString(inKey).getBytes());
        OpenSSLKey outKey = new BouncyCastleOpenSSLKey(in);
        assertTrue(!outKey.isEncrypted());

        in = new ByteArrayInputStream(toString(outKey).getBytes());
        OpenSSLKey outKey2 = new BouncyCastleOpenSSLKey(in);
        assertTrue(!outKey2.isEncrypted());
    }

    public void testEcryptedToString() throws Exception {
        KeyPair keyPair = getKeyPair();
	OpenSSLKey inKey = new BouncyCastleOpenSSLKey(keyPair.getPrivate());
	assertTrue(!inKey.isEncrypted());
	inKey.encrypt(pwd);
	assertTrue(inKey.isEncrypted());
        
        ByteArrayInputStream in = null;
        in = new ByteArrayInputStream(toString(inKey).getBytes());
        OpenSSLKey outKey = new BouncyCastleOpenSSLKey(in);
        assertTrue(outKey.isEncrypted());

        in = new ByteArrayInputStream(toString(outKey).getBytes());
        OpenSSLKey outKey2 = new BouncyCastleOpenSSLKey(in);
        assertTrue(outKey2.isEncrypted());
    }

}

