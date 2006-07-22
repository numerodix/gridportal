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
package org.globus.gsi.proxy.ext.test;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import org.globus.gsi.proxy.ext.ProxyPolicy;
import org.globus.gsi.proxy.ext.ProxyCertInfo;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.ASN1Sequence;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class ProxyCertInfoTest extends TestCase {
    
    String testPolicy = "blahblah";
    DERObjectIdentifier testOid = new DERObjectIdentifier("1.2.3.4.5");

    public ProxyCertInfoTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(ProxyCertInfoTest.class);
    }

    public void testCreateProxyCertInfo() throws Exception {

	ProxyPolicy policy = new ProxyPolicy(testOid, testPolicy);

	ProxyCertInfo info = new ProxyCertInfo(3,
					       policy);
	
	assertEquals(3, info.getPathLenConstraint());

	assertEquals(testPolicy, info.getProxyPolicy().getPolicyAsString());
	assertEquals(testOid, info.getProxyPolicy().getPolicyLanguage());
	
    }

    public void testParseProxyCertInfo() throws Exception {
	
	ProxyPolicy policy = new ProxyPolicy(testOid, testPolicy);

	ProxyCertInfo info = new ProxyCertInfo(3,
					       policy);
	
	
	ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
	dOut.writeObject(info);

	ByteArrayInputStream bIn = 
	    new ByteArrayInputStream(bOut.toByteArray());
	DERInputStream dIn = new DERInputStream(bIn);
	DERObject obj = dIn.readObject();
	
	assertTrue(obj instanceof ASN1Sequence);
	
	ProxyCertInfo testInfo = new ProxyCertInfo((ASN1Sequence)obj);

	assertEquals(3, testInfo.getPathLenConstraint());

	assertEquals(testPolicy, testInfo.getProxyPolicy().getPolicyAsString());
	assertEquals(testOid, testInfo.getProxyPolicy().getPolicyLanguage());
    }
        
    public void testConstraintsCheck() throws Exception {
	
	ProxyPolicy policy; 

	try {
	    policy = new ProxyPolicy(ProxyPolicy.IMPERSONATION,
				     testPolicy);
	    fail("Did not throw exception as expected");
	} catch (IllegalArgumentException e) {
	}

	try {
	    policy = new ProxyPolicy(ProxyPolicy.INDEPENDENT,
				     testPolicy);
	    fail("Did not throw exception as expected");
	} catch (IllegalArgumentException e) {
	}
	
    }	

    public void testCreateProxyCertInfo2() throws Exception {
	
	ProxyPolicy policy = new ProxyPolicy(testOid, testPolicy);
	ProxyCertInfo info = new ProxyCertInfo(policy);
	
	assertEquals(Integer.MAX_VALUE, info.getPathLenConstraint());

	assertEquals(testPolicy, info.getProxyPolicy().getPolicyAsString());
	assertEquals(testOid, info.getProxyPolicy().getPolicyLanguage());

	ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
	dOut.writeObject(info);
	
	ByteArrayInputStream bIn = 
	    new ByteArrayInputStream(bOut.toByteArray());
	DERInputStream dIn = new DERInputStream(bIn);
	DERObject obj = dIn.readObject();

	ProxyCertInfo testInfo = new ProxyCertInfo((ASN1Sequence)obj);

	
	assertEquals(Integer.MAX_VALUE, testInfo.getPathLenConstraint());
	
	assertEquals(testPolicy, testInfo.getProxyPolicy().getPolicyAsString());
	assertEquals(testOid, testInfo.getProxyPolicy().getPolicyLanguage());
    }	
}
