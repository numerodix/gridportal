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

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.globus.common.CoGProperties;

public class PKCS11ProxyOptionsPanel extends ProxyOptionsPanel {

    protected JTextField certHandleTF, keyHandleTF;

    public PKCS11ProxyOptionsPanel() {
	super();

	setInsets(2, 2, 2, 2);

	setAnchor(GridBagConstraints.EAST);
	gbc.weightx = 0;
	setFill(GridBagConstraints.NONE);

	add(new JLabel("User Certificate Handle: "),
	    0, 4, 1, 1);
	
	add(new JLabel("User Private Handle: "),
	    0, 5, 1, 1);
	
	setAnchor(GridBagConstraints.WEST);
	
	// this makes the component to fill the entrie available space
	gbc.weightx = 1;
	
	setFill(GridBagConstraints.HORIZONTAL);
		
	certHandleTF = new JTextField(20);
	
	add(certHandleTF,
	    1, 4, 1, 1);
	
	keyHandleTF = new JTextField(20);
	
	add(keyHandleTF,
	    1, 5, 1, 1);
    }
    
    public boolean validateSettings() {
	if (!super.validateSettings()) return false;
	
	if (certHandleTF.getText().equals("") &&
	    keyHandleTF.getText().equals("")) {
	    return error("Please enter the certificate or the private key handle.");
	} 
	return true;
    }
    
    public void set(CoGProperties props) {
	super.set(props);
	
	String certH = props.getProperty("pkcs11.certhandle");
	String keyH  = props.getProperty("pkcs11.keyhandle");
	
	if (certH == null || certH.length() == 0) {
	    if (keyH == null || keyH.length() == 0) {
		certH = keyH = props.getDefaultPKCS11Handle();
	    } else {
		certH = keyH;
	    }
	} else {
	    if (keyH == null || keyH.length() == 0) {
		keyH = certH;
	    }
	}
	
	certHandleTF.setText(certH);
	keyHandleTF.setText(keyH);
    }

    public void get(CoGProperties props) {
	super.get(props);
	
	String certH = certHandleTF.getText();
	String keyH  = keyHandleTF.getText();
	
	if (certH.length() == 0) {
	    if (keyH.length() == 0) {
		certH = keyH = props.getDefaultPKCS11Handle();
	    } else {
		certH = keyH;
	    }
	} else {
	    if (keyH.length() == 0) {
		keyH = certH;
	    }
	}
	
	props.put("pkcs11.certhandle", certH);
	props.put("pkcs11.keyhandle", keyH);
    }
    
}
