/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


/**
 * The Create certificate request pane.
 *
 * @author Martin Matusiak
 */
class GuiPanelCertReqCreate extends GuiPanelGeneric implements ActionListener {

	private JTextField nameField;
	private JTextField emailField;
	private JTextField domainField;
	private JTextField org0Field;
	private JTextField org1Field;
	private JButton createBtn;

	private GuiDialogPassword passwordDlg;
	
	private LibCert cert;
	private ArrayList inputFields;
	
	
	public GuiPanelCertReqCreate(Frame parent, ArrayList windows) {

		// Register components with superclass
		super(parent, windows);

		// Init backend
		cert = new LibCert(threadlistener);
		
		// Set up password dialog
		JPanel warningLbl = GuiToolbox.getMultilineJLabel(
			"Please choose this password with care, \nonce the certificate is created, \nit cannot be used without this password. \nNote also that if the password is lost, \na new certificate must be created.",
			Color.RED
		);
		passwordDlg = new GuiDialogPassword(this, parent, "Enter a password",
			warningLbl);
		windows.add(passwordDlg);
		
		// Set the layout (rows, cols, hspace, vspace)
		this.setLayout(new GridLayout(6, 2, 10, 10));
		
		// Set border
		this.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("Create certificate request")
		));
		
		// Set up labels
		JLabel nameLbl = new JLabel("Full name:");
		JLabel emailLbl = new JLabel("Email address:");
		JLabel domainLbl = new JLabel("Domain:");
		JLabel org0Lbl = new JLabel("Level-0 Organization:");
		JLabel org1Lbl = new JLabel("Level-1 Organization:");
		JLabel dummyLbl = new JLabel();
		
		// Set up inputfields
		inputFields = new ArrayList();
		
		nameField = new JTextField(textFieldWidth);
		nameField.setToolTipText("Your full name");
		inputFields.add(nameField);
		
		emailField = new JTextField(textFieldWidth);
		emailField.setToolTipText("Your email address");
		inputFields.add(emailField);
		
		domainField = new JTextField(textFieldWidth);
		domainField.setToolTipText("The domain your of institution (e.g. \"ntnu.no\"), probably the part of your email address after the \"@\"");
		inputFields.add(domainField);
		
		org0Field = new JTextField(NorduGridCertificate.org0, textFieldWidth);
		org0Field.setEnabled(false);
		
		org1Field = new JTextField(NorduGridCertificate.org1, textFieldWidth);
		org1Field.setEnabled(false);
		
		// Set up button
		createBtn = new JButton("Create");
		createBtn.addActionListener(this);
		
		// Set up components in order
		this.add(nameLbl);
		this.add(nameField);
		
		this.add(emailLbl);
		this.add(emailField);
		
		this.add(domainLbl);
		this.add(domainField);
		
		this.add(org0Lbl);
		this.add(org0Field);
		
		this.add(org1Lbl);
		this.add(org1Field);
		
		this.add(createBtn);
		this.add(dummyLbl);
	}
	
	
	public void actionPerformed(ActionEvent event) {
	
		// Reset markers for input fields before validation
		GuiToolbox.resetFieldMarkers(inputFields);
		
		// Validate input fields
		if (inputIsValid(inputFields))
			passwordDlg.setVisible(true);
		else
			statusbar.addError("Field is empty or contains illegal characters.");
	}
	
	
	public void preThread(char[] password) {

		// Disable the button to prevent the process being started multiple
		// times simultaneously
		formSetStatus(false);
	
		// Set the DN for the certificate
		cert.setDN(nameField.getText(), emailField.getText(),
			domainField.getText(), org0Field.getText(), org1Field.getText());
		cert.setPassword(password);
		cert.setPath(Config.globusDir);
		cert.setKeys(Config.userCertFileName, Config.userCertReqFileName,
			Config.userKeyFileName);

		startThread(cert, "Creating certificate");

	}


	public void postThread() {
	
		if (!threadlistener.hasError()) {
			statusbar.addMessage(
				"Certificate creation successful in: " + Config.globusDir + "\n" +
				"Certificate file: " + Config.userCertReqFileName + "\n" +
				"Private key file: " + Config.userKeyFileName);
		} else
			statusbar.addError(threadlistener.getError());
			
		// Form active again
		formSetStatus(true);
		
	}
	
	
/**
 * Validates input in all input fields.
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
		
		return true;
	
	}


	private void formSetStatus(boolean status) {
		createBtn.setEnabled(status);
		for (int i=0; i < inputFields.size(); i++) {
			JTextField field = (JTextField) inputFields.get(i);
			field.setEnabled(status);
		}
	}

}
