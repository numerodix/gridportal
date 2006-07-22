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
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.globus.common.CoGProperties;
import org.globus.tools.ui.swing.VerticalLabelUI;

public abstract class BaseModule extends EJPanel 
    implements ConfigurationModule {
    
    protected CoGProperties props;
    
    protected Font font;
    
    public BaseModule(CoGProperties props) {
	this.props = props;
	
	setInsets(10, 10, 10, 10);
	
	JLabel label = new JLabel();
	font    = label.getFont();  // default font used
	
	JLabel l = new JLabel(SIDE_TITLE, SwingConstants.LEFT );
	l.setUI( new VerticalLabelUI(false) );
	l.setForeground(Color.black);
	l.setFont(getFont(font, 15));
	
	add(l, 
	    0, 0, 1, 5);
	
	setInsets(1, 1, 1, 1);
    }
    
    public abstract void saveSettings();
    
    public boolean verifySettings() {
	return true;
    }
    
}


