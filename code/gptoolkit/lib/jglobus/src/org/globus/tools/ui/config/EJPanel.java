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
import java.awt.Insets;
import java.awt.Font;
import java.io.File;

import org.globus.tools.ui.util.JJPanel;

public class EJPanel extends JJPanel {

    public static final String SIDE_TITLE = "Java CoG Kit";

    public boolean checkFile(String file, String name) {
	String msg = null;
	if (file.length() == 0) {
	    msg = name + " must be specified.";
	} else {
	    File f = new File(file);
	    if (!f.exists()) {
		msg = name + " file not found.";
	    }
	}
	
	if (msg != null) {
	    JOptionPane.showMessageDialog(this, msg, name + " Error",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	
	return true;
    }

    public Insets getInsets() {
	return new Insets(5, 5, 5, 5);
    }
    
    protected Font getFont(Font srcFont, int sizeDiff) {
	return new Font(srcFont.getName(),
			srcFont.getStyle(),
			srcFont.getSize() + sizeDiff);
    }
}
