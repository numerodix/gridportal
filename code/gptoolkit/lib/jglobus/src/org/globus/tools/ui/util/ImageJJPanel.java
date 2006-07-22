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

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.ImageIcon;

public class ImageJJPanel extends JJPanel {
    
    protected Image image;
    
    public void setImage(ImageIcon icon) {
	image = icon.getImage();
    }
    
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	if (image == null) return ;
	Dimension d = getSize();
	g.drawImage(image, 0, 0, (int)d.getWidth(), 
		    (int)d.getHeight(), this);
    }
    
}
