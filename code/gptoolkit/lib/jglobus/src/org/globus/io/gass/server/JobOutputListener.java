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

/**
 * This class defines a job output listener.
 */
public interface JobOutputListener {
  
  /**
   * It is called whenever the job's output
   * has been updated. 
   *
   * @param output new output
   */
  public void outputChanged(String output);
  
  /**
   * It is called whenever job finished
   * and no more output will be generated.
   */
  public void outputClosed();

}
