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
 * A generic password dialog which accepts a password of at least 6
 * characters and verifies it is in a valid charset. Instructions for
 * the password can be passed to the constructor.
 *
 * @author Martin Matusiak
 */
class GuiDialogPassword extends JDialog implements ActionListener,
	WindowListener {

	JPasswordField passField;
	JPasswordField passrepField;
	JButton okBtn;
	JButton cancelBtn;
	
	private GuiPanelGeneric parent;
	private LibCert cert;


	public GuiDialogPassword(GuiPanelGeneric parent, Frame owner,
		String title, JPanel warningLbl) {
	
		// Pass to superclass, make modal
		super(owner, title, true);
		
		this.parent = parent;
		
		// Init backend
		cert = new LibCert(null);
		
		setupGui(warningLbl);
		
		this.addWindowListener(this);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(getParent());
	}
	
	
	private void setupGui(JPanel warningLbl) {
	
		// Create a panel to hold everything
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.getContentPane().add(panel);
		
		if (warningLbl != null) {
			// Create a panel for the text label
			JPanel upper = new JPanel();
	
			//warningLbl.setEnabled(false);
			upper.add(warningLbl);
			panel.add(upper, BorderLayout.NORTH);
		}
 		
 		// Dummy panel to fix a layout bug
 		JPanel dummyPanel = new JPanel();
 		panel.add(dummyPanel, BorderLayout.CENTER);
		
		// Create a panel for the rest
		JPanel lower = new JPanel();
		panel.add(lower, BorderLayout.SOUTH);
		
		// Set border for master panel
		panel.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)
		));
		
		// Set up layout for lower panel
		lower.setLayout(new GridLayout(3, 2, 10, 10));
		
		// Set up labels
		JLabel passLbl = new JLabel("Password:");
		JLabel passrepLbl = new JLabel("Repeat password:");
		
		// Set up inputfields
		passField = new JPasswordField();
		passField.setToolTipText("Enter a password to use with the certificate");
		passField.setText("");
		
		passrepField = new JPasswordField();
		passrepField.setToolTipText("Enter the password again for verification");
		passrepField.setText("");
		
		// Set up button
		okBtn = new JButton("Proceed");
		okBtn.addActionListener(this);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		
		// Set up components in order
		lower.add(passLbl);
		lower.add(passField);
		
		lower.add(passrepLbl);
		lower.add(passrepField);
		
		lower.add(okBtn);
		lower.add(cancelBtn);
	}
	
	
	public void actionPerformed(ActionEvent event) {
		char[] pass1 = passField.getPassword();
		char[] pass2 = passrepField.getPassword();
	
		// Find out which button triggered the event
		if (event.getSource().equals(okBtn)) {								// Ok button

			// Check that passwords match
			if (!passwordsEqual(pass1, pass2))
				JOptionPane.showMessageDialog(this, "The passwords do not match.",
					"Password mismatch", JOptionPane.ERROR_MESSAGE);
					
			// Openssl requires password to be at least 4 characters long
			// MyProxy requires password to be at least 6 characters long
			else if (pass1.length < 6)
				JOptionPane.showMessageDialog(this, "The password must be at least 6 characters long.",
					"Password too short", JOptionPane.ERROR_MESSAGE);
					
			// Check that password does not contain illegal chars
			else if (!cert.charsetIsValid(new String(pass1)))
				JOptionPane.showMessageDialog(this, "The password contains illegal characters.",
					"Password invalid", JOptionPane.ERROR_MESSAGE);
					
			else {
				this.dispose();
				parent.preThread(pass1);
			}

		} else if (event.getSource().equals(cancelBtn)) {		// Cancel button
			this.dispose();
		}
		
	}
	
	
	// Window closing should nullify text fields
	public void windowClosed(WindowEvent e) {
		resetPassword();
	}
	
	// Window closing should nullify text fields
	public void windowClosing(WindowEvent e) {
		resetPassword();
	}
	
	// Must implement abstract functions	
	public void windowOpened(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	
	
	private boolean passwordsEqual(char[] first, char[] second) {
	
		// Passwords not the same length
		if (first.length != second.length) return false;
	
		// Passwords not equal
		for (int i=0; i < first.length; i++) {
			if (first[i] != second[i]) return false;
		}
		
		return true;
	}
	
	
	private void resetPassword() {
		passField.setText("");
		passrepField.setText("");
	}

}