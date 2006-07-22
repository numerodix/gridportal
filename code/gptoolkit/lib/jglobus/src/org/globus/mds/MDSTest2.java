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
package org.globus.mds;

import java.util.*;

import org.globus.common.*;

public class MDSTest2 {

  private int errorCount = 0;

  MDSResult mdsResult;
  
  public void displayAttributes(MDS mds, String dn) {
    try {

      MDSResult mdsResult = mds.getAttributes( dn );      
      mdsResult.print();
      System.out.println();

    } catch (MDSException e) {
      System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
      errorCount++;
    }
  }
  
  public void run(String host, String port, String userdn, String userpwd, String bdn) {
    
    String dn;
    MDS mds = new MDS(host, port);
    
    try {

      try {
	mds.connect(userdn, userpwd);
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      try {
	mdsResult = mds.getAttributes( bdn );      
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      mdsResult.print();
      
      mdsResult.remove("dn");
      mdsResult.add("testfield", new String [] {"value1", "value2", "value3"});
    
      try {
	mds.updateEntry(bdn, mdsResult);
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      System.out.println();
      
      displayAttributes(mds, bdn );
      
      MDSResult a3 = new MDSResult();
      a3.add("testfield", "value2");
      
      try {
	mds.deleteValues(bdn, a3);
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      displayAttributes(mds, bdn );
      
      try {
	mds.deleteAttribute(bdn, "testfield");
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }

      displayAttributes(mds,  bdn );
      
    } finally {
      try {
	mds.disconnect();
      } catch (MDSException e) {
      }
    }    
  }
  
  public void printResults() {
    if( errorCount == 0 ) {
	System.out.println( "{test} MDS TEST 2: succeeded" );
    } else {
	System.out.println( "{test} MDS TEST 2: failed -- "
			    + errorCount + " error(s) encountered" );
    }
  }

  public static void main(String[] argv) {
    if (argv.length < 5) {
      System.err.println("Usage: java MDSTest2 host port userdn userpwd dn");
      System.exit(-1);
    }
    
    MDSTest2 mdsTest = new MDSTest2();
    mdsTest.run(argv[0],
		argv[1],
		argv[2],
		argv[3],
		argv[4]);
    mdsTest.printResults();
  }
}












