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
package org.globus.gram;

import org.globus.util.deactivator.*;

public class GramTest {

  public static void main(String [] args) {
    
    GramJob job1 = new GramJob("&(executable=/bin/sleep)(directory=/tmp)(arguments=15)");

    GramJob job2 = new GramJob("&(executable=/bin/sleep)(directory=/tmp)(arguments=25)");

    GramJob job3 = new GramJob("&(executable=/bin/sleep)(directory=/tmp)(arguments=35)");
    
    String contact = null;

    if (args.length == 0) {
      System.err.println("Usage: java GramTest [resource manager]");
      System.exit(1);
    }

    contact = args[0];

    try {
      job1.addListener( new GramJobListener() {
	public void statusChanged(GramJob job) {
	  System.out.println("Job1 status change \n" +  
			     "    ID     : "+ job.getIDAsString() + "\n" + 
			     "    Status : "+ job.getStatusAsString());
	}	
      });

      job3.addListener( new GramJobListener() {
	public void statusChanged(GramJob job) {
	  System.out.println("Job3 status change \n" +  
			     "    ID     : "+ job.getIDAsString() + "\n" + 
			     "    Status : "+ job.getStatusAsString());

	}	
      });

      job2.addListener( new GramJobListener() {
	public void statusChanged(GramJob job) {
	   System.out.println("Job2 status change \n" +  
			     "    ID     : "+ job.getIDAsString() + "\n" + 
			     "    Status : "+ job.getStatusAsString());

	  if (job.getStatus() == 2) {
	    try {
	      System.out.println("disconnecting from job2");
	      job.unbind();
	      System.out.println("canceling job2");
	      job.cancel();
	    } catch(Exception e) {
	      System.out.println(e);
	    }

	  }

	}	
      });


      System.out.println("submitting job1...");
      job1.request(contact);
      System.out.println("job submited: " + job1.getIDAsString());

      System.out.println("submitting job2...");
      job2.request(contact);
      System.out.println("job submited: " + job2.getIDAsString());

      System.out.println("submitting job3 in batch mode...");
      job3.request(contact, true);
      System.out.println("job submited: " + job3.getIDAsString());

      try {  Thread.sleep(2000); }  catch(Exception e) {}

      System.out.println("rebinding to job3..");
      job3.bind();

      try {
	while ( Gram.getActiveJobs() != 0 ) {
	  Thread.sleep(2000); 
	}
      } catch(Exception e) {}

      System.out.println("Test completed.");
    } catch(Exception e) {
      System.out.println(e.getMessage());
    } finally {
	Deactivator.deactivateAll();
    }
    
  }

}




