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
package org.globus.tools.ui.proxy;

public class GridProxyConfigUtil {
  
  private static int DEFAULT_HOURS = 24;
  private static int DEFAULT_BITS = 512;
  private static boolean DEFAULT_IS_LIMITED = false;

  public static int discoverHours() {
    String hString = System.getProperty("PROXY_HOURS");
    if (hString != null) {
      return Integer.parseInt(hString);
    } else {
      return DEFAULT_HOURS;
    }
  }

  public static int discoverBits() {
    String bString = System.getProperty("PROXY_BITS");
    if (bString != null) {
      return Integer.parseInt(bString);
    } else {
      return DEFAULT_BITS;
    }
  }

  public static boolean discoverLimited() {
    String isLimited = System.getProperty("PROXY_LIMITED");
    if (isLimited != null) {
      return isLimited.equalsIgnoreCase("true");
    } else {
      return DEFAULT_IS_LIMITED;
    }
  }
}

