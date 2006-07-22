/**
 * MyproxyGUI.java
 *
 * An abstract Frame to contain GridProxy properties
 *
 * $Id: MyproxyGUI.java,v 1.4 2005/04/18 15:36:46 gawor Exp $
 */   

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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.filechooser.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.security.cert.*;
import org.globus.security.*;
import org.globus.myproxy.*;

public class MyproxyGUI extends JFrame {

    protected static GridProxyProperties gridProps;

    public MyproxyGUI() {
	gridProps = new GridProxyProperties();
    }

    public GridProxyProperties getGridProxyProperties() {
	return gridProps;
    }

    public void setGridProxyProperties(GridProxyProperties gridProps) {
	this.gridProps = gridProps;
    }
}
