/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */


package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.lang.NumberFormatException;

import java.util.*;


/**
 * This class is the Register with GRIDportal pane. It is a gui form
 * which allows a certificate to be registered with MyProxy, later to be
 * use when logging into GRIDportal.
 *
 * @author Martin Matusiak
 */
class GuiPanelMyproxyReg extends GuiPanelGeneric implements ActionListener {

	private String userCertFile;
	private String userKeyFile;
	private String globusDir;

	private JTextField hostField;
	private JTextField portField;
	private JTextField usernameField;
	private JButton registerBtn;
	
	private GuiDialogPassword passwordDlg;

	private LibMyProxy myproxy;
	private ArrayList inputFields;
	
	private LibCert cert;
	
	
	public GuiPanelMyproxyReg(Frame parent, ArrayList windows) {

		// Register components with superclass
		super(parent, windows);

		// Init backend
		myproxy = new LibMyProxy(threadlistener);
		cert = new LibCert(null);
		
		// Set up password dialog
		JPanel warningLbl = GuiToolbox.getMultilineJLabel(
			"Please enter the password corresponding\nto your certificate. You will also use this\npassword when logging into GRIDportal.",
			Color.RED
		);
		passwordDlg = new GuiDialogPassword(this, parent, "Enter a password",
			warningLbl);
		windows.add(passwordDlg);
		
		// Set border
		this.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("Register with GRIDportal")
		));
		
		// Set the layout
		this.setLayout(new BorderLayout());
		
		// Separate panel for inputfields
		JPanel fieldsPanel = new JPanel();
		
		// Set the layout for buttonPanel (rows, cols, hspace, vspace)
		fieldsPanel.setLayout(new GridLayout(4, 2, 10, 10));
		
		// Set up labels
		JLabel hostLbl = new JLabel("GRIDportal/MyProxy host:");
		JLabel portLbl = new JLabel("GRIDportal/MyProxy port:");
		JLabel usernameLbl = new JLabel("Username:");
		JLabel dummyLbl = new JLabel();
		
		JPanel instructionsLbl = GuiToolbox.getMultilineJLabel("You will probably want to leave the first two values to the default setting.\n \nYour username is set based on your certificate's DN (Distinguished Name).\nUse this username later when logging into GRIDportal.\n \n");
		this.add(instructionsLbl, BorderLayout.CENTER);
		
		// Set up inputfields
		inputFields = new ArrayList();
		
		hostField = new JTextField(Config.myProxyHost, textFieldWidth);
		hostField.setToolTipText("The host name of the GRIDportal-MyProxy server");
		inputFields.add(hostField);
		
		portField = new JTextField(Config.myProxyPort, textFieldWidth);
		portField.setToolTipText("The port number of the GRIDportal-MyProxy server");
		inputFields.add(portField);
		
		usernameField = new JTextField(textFieldWidth);
		usernameField.setToolTipText("Your username on the GRIDportal-MyProxy server and GRIDportal");
		setUsername();
		usernameField.setEnabled(false);
		
		registerBtn = new JButton("Register");
		registerBtn.setToolTipText("If disabled, detect certificate on the \"Summary\" page");
		registerBtn.addActionListener(this);
		registerBtn.setEnabled(false);
		
		// Set up components in order
		fieldsPanel.add(hostLbl);
		fieldsPanel.add(hostField);
		
		fieldsPanel.add(portLbl);
		fieldsPanel.add(portField);
		
		fieldsPanel.add(usernameLbl);
		fieldsPanel.add(usernameField);
		
		fieldsPanel.add(registerBtn);
		fieldsPanel.add(dummyLbl);
		
		this.add(fieldsPanel, BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed(ActionEvent event) {
	 
		// Reset markers for input fields before validation
		GuiToolbox.resetFieldMarkers(inputFields);
		
		// Validate input fields
		if (inputIsValid(inputFields)) {
			passwordDlg.setVisible(true);
		} else
			statusbar.addError("Field is empty or contains illegal characters.");
	}
	
	
	public void preThread(char[] password) {

		// Disable the button to prevent the process being started multiple
		// times simultaneously
		formSetStatus(false);
	
		// Set the params for myproxy
		myproxy.setPassword(new String(password));
		myproxy.setParams(hostField.getText(), portField.getText());

		startThread(myproxy, "Registering user");

	}
		
	
	public void postThread() {
	
		if (!threadlistener.hasError()) {
			statusbar.addMessage("Registration successful for on " +
				hostField.getText() + ", with username " +
				usernameField.getText() + ".");
		} else
			statusbar.addError(threadlistener.getError());
		
		// Form active again
		formSetStatus(true);
		
		// Write configuration to config file
		Config.myProxyHost = hostField.getText();
		Config.myProxyPort = portField.getText();
		Config.savePropertiesToFile();	// flush config to disk
	}
	
	
/**
 * This method validates the input in the input fields.
 */	
	private boolean inputIsValid(ArrayList inputFields) {
	
		for (int i=0; i < inputFields.size(); i++) {
			JTextField field = (JTextField) inputFields.get(i);
			String text = field.getText();
			
			// Check for empty fields
			if (text.equals("")) {
				GuiToolbox.markFieldError(field);
				return false;
			}
			
			// Check for invalid charset
			if (!cert.charsetIsValid(text)) {
				GuiToolbox.markFieldError(field);
				return false;
			}
		}
		
		// port must be an integer
		try {
			Integer.parseInt(portField.getText());
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	
	}
	
	
	private void formSetStatus(boolean status) {
		registerBtn.setEnabled(status);
		for (int i=0; i < inputFields.size(); i++) {
			JTextField field = (JTextField) inputFields.get(i);
			field.setEnabled(status);
		}
	}
	
	
	private void setUsername() {
		usernameField.setText(Config.manager.getDNAsUsername());
	}
	
	
	public void setFormStatus(boolean status) {
		registerBtn.setEnabled(status);
		setUsername();
	}

}
