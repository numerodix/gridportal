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

public class MDSTest1 {

  private int errorCount = 0;
  MDSResult mdsResult;
  Hashtable v;

  public void run(String host, String port, String dn) {

    MDS mds = new MDS(host, port);
     
    try {

      try {
	mds.connect();
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      try {
	mdsResult = mds.getAttributes(dn);
	mdsResult.print();
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
      
      System.out.println("---");

      mds.setSearchLimit(10);
      try {
	v = mds.search(dn,
		       "(objectclass=*)", 
		       new String[]{"objectclass", "domainname"}, 
		       MDS.ONELEVEL_SCOPE);
	
	Enumeration e = v.keys();
	while(e.hasMoreElements()) {
	  dn = (String)e.nextElement();
	  mdsResult = (MDSResult)v.get(dn);
	  System.out.println("DN: " + dn);
	  System.out.println("...domain: " + mdsResult.getFirstValue("domainname"));
	}
      } catch (MDSException e) {
	System.err.println("MDS error:" + e.getMessage() + " " + e.getLdapMessage());
	errorCount++;
	return;
      }
    
    } finally {
      try {
	mds.disconnect();
      } catch (MDSException e) {
      }
    }
  }
  
  public void printResults() {
    if( errorCount == 0 ) {
      System.out.println( "\n{test} MDS TEST 1: succeeded" );
    } else {
      System.out.println( "\n{test} MDS TEST 1: failed -- "
			  + errorCount + " error(s) encountered" );
    }
  }    
  
  public static void main( String[] argv ) {
    if (argv.length < 3) {
      System.err.println("Usage: java MDSTest1 host port dn");
      System.exit(-1);
    }
    
    MDSTest1 mdsTest = new MDSTest1();
    mdsTest.run(argv[0],
		argv[1],
		argv[2]);
    mdsTest.printResults();
  }
}









