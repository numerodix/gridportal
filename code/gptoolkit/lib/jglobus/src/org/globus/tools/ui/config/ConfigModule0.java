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

import java.awt.Color;
import javax.swing.JLabel;

import org.globus.common.CoGProperties;

public class ConfigModule0 extends BaseModule {
    
    public ConfigModule0(CoGProperties props) {
	super(props);
	
	setInsets(1, 1, 1, 1);
	
	JLabel label = 
	    new JLabel("Welcome to the Java CoG Kit installation wizard");
	label.setFont(getFont(font, 4));
	label.setForeground(Color.black);
	
	add(label,
	    1, 0, 1, 1);
	
	label = new JLabel("This wizard helps to configure Java CoG Kit installation.");
	label.setFont(getFont(font, 2));
	label.setForeground(Color.black);
	
	add(label,
	    1, 1, 1, 1);
    }
    
    public void saveSettings() {
    }
    
}


