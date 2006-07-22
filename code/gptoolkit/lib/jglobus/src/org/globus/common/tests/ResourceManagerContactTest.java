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
package org.globus.common.tests;

import org.globus.common.ResourceManagerContact;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ResourceManagerContactTest extends TestCase {

    public  ResourceManagerContactTest(String name) {
	super(name);
    }

    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(ResourceManagerContactTest.class);
    }

    public void testBasic() throws Exception {
	ResourceManagerContact c = null;
	
	c = new ResourceManagerContact("pitcairn.mcs.anl.gov");
	verify(c, "pitcairn.mcs.anl.gov", 2119);

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:123");
	verify(c, "pitcairn.mcs.anl.gov", 123);

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:123/job");
	verify(c, "pitcairn.mcs.anl.gov", 123, "/job");

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov/job");
	verify(c, "pitcairn.mcs.anl.gov", 2119, "/job");

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:/job");
	verify(c, "pitcairn.mcs.anl.gov", 2119, "/job");

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov::cn=jarek");
	verify(c, "pitcairn.mcs.anl.gov", 2119, "/jobmanager", "cn=jarek");

	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:123:cn=jarek");
	verify(c, "pitcairn.mcs.anl.gov", 123, "/jobmanager", "cn=jarek");
	
	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:/job:cn=jarek");
	verify(c, "pitcairn.mcs.anl.gov", 2119, "/job", "cn=jarek");
	
	c = new ResourceManagerContact("pitcairn.mcs.anl.gov/job:cn=jarek");
	verify(c, "pitcairn.mcs.anl.gov", 2119, "/job", "cn=jarek");
	
	c = new ResourceManagerContact("pitcairn.mcs.anl.gov:123/job:cn=jarek");
	verify(c, "pitcairn.mcs.anl.gov", 123, "/job", "cn=jarek");
    }


    public void testBasicIPv6() throws Exception {
	ResourceManagerContact c = null;
	c = new ResourceManagerContact("[3ffe:2a00:100:7031::1]");
	verify(c, "[3ffe:2a00:100:7031::1]", 2119);

	c = new ResourceManagerContact("[3ffe:2a00:100:7031::1]:123");
	verify(c, "[3ffe:2a00:100:7031::1]", 123);
	
	c = new ResourceManagerContact("[3ffe:2a00:100:7031::1]/job");
	verify(c, "[3ffe:2a00:100:7031::1]", 2119, "/job");
     }

    private void verify(ResourceManagerContact contact,
			String hostname,
			int port) {
	verify(contact, hostname, port, "/jobmanager", null);
    }
    
    private void verify(ResourceManagerContact contact,
			String hostname,
			int port,
			String serviceName) {
	verify(contact, hostname, port, serviceName, null);
    }

    private void verify(ResourceManagerContact contact,
			String hostname,
			int port,
			String serviceName,
			String dn) {
	assertEquals("hostname", hostname, contact.getHostName());
	assertEquals("port", port, contact.getPortNumber());
	assertEquals("service", serviceName, contact.getServiceName());
	assertEquals("dn", dn, contact.getGlobusDN());
    }

}
