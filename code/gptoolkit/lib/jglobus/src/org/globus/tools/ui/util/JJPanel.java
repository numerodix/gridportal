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
package org.globus.tools.ui.util;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JComponent;

public class JJPanel extends JPanel {
    
    protected GridBagLayout gbl;
    public GridBagConstraints gbc;
    
    public JJPanel() {
	gbl = new GridBagLayout();
	gbc = new GridBagConstraints();
	setLayout(gbl);
    }

    public void add(JComponent c, int x, int y, int w, int h) {
	gbc.gridx      = x;
	gbc.gridy      = y;
	gbc.gridwidth  = w;
	gbc.gridheight = h;
	
	gbl.setConstraints(c, gbc);
	
	add(c);
    }
    
    public void setAnchor(int anchor) {
	gbc.anchor = anchor;
    }
    
    public void setFill(int fill) {
	gbc.fill = fill;
    }
    
    public void setInsets(int a, int b, int c, int d) {
	gbc.insets = new Insets(a, b, c, d);
    }
    
}
