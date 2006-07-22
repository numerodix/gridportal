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
package org.globus.io.gass.server;

import java.io.OutputStream;
import java.io.IOException;

/**
 * This is a small class that allows to redirect
 * a job's output to a custom job output listener.
 * That is, a listener that presents/displays the
 * job output in a specific way. For example, this
 * class can be used to redirect a job's output
 * to a window.
 * <p>
 * This class is specificaly designed for jobs
 * that generate textual output. Binary data
 * might not be handled correctly.
 */
public class JobOutputStream extends OutputStream {

  protected JobOutputListener listener;
  
  /**
   * Creates a job output stream with a specific
   * job output listener to which the job output
   * will be redirected to.
   *
   * @param jobListener an instance of the job output 
   *        listener. Cannot be null.
   */
  public JobOutputStream(JobOutputListener jobListener) {
    if (jobListener == null) {
      throw new IllegalArgumentException("jobListener cannot be null");
    }
    listener = jobListener;
  }
  
  /**
   * Converts the byte array to a string and forwards
   * it to the job output listener.
   * <BR>Called by the GassServer.
   */
  public void write(byte[] b, int off, int len) 
       throws IOException {
	 String s = new String(b, off, len);
	 listener.outputChanged(s);
  }
  
  /**
   * Converts the int to a string and forwards
   * it to the job output listener.
   * <BR>Called by the GassServer.
   */
  public void write(int b) 
       throws IOException {
	 listener.outputChanged(String.valueOf(b));
  }
  
  /**
   * Notifies the job output listener that
   * no more output will be produced.
   * <BR>Called by the GassServer.
   */
  public void close() 
       throws IOException {
	 listener.outputClosed();
  }
  
}
