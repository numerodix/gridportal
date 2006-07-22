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
package org.globus.tools.ui.config;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;

import org.globus.common.CoGProperties;
import org.globus.tools.ui.util.UITools;

/** This class represents the Java CoG Kit configuration wizard.
 *
 */
public class ConfigurationWizard extends AbstractWizard {

    protected CoGProperties props = null;

    public ConfigurationWizard() {
	setTitle("Java CoG Kit Configuration Wizard");

	props = CoGProperties.getDefault();
	
    	initComponents();
    }

    private void initComponents() {
	addModule( new ConfigModule0(props) );
	addModule( new ConfigModule1(props) );
	
	pack();
	
	addModule( new ConfigModule2(props) );
	addModule( new ConfigModule3(props) );
    }

    public void saveSettings() {

	File cFile = new File(CoGProperties.configFile);
	File directory = cFile.getParentFile();
	
	try {
	    if (!directory.exists()) {
		if (!directory.mkdir()) 
		    throw new IOException("Unable to create directory : " + 
					  directory);
	    }
	    
	    props.remove("internal.usercert");
	    
	    props.save(CoGProperties.configFile);
	} catch(Exception e) {
	    JOptionPane.showMessageDialog(this,
	       "Failed to save the configuration file:\n" + e.getMessage(),
	       "Error Saving Configuration",
	       JOptionPane.ERROR_MESSAGE);
	    
	    return;
	}
	
	JOptionPane.showMessageDialog(this,
	  "The Java CoG Kit is now successfully configured.",
	  "Java CoG Kit Configuration Wizard",
	  JOptionPane.INFORMATION_MESSAGE);
	
	System.exit(0);
    }
    
    public static void main( String[] args ) {
	ConfigurationWizard configWizard = new ConfigurationWizard();
	UITools.center(null, configWizard);
	configWizard.setVisible( true );
    }
}












