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
package org.globus.gsi.jaas.test;

import org.globus.gsi.jaas.JaasSubject;

import java.security.PrivilegedAction;
import java.security.AccessController;
import javax.security.auth.Subject;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class GlobusSubjectTest extends TestCase {
    
    private static final String CRED = "testCred1";
    private static final String CRED2 = "testCred2";

    public GlobusSubjectTest(String name) {
	super(name);
    }
    
    public static void main (String[] args) {
	junit.textui.TestRunner.run (suite());
    }

    public static Test suite() {
	return new TestSuite(GlobusSubjectTest.class);
    }

    public void testSubject() throws Exception {
	
	Subject subject = new Subject();
	subject.getPublicCredentials().add(CRED);

	TestAction action = new TestAction();
	JaasSubject.doAs(subject, action);
	
	assertEquals(subject, action.subject1);
	assertEquals(subject, action.innerSubject);
	assertEquals(subject, action.subject2);
    }
    
    class TestAction implements PrivilegedAction {
	
	Subject subject1, innerSubject, subject2;

	public Object run() {
	    this.subject1 = JaasSubject.getCurrentSubject();
	    this.innerSubject = (Subject)AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			return JaasSubject.getCurrentSubject();
		    }
		});
	    this.subject2 = JaasSubject.getCurrentSubject();
	    return null;
	}
    }

    public void testNestedSubject() throws Exception {
	
	Subject subject = new Subject();
	subject.getPublicCredentials().add(CRED);

	Subject anotherSubject = new Subject();
	anotherSubject.getPublicCredentials().add(CRED2);

	NestedTestAction action = new NestedTestAction(anotherSubject);
	JaasSubject.doAs(subject, action);
	
	assertEquals(subject, action.subject1);
	assertEquals(subject, action.subject2);
	
	assertEquals(anotherSubject, action.innerSubject1);
	assertEquals(anotherSubject, action.innerSubject2);
	assertEquals(anotherSubject, action.innerInnerSubject);
    }

    class NestedTestAction implements PrivilegedAction {
	
	Subject subject1, subject2;
	Subject innerSubject1, innerSubject2, innerInnerSubject;

	Subject anotherSubject;

	public NestedTestAction(Subject anotherSubject) {
	    this.anotherSubject = anotherSubject;
	}

	public Object run() {
	    this.subject1 = JaasSubject.getCurrentSubject();

	    TestAction action = new TestAction();
	    JaasSubject.doAs(anotherSubject, action);

	    this.innerSubject1 = action.subject1;
	    this.innerSubject2 = action.subject2;
	    this.innerInnerSubject = action.innerSubject;

	    this.subject2 = JaasSubject.getCurrentSubject();
	    return null;
	}
    }

    public void testGetSubjectSameThread() throws Exception {
	
	Subject subject = new Subject();
	subject.getPublicCredentials().add(CRED);
	
	SimpleTestAction action = new SimpleTestAction();
	Subject returnedSubject = 
	    (Subject)JaasSubject.doAs(subject, action);
	
	assertEquals(subject, returnedSubject);
    }

    class SimpleTestAction implements PrivilegedAction {
	public Object run() {
	    return JaasSubject.getCurrentSubject();
	}
    }

    public void testGetSubjectInheritThread() throws Exception {
	
	Subject subject = new Subject();
	subject.getPublicCredentials().add(CRED);

	ThreadTestAction action = new ThreadTestAction();
	Subject returnedSubject = 
	    (Subject)JaasSubject.doAs(subject, action);
	
	assertEquals(subject, returnedSubject);
    }

    class ThreadTestAction implements PrivilegedAction {
	public Object run() {
	    TestThread t = new TestThread();
	    t.start();
	    try {
		t.join();
	    } catch (Exception e) {
	    }
	    return t.subject;
	}
    }

    class TestThread extends Thread {
	Subject subject;
	public void run() {
	    this.subject = JaasSubject.getCurrentSubject();
	}
    }

}
