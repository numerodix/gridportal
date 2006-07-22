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

import java.io.File;

public class CustomFileFilter extends javax.swing.filechooser.FileFilter {
    
    private String description;
    private String extension;

    public CustomFileFilter(String ext, String desc) {
	extension = ext;
	description = desc;
    }

    public boolean accept(File f) {

	if(f.isDirectory()) {
	    return true;
	}

	String s = f.getName().toLowerCase();
	if (s.endsWith(extension)) {
	    return true;
	} else {
	    return false;
	}

    }
  
    public String getDescription() {
	return description;
    }
}
