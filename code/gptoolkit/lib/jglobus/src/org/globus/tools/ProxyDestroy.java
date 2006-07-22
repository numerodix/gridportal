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
package org.globus.tools;

import java.io.File;

import org.globus.common.CoGProperties;
import org.globus.util.Util;

/** Destroys a proxy file.
 *<pre>
 * Syntax: java ProxyDestroy [-dryrun][-default] [file1...]
 *         java ProxyDestroy -help\n
 * Options
  * -help | -usage
  *     Displays usage
  * -dryrun
  *     Prints what files would have been destroyed
  * file1 file2 ...
  *     Destroys files listed
 *</pre>
 */
public class ProxyDestroy {

    private static final String message =
	"\n" +
	"Syntax: java ProxyDestroy [-dryrun] [file1...]\n" +
	"        java ProxyDestroy -help\n\n" +
	"\tOptions\n" +
	"\t-help | -usage\n" +
	"\t\tDisplays usage\n" +
	"\t-dryrun\n" +
	"\t\tPrints what files would have been destroyed\n" +
	"\tfile1 file2 ...\n" +
	"\t\tDestroys files listed\n\n";
    
  public static void main(String args[]) {

      boolean dryrun      = false;
      boolean error       = false;
      boolean debug       = false;
      File file           = null;
      
      for (int i = 0; i < args.length; i++) {
	  if (args[i].equalsIgnoreCase("-dryrun")) {
	      dryrun = true;
	  } else if (args[i].equalsIgnoreCase("-help") ||
		     args[i].equalsIgnoreCase("-usage")) {
	      System.err.println(message);
	      System.exit(1);
	  } else {
	      file = new File(args[i]);
	      if (dryrun) {
		  System.out.println("Would remove " + file.getAbsolutePath());
		  continue;
	      }
	      Util.destroy(file);
	  }
      }
      
      String fn = CoGProperties.getDefault().getProxyFile();
      if (fn == null) return ;
      file = new File(fn);
      if (dryrun) {
	  System.out.println("Would remove " + file.getAbsolutePath());
	  return;
      }
      
      Util.destroy(file);
  }
    
}
