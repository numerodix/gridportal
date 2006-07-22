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
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.globus.util.Util;
import org.globus.common.CoGProperties;
import org.globus.tools.ui.swing.MultiLineLabelUI;
import org.globus.tools.ui.util.FileBrowser;

public class ConfigModule3 extends BaseModule {

    private FileBrowser proxyfile;
    
    public ConfigModule3(CoGProperties props) {
	super(props);

	JLabel label = new JLabel("Configuring Proxy File");
	label.setFont(getFont(font, 1));
	label.setForeground(Color.black);
	
	add(label,
	    1, 1, 1, 1);
	
	label = new JLabel(" \nPlease make sure to set proper access rights\n" +
			   "to the proxy file.\n  ");
	label.setUI( new MultiLineLabelUI() );
	label.setFont(getFont(font, 1));

	add(label,
	    1, 2, 1, 1);
	
	proxyfile = new FileBrowser("Open Proxy File", 
				    "Proxy File: ",
				    "Select");
	proxyfile.setFile( props.getProxyFile() );

	add(proxyfile,
	    1, 3, 1, 1);
    }

    public void saveSettings() {
	props.setProxyFile( proxyfile.getFile() );
    }

    public boolean verifySettings() {
	
	String msg  = null;
	String file = proxyfile.getFile();
	
	if (file.length() == 0) {
	    msg = "Proxy file must be specified.";
	} else {
	    File f = new File(file);
	    if (f.exists()) {
		if (f.isDirectory()) {
		    msg = "Selected file is a directory.";
		} else if (!f.canWrite()) {
		    msg = "Cannot write to the selected file.";
		} else if (!f.canRead()) {
		    msg = "Cannot read from the selected file.";
		}
	    } else {
		try {
		    f.createNewFile();
		    Util.setFilePermissions(f.getAbsolutePath(), 600);
		} catch(IOException e) {
		    msg = "Unable to create selected file : " + e.getMessage();
		}
	    }
	}
	
	if (msg != null) {
	    JOptionPane.showMessageDialog(this, msg, "Proxy File Error",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	
	return true;
    }

}

