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

import org.globus.security.Config;
import org.globus.util.ConfigUtil;

import java.util.Properties;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Responsible for managing the properties file "proxy.properties"
 * which holds information about various properties needed by grid
 * proxy init classes.
 *
 * This class is modelled after org.globus.security.Config and is
 * designed to replace GridProperties.
 */

public class GridProxyConfig {

    private static Log logger =
        LogFactory.getLog(GridProxyConfig.class.getName());

  public static final String configFile = "proxy.properties";
  
  public static Properties props = new Properties();

  static {
    String file = System.getProperty("org.globus.proxy.config.file");
    if (file == null) {
      file = ConfigUtil.globus_dir + configFile;
    }

    if (!file.equalsIgnoreCase("none")) {
      try {
	props = Config.readProperties(file);
      } catch(IOException e) {
	  logger.warn("Proxy configuration file failed to load: " + e.getMessage(), e);
      }
    }
  }

  public static int getHours() {
    String hString = props.getProperty("hours");
    if (hString == null) {
      return GridProxyConfigUtil.discoverHours();
    } else {
      return Integer.parseInt(hString);
    }
  }

  public static int getBits() {
    String bString = props.getProperty("bits");
    if (bString == null) {
      return GridProxyConfigUtil.discoverBits();
    } else {
      return Integer.parseInt(bString);
    }
  }

  public static boolean getLimited() {
    String lString = props.getProperty("limited");
    if (lString == null) {
      return GridProxyConfigUtil.discoverLimited();
    } else {
      return lString.equalsIgnoreCase("true");
    }
  }
}
  








