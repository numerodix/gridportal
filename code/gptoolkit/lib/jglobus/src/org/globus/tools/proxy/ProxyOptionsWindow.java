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

import java.awt.Dialog;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;

import org.globus.common.CoGProperties;

public class ProxyOptionsWindow extends JDialog 
    implements ActionListener {
    
    private ProxyOptionsPanel optionsPanel;
    private CoGProperties properties;
    
    private JButton applyBt = null;
    private JButton cancelBt = null;

    public ProxyOptionsWindow(Dialog owner, boolean modal, boolean pkcs11) {
	
	super(owner, modal);
	setTitle("Options");
	
	if (pkcs11) {
	    optionsPanel = new PKCS11ProxyOptionsPanel();
	    optionsPanel.setBorder(BorderFactory.createTitledBorder(" PKCS11 Options "));
	} else {
	    optionsPanel = new DefaultProxyOptionsPanel();
	    optionsPanel.setBorder(BorderFactory.createTitledBorder(" Proxy Options "));
	}
	
	JPanel buttonPanel = new JPanel();
	applyBt = new JButton("Apply");
	applyBt.addActionListener(this);
	buttonPanel.add(applyBt);
	cancelBt = new JButton("Cancel");
	cancelBt.addActionListener(this);
	buttonPanel.add(cancelBt);
	
	Container contentPane = this.getContentPane();
	
	contentPane.setLayout(new BorderLayout());
	contentPane.add(optionsPanel, BorderLayout.CENTER);
	contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setProperties(CoGProperties props) {
	if (props == null) {
	    throw new IllegalArgumentException("Properties cannot be null");
	}
	properties = props;
	optionsPanel.set(props);
    }

    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource();
	if (source == cancelBt) {
	    properties = null;
	    dispose();
	} else if (source == applyBt && optionsPanel.validateSettings()) {
	    optionsPanel.get(properties);
	    dispose();
	}
    }
    
    public CoGProperties getProperties() {
	return properties;
    }
    
}
