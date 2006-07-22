/** 
 * MyproxyInit.java
 *
 * Provides a simple GUI for creating a proxy and delegating to a myproxy server
 *
 * $Id: MyproxyInit.java,v 1.10 2005/04/18 15:36:46 gawor Exp $
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
import org.globus.util.*;

import org.globus.tools.ui.util.*;

public class MyproxyInit extends MyproxyGUI 
  implements ActionListener, WindowListener {

    public static final int FRAME_WIDTH = 430;
    public static final int FRAME_HEIGHT = 180;

    private JTextField usernameTF      = new JTextField(15);
    private JPasswordField passwordTF  = new JPasswordField(15); 

    private JButton createButton, sendButton, killLocButton, killRemButton, exitButton;
    private boolean runAsApplication = true;
    private static GlobusProxy gridProxy = null;
    private String myproxyPassword = "";
    
    /**
     * Create the interface
     */
    public MyproxyInit() { 

	// set up GUI
        setTitle("Myproxy Init");
	
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

	addWindowListener( this );

	Container contentPane = this.getContentPane();
	
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);
	
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	exitMenuItem.addActionListener(this);
	fileMenu.add(exitMenuItem);

	JMenu editMenu = new JMenu("Edit");
	menuBar.add(editMenu);
	
	JMenuItem cpMenuItem = new JMenuItem("Certificate Properties");
	cpMenuItem.addActionListener(this);
	editMenu.add(cpMenuItem);

	JMenuItem mpMenuItem = new JMenuItem("Myproxy Properties");
	mpMenuItem.addActionListener(this);
	editMenu.add(mpMenuItem);


	JPanel buttonPanel = new JPanel();
	//buttonPanel.setLayout(new BorderLayout());
	
	JPanel proxyPanel = new JPanel();
	proxyPanel.setBorder(BorderFactory.createTitledBorder(" Local Proxy "));
	
	createButton  = new JButton("Create");
	killLocButton = new JButton("Destroy");
	
	createButton.addActionListener(this);
	killLocButton.addActionListener(this);

	proxyPanel.add(createButton);
	proxyPanel.add(killLocButton);

	JPanel myproxyPanel = new JPanel();
	myproxyPanel.setBorder(BorderFactory.createTitledBorder(" Myproxy "));

	sendButton    = new JButton("Init");	
	killRemButton = new JButton("Destroy");

	sendButton.addActionListener(this);
	killRemButton.addActionListener(this);

	myproxyPanel.add(sendButton);
	myproxyPanel.add(killRemButton);

	buttonPanel.add(proxyPanel); //, BorderLayout.WEST);
	buttonPanel.add(myproxyPanel); //, BorderLayout.EAST);

	
	JJPanel mainPanel = new JJPanel();
	//mainPanel.setBorder(BorderFactory.createEtchedBorder());
	mainPanel.setInsets(5, 5, 5, 5);
	mainPanel.setAnchor(GridBagConstraints.EAST);

	mainPanel.add(new JLabel("Username: "),
		      0, 0, 1, 1);

	mainPanel.add(new JLabel("PEM Passphrase: "),
		      0, 1, 1, 1);
	
	mainPanel.setAnchor(GridBagConstraints.WEST);
	mainPanel.gbc.weightx = 1;
	mainPanel.setFill(GridBagConstraints.HORIZONTAL);

	mainPanel.add(usernameTF,
		      1, 0, 1, 1);

	usernameTF.addActionListener( this );
	
	mainPanel.add(passwordTF,
		      1, 1, 1, 1);
	
	passwordTF.addActionListener( this );

	contentPane.add(mainPanel, BorderLayout.CENTER);
	contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Event handlers for button panel
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
	String cmd =  evt.getActionCommand();

	/** make okButton the default input button **/
	//if( source == passwordTF ) {
	//    source = okButton;
	//}
	
	/* Check menu */
	if (cmd.equals("Certificate Properties")) {
	    GridProxyDialog gpd = new GridProxyDialog();
	    gpd.show();
	    return;
	} else if (cmd.equals("Myproxy Properties")) {
	    MyproxyDialog mpd = new MyproxyDialog(this);
	    mpd.show();
	    return;
	} else if (cmd.equals("Exit")) {
	    this.exit(0);
	    return;
	}
 
	/* Check Buttons */
        if (source == createButton) {

	  if (validatePassword()) {
	    if (createProxy(gridProps.getHours())) {
		JOptionPane.showMessageDialog(
		    this,
		    "Succesfully created a proxy in " + gridProps.getProxyFile(),
		    "Success",
		    JOptionPane.PLAIN_MESSAGE);
	    }
	  }

	} else if (source == sendButton) {

	  if (validateUsername() && validatePassword() && validateMyproxyPassword()) {
	    if (createProxy(gridProps.getCredLifetime())) sendProxy();
	  }

	} else if (source == killLocButton) {
	    deleteProxy();
	} else if (source == killRemButton) {

	  if (validateUsername() && validateMyproxyPassword()) {
	    deleteMyproxy();
	  }

        } else {
            System.err.println("Unidentified event in MyproxyInit" + cmd + source);
        }
    } 

  private boolean validateUsername() {
    if (usernameTF.getText().trim().equals("")) {
      JOptionPane.showMessageDialog(this, 
				    "Please enter your username.",
				    "Need More Information",
				    JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
    
  private boolean validatePassword() {
    if (passwordTF.getPassword().length == 0) {
      JOptionPane.showMessageDialog(this, 
				    "Please enter your password.", 
				    "Need More Information", 
				    JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
    
  private boolean validateMyproxyPassword() {
    if (myproxyPassword.equals("")) {
      PasswordDialog passDialog = new PasswordDialog(this);
      passDialog.show();
      return !passDialog.isCanceled();
    }
    return true;
  }

  public void deleteProxy() {
    gridProxy = null;
    if (!gridProps.getProxyFile().equals("")) {
      File proxyFile = new File(gridProps.getProxyFile());
      if (proxyFile.exists()) {
	if (!Util.destroy(proxyFile)) {
	  JOptionPane.showMessageDialog(this, 
					"Unable to destroy local proxy", 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	} else {
	  JOptionPane.showMessageDialog(this,
					"Succesfully destroyed proxy in " + gridProps.getProxyFile(),
					"Success",
					JOptionPane.PLAIN_MESSAGE);
	}
      } else {
	      JOptionPane.showMessageDialog(this,
					    "No proxy exists in " + gridProps.getProxyFile(),
					    "Information",
					    JOptionPane.INFORMATION_MESSAGE);
	    }
    }
  }

    public void deleteMyproxy() {
	if (gridProxy == null)
	    return;
	
	try {
	    MyProxy.destroy(gridProps.getMyproxyServer(),
			      gridProps.getMyproxyPort(),
			                       gridProxy,
			     usernameTF.getText().trim(),
			                 myproxyPassword);
	    JOptionPane.showMessageDialog(
            this,
            "Succesfully destroyed proxy from myproxy server",
            "Success",
            JOptionPane.PLAIN_MESSAGE);
        return; 
	} catch (MyProxyException e) {
	    JOptionPane.showMessageDialog(this,
		"Failed to delete proxy from myproxy server!\n\"" 
                + e.getMessage() + "\"",
	        "Error",
		 JOptionPane.ERROR_MESSAGE);
	}
    }

    public boolean createProxy(int hours) {	
	X509Certificate userCert = null;	
	PrivateKey userKey = null;  
	char[] pwd;

	try {
	    userCert = CertUtil.loadCert(gridProps.getUserCertFile());
	    
	    if (userCert == null) {
		JOptionPane.showMessageDialog(this, 
		                "Failed to load cert: " 
			  + gridProps.getUserCertFile(), 
					        "Error", 
			      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    
	    pwd = passwordTF.getPassword();
	    userKey  = CertUtil.loadUserKey(gridProps.getUserKeyFile(), new String(pwd));
	    pwd = null;
	    
	    if (userKey == null) {
		JOptionPane.showMessageDialog(this, 
			 "Failed to load private key: " 
			  + gridProps.getUserKeyFile(), 
                                               "Error", 
			     JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    
	    gridProxy = CertUtil.createProxy(userCert,
					      userKey,
				  gridProps.getBits(),
				                hours,
			       gridProps.getLimited());
	    
	    if (gridProxy == null) {
		JOptionPane.showMessageDialog(this, "Failed to create proxy!", "Error", JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    
	    gridProxy.save(gridProps.getProxyFile());
	        
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(
	    	this,
		"Failed to create proxy!\n\"" + e.getMessage() + "\"",
		"Error",
		JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	return true;
    }

    public void sendProxy() {

	if ((gridProxy == null) || (myproxyPassword.equals("")))
	    return;
	
	if (myproxyPassword.equals("")) {
	    PasswordDialog passDialog = new PasswordDialog(this);
	    passDialog.show();
	}
	
	try {
	    MyProxy.put(gridProps.getMyproxyServer(),
			  gridProps.getMyproxyPort(),
		                           gridProxy,
			 usernameTF.getText().trim(),
			             myproxyPassword,
		         gridProps.getCredLifetime());
	    JOptionPane.showMessageDialog(
	    this,
	    "Succesfully delegated a proxy to myproxy server",
	    "Success",
	    JOptionPane.PLAIN_MESSAGE);
	} catch(MyProxyException e) {
	    JOptionPane.showMessageDialog(
	    	this,
		"Failed to delegate proxy to myproxy server!\n\"" 
                + e.getMessage() + "\"",
		"Error",
		JOptionPane.ERROR_MESSAGE);
	}
	return;
    }

    public void setMyproxyPassword(String password) {
	this.myproxyPassword = password;
    }

    public void setRunAsApplication( boolean runAsApplication ) {
    	this.runAsApplication = runAsApplication;
    }

    public boolean isRunAsApplication() {
    	return this.runAsApplication;
    }

    private void exit( int exitValue ) {
    	if( this.isRunAsApplication() ) {
	    System.exit( exitValue );
	} else {
	    this.setVisible( false );
	}
    }

    /*** WindowListener Methods ***/
    public void windowActivated( WindowEvent we ) { }
    public void windowClosed( WindowEvent we ) { }
    public void windowDeactivated( WindowEvent we ) { }
    public void windowDeiconified( WindowEvent we ) { }
    public void windowIconified( WindowEvent we ) { }
    public void windowOpened( WindowEvent we ) { }
    public void windowClosing( WindowEvent we ) {
    	this.exit( 0 );
    }
    /******************************/
    
    /**
     * Start the program
     */
    public static void main(String args[]) {
	String vers = System.getProperty("java.version");
	if (vers.compareTo("1.1.2") < 0) {
            System.out.println("!!!WARNING: Swing must be run with a " +
                               "1.1.2 or higher version VM!!!");
        }

	MyproxyInit mpiFrame = new MyproxyInit();
	UITools.center(null, mpiFrame);
        mpiFrame.setVisible(true);
    }
    
}














