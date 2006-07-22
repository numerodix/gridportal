/** 
 * GridProxyDialog.java
 *
 * A dialog to allow users to set GridProxyProperties
 *
 * $Id: GridProxyDialog.java,v 1.9 2005/04/18 15:36:46 gawor Exp $
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

import org.globus.tools.ui.util.*;
import org.globus.security.Config;

public class GridProxyDialog extends JDialog implements ActionListener {

  //protected MyproxyGUI myproxyGUI;
  //private GridProxyProperties gridProps;
  
  private FileBrowser certFileFB, keyFileFB, proxyFileFB, caCertFileFB;  
  private ButtonGroup hoursGroup = new ButtonGroup();
  private ButtonGroup bitsGroup = new ButtonGroup();
  private JButton okButton, saveButton, exitButton; 
  
  private String proxyFile = null;
  private String userCertLoc = null;
  private String userKeyLoc = null;
  private String caCertFile = null;

  private int hours = 0;
  private int bits = 0;
  private boolean limited = true;

  public GridProxyDialog() {
    //myproxyGUI = parent;
    //gridProps = parent.getGridProxyProperties();
	setTitle("Credential Properties");
        setSize(480,300);      

	Container contentPane = getContentPane();

	JJPanel gridPanel = new JJPanel();
	gridPanel.setInsets(2, 2, 2, 2);
	gridPanel.setAnchor(GridBagConstraints.EAST);
	gridPanel.setBorder(BorderFactory.createEtchedBorder());


	gridPanel.add(new JLabel("Proxy Lifetime: "),
		      0, 0, 1, 1);
	
	gridPanel.add(new JLabel("Key length: "),
		      0, 1, 1, 1);
	
	gridPanel.add(new JLabel("Location of User Certificate: "),
		      0, 2, 1, 1);
	
	gridPanel.add(new JLabel("Location of User Private Key: "),
		      0, 3, 1, 1);
	
	gridPanel.add(new JLabel("Location of CA Certificate: "),
		      0, 4, 1, 1);
	
	gridPanel.add(new JLabel("Location of Grid Proxy File: "),
		      0, 5, 1, 1);


	gridPanel.setAnchor(GridBagConstraints.WEST);

	// this makes the component to fill the entrie available space
	gridPanel.gbc.weightx = 1;

	gridPanel.setFill(GridBagConstraints.HORIZONTAL);
		
	JPanel hoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
	
	JRadioButton shRB = new JRadioButton("12 h");
	JRadioButton mhRB = new JRadioButton("24 h", true);
	JRadioButton lhRB = new JRadioButton("1 week");
	JRadioButton ghRB = new JRadioButton("1 month");
	
	shRB.setActionCommand("12");
	mhRB.setActionCommand("24");
	lhRB.setActionCommand("168");
	ghRB.setActionCommand("672");
	
	if (GridProxyConfig.getHours() == 12)
	  shRB.setSelected(true);
	else if (GridProxyConfig.getHours() == 24)
	  mhRB.setSelected(true);
	else if (GridProxyConfig.getHours() == 168)
	  lhRB.setSelected(true);
	else if (GridProxyConfig.getHours() == 672)
	  ghRB.setSelected(true);

	// Group the radio buttons.
	hoursGroup.add(shRB);
	hoursGroup.add(mhRB);
	hoursGroup.add(lhRB);
	hoursGroup.add(ghRB);
	
	hoursPanel.add(shRB);
	hoursPanel.add(mhRB);
	hoursPanel.add(lhRB);
	hoursPanel.add(ghRB);

	gridPanel.add(hoursPanel,
		      1, 0, 1, 1);

	JPanel bitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
	
	JRadioButton sbRB = new JRadioButton("512");
	JRadioButton mbRB = new JRadioButton("1024");
	JRadioButton lbRB = new JRadioButton("2048");
	JRadioButton gbRB = new JRadioButton("4096");
	sbRB.setActionCommand("512");
	mbRB.setActionCommand("1024");
	lbRB.setActionCommand("2048");
	gbRB.setActionCommand("4096");

	if (GridProxyConfig.getBits() == 512)
	  sbRB.setSelected(true);
	else if (GridProxyConfig.getBits() == 1024)
	  mbRB.setSelected(true);
	else if (GridProxyConfig.getBits() == 2048)
	  lbRB.setSelected(true);
	else if (GridProxyConfig.getBits() == 4096)
	  gbRB.setSelected(true);
	
	// Group the radio buttons.
	bitsGroup.add(sbRB);
	bitsGroup.add(mbRB);
	bitsGroup.add(lbRB);
	bitsGroup.add(gbRB);

	bitsPanel.add(sbRB);
	bitsPanel.add(mbRB);
	bitsPanel.add(lbRB);
	bitsPanel.add(gbRB);
	
	gridPanel.add(bitsPanel,
		      1, 1, 1, 1);


	certFileFB = new FileBrowser("Select User Certificate", "Select"); 
	certFileFB.setFile(Config.getUserCertFile());

	gridPanel.add(certFileFB,
		      1, 2, 1, 1);

	keyFileFB = new FileBrowser("Select User Private Key", "Select");
	keyFileFB.setFile(Config.getUserKeyFile());

	gridPanel.add(keyFileFB,
		      1, 3, 1, 1);

	caCertFileFB = new FileBrowser("Select CA Certificate", "Select");
	caCertFileFB.setFile(Config.getCaCertLocations());

	gridPanel.add(caCertFileFB,
		      1, 4, 1, 1);
	
	proxyFileFB = new FileBrowser("Select Grid Proxy File", "Select");
	proxyFileFB.setFile(Config.getProxyFile());

	gridPanel.add(proxyFileFB,
		      1, 5, 1, 1);

	contentPane.setLayout(new BorderLayout());
	contentPane.add(gridPanel, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton   = new JButton("OK");
        saveButton = new JButton("Save");
        exitButton = new JButton("Cancel");

	buttonPanel.add(okButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(exitButton);

        okButton.addActionListener(this);
        saveButton.addActionListener(this);
        exitButton.addActionListener(this);

	contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

  private boolean error(String msg) {
    JOptionPane.showMessageDialog(this, 
				  msg,
				  "Need More Information", 
				  JOptionPane.WARNING_MESSAGE);
    return false;
  }

  private boolean validateSettings() {
    System.out.println("Validating settings...");
    if (proxyFileFB.getFile().equals("")) {
      return error("Please enter the proxy file location");
    } else if (certFileFB.getFile().equals("")) {
      return error("Please enter the certificate file location");
    } else if (keyFileFB.getFile().equals("")) {
      return error("Please enter the private key file location");
    } else if (caCertFileFB.getFile().equals("")) {
      return error("Please enter the CA certificate file location");
    } 

    hours = Integer.parseInt(hoursGroup.getSelection().getActionCommand());
    bits = Integer.parseInt(bitsGroup.getSelection().getActionCommand());
    //gridProps.setProxyFile(proxyFileFB.getFile());
    proxyFile = proxyFileFB.getFile();
    //gridProps.setUserCertFile(certFileFB.getFile());
    userCertLoc = certFileFB.getFile();
    //gridProps.setUserKeyFile(keyFileFB.getFile());
    userKeyLoc = keyFileFB.getFile();
    //gridProps.setCACertFile(caCertFileFB.getFile());
    caCertFile = caCertFileFB.getFile();
    
    return true;
  }

  public int getHours() {
    return hours;
  }

  public int getBits() {
    return bits;

  }

  public String getUserCertFile() {
    return userCertLoc;
  }

  public String getUserKeyFile() {
    return userKeyLoc;
  }

  public boolean getLimited() {
    return GridProxyConfig.getLimited();
  }
  
  /**
   * Handles button events for saving and exiting
   *
   * @param evt an ActionEvent
   */ 
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == okButton) {
      System.out.println("Recognized ok button");
      if (!validateSettings()) {
	return;
      }
      //myproxyGUI.setGridProxyProperties(gridProps);
      setVisible(false);
    } else if (source == saveButton) {
      if (!validateSettings()) {
	return;
      }
      //myproxyGUI.setGridProxyProperties(gridProps);
	JOptionPane.showMessageDialog(this, 
				      "Successfully saved properties", 
				      "Information", 
				      JOptionPane.WARNING_MESSAGE); 
    } else if (source == exitButton) {
      
      setVisible(false);
    } else {
      System.err.println("Unidentified event in GridProxyDialog");
    }
  }
  
}

