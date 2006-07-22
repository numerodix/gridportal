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
package org.globus.tools.proxy;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import org.globus.common.CoGProperties;
import org.globus.tools.ui.util.JJPanel;
import org.globus.tools.ui.util.FileBrowser;

public class ProxyOptionsPanel extends JJPanel {

    protected FileBrowser proxyFileFB;
    protected ButtonGroup hoursGroup;
    protected ButtonGroup bitsGroup;
    protected JRadioButton shRB, mhRB, lhRB, ghRB;
    protected JRadioButton sbRB, mbRB, lbRB, gbRB;
    
    public ProxyOptionsPanel() {
	
	setAnchor(GridBagConstraints.EAST);
	
	add(new JLabel("Proxy Lifetime: "),
	    0, 0, 1, 1);
	
	add(new JLabel("Strength: "),
	    0, 1, 1, 1);
	
	add(new JLabel("Proxy File: "),
	    0, 2, 1, 1);
	
	setAnchor(GridBagConstraints.WEST);
	
	// this makes the component to fill the entrie available space
	gbc.weightx = 1;

	setFill(GridBagConstraints.HORIZONTAL);
		
	JPanel hoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
	
	shRB = new JRadioButton("12 h");
	mhRB = new JRadioButton("24 h", true);
	lhRB = new JRadioButton("1 week");
	ghRB = new JRadioButton("1 month");
	
	shRB.setActionCommand("12");
	mhRB.setActionCommand("24");
	lhRB.setActionCommand("168");
	ghRB.setActionCommand("672");
	
	// Group the radio buttons.
	hoursGroup = new ButtonGroup();
	hoursGroup.add(shRB);
	hoursGroup.add(mhRB);
	hoursGroup.add(lhRB);
	hoursGroup.add(ghRB);
	
	hoursPanel.add(shRB);
	hoursPanel.add(mhRB);
	hoursPanel.add(lhRB);
	hoursPanel.add(ghRB);

	add(hoursPanel,
	    1, 0, 1, 1);

	JPanel bitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
	
	sbRB = new JRadioButton("512", true);
	mbRB = new JRadioButton("1024");
	lbRB = new JRadioButton("2048");
	gbRB = new JRadioButton("4096");
	sbRB.setActionCommand("512");
	mbRB.setActionCommand("1024");
	lbRB.setActionCommand("2048");
	gbRB.setActionCommand("4096");
	
	// Group the radio buttons.
	bitsGroup = new ButtonGroup();
	bitsGroup.add(sbRB);
	bitsGroup.add(mbRB);
	bitsGroup.add(lbRB);
	bitsGroup.add(gbRB);

	bitsPanel.add(sbRB);
	bitsPanel.add(mbRB);
	bitsPanel.add(lbRB);
	bitsPanel.add(gbRB);
	
	add(bitsPanel,
	    1, 1, 1, 1);
	
	proxyFileFB = new FileBrowser("Select Grid Proxy File", "Select");
	
	add(proxyFileFB,
	    1, 2, 1, 1);
    }

    protected boolean error(String msg) {
	JOptionPane.showMessageDialog(this, 
				      msg,
				      "Need More Information", 
				      JOptionPane.WARNING_MESSAGE);
	return false;
    }

    public boolean validateSettings() {
	if (proxyFileFB.getFile().equals("")) {
	    return error("Please enter the proxy file location");
	}
	return true;
    }
    
    public void set(CoGProperties props) {
	
	proxyFileFB.setFile(props.getProxyFile());
	
	int value = props.getProxyLifeTime();

	if (value == 12)
	    shRB.setSelected(true);
	else if (value == 24)
	    mhRB.setSelected(true);
	else if (value == 168)
	    lhRB.setSelected(true);
	else if (value == 672)
	    ghRB.setSelected(true);
	else 
	    shRB.setSelected(true);
	
	value = props.getProxyStrength();

	if (value == 512)
	    sbRB.setSelected(true);
	else if (value == 1024)
	    mbRB.setSelected(true);
	else if (value == 2048)
	    lbRB.setSelected(true);
	else if (value == 4096)
	    gbRB.setSelected(true);
	else 
	    sbRB.setSelected(true);
	
    }
    
    public void get(CoGProperties props) {
	props.setProxyLifeTime(Integer.parseInt(hoursGroup.getSelection().getActionCommand()));
	props.setProxyStrength(Integer.parseInt(bitsGroup.getSelection().getActionCommand()));
	
	props.setProxyFile(proxyFileFB.getFile());
    }
    
}
