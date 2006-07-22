/** 
 * PasswordDialog.java
 *
 * A dialog to allow users to set the password used in storing/protecting 
 * a user's proxy on the myproxy server
 *
 * $Id: PasswordDialog.java,v 1.7 2005/04/18 15:36:46 gawor Exp $
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
import java.io.*;
import java.util.*;

import org.globus.tools.ui.util.*;

public class PasswordDialog extends JDialog implements ActionListener {

  protected MyproxyInit myproxyInit;
  private JPasswordField passwd1TF = new JPasswordField(10);
  private JPasswordField passwd2TF = new JPasswordField(10);
  
  private JButton okButton, cancelButton; 
  private GridProxyProperties gridProps;
  private Properties proxyProps;
  private String password = null;
  
  private boolean cancel = false;

    public PasswordDialog(MyproxyInit parent) {
	super(parent, "Enter Password", true);
	myproxyInit = parent;
	gridProps = parent.getGridProxyProperties();
                setSize(450,125);      

	Container contentPane = getContentPane();

	JJPanel gridPanel = new JJPanel();
	gridPanel.setInsets(2, 2, 2, 2);
	gridPanel.setAnchor(GridBagConstraints.EAST);
	gridPanel.setBorder(BorderFactory.createEtchedBorder());

	gridPanel.add(new JLabel("Enter Password to Protect Proxy: "),
		      0, 0, 1, 1);
	
	gridPanel.add(new JLabel("Enter Password Again: "),
		      0, 1, 1, 1);

	gridPanel.setAnchor(GridBagConstraints.WEST);

	gridPanel.gbc.weightx = 1;
	gridPanel.setFill(GridBagConstraints.HORIZONTAL);
	
	gridPanel.add(passwd1TF,
		      1, 0, 1, 1);
	
	gridPanel.add(passwd2TF,
		      1, 1, 1, 1);
	
	contentPane.add(gridPanel, BorderLayout.CENTER);
	
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton   = new JButton("OK");
        cancelButton = new JButton("Cancel");
	
	buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
	contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateSettings() {

	char[] pwd1 = passwd1TF.getPassword();
	char[] pwd2 = passwd2TF.getPassword();

	if ((pwd1 != null) && (pwd2 != null)) {
	    String p1 = new String(pwd1);
	    String p2 = new String(pwd2);
	    if (p1.equals(p2)) {
		password = p1;
		return true;
	    }
	} 
	
	JOptionPane.showMessageDialog(this, 
				      "Passwords do not match!", 
				      "Error", 
				      JOptionPane.ERROR_MESSAGE);
	return false;
    }
  
  public boolean isCanceled() {
    return cancel;
  }

  /**
   * Handles button events for saving and exiting
   *
   * @param evt an ActionEvent
   */ 
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == okButton) {
      if (validateSettings()) {
	myproxyInit.setMyproxyPassword(password);
	cancel = false;
	setVisible(false);
      }
    } else if (source == cancelButton) {
      cancel = true;
      setVisible(false);
    } else {
      System.err.println("Unidentified event in PasswordDialog");
    }
  }
  
}
