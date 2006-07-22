/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


/**
 * This class is the Summary pane. It provides the gui to detect an 
 * existing certificate [request] and to inspect it.
 *
 * @author Martin Matusiak
 */
class GuiPanelSummary extends JPanel implements ActionListener {

	private static final int fieldWidth = 30;

	private LibCommon common = new LibCommon();
	
	JTextField certField;
	JTextField keyField;
	JTextField searchField;
	
	JButton changeBtn;
	JButton detectBtn;
	JButton viewBtn;

	private Frame parent;
	private GuiPanelMailRequest mailPanel;
	private GuiPanelMyproxyReg myproxyPanel;

	private ArrayList windows;
	
	private GuiDialogViewCertificate viewcertDlg;


	public GuiPanelSummary(Frame parent, ArrayList windows,
		GuiPanelMailRequest mailPanel, GuiPanelMyproxyReg myproxyPanel) {
	
		this.parent = parent;
		this.windows = windows;
		this.myproxyPanel= myproxyPanel;
		this.mailPanel = mailPanel;
		
		// Set up view certificate dialog
		viewcertDlg = new GuiDialogViewCertificate(parent,
			"View certificate");
		windows.add(viewcertDlg);

		// Setup master panel
		JPanel master = new JPanel();
	
		// Set layout
		master.setLayout(new BorderLayout());
		
		// Set up certificate panel
		JPanel cert = new JPanel();
		cert.setLayout(new BorderLayout());
		cert.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("Certificate")
		));
		master.add(cert, BorderLayout.NORTH);
			
		JPanel instructions = GuiToolbox.getMultilineJLabel("If you have already created a certificate, you can detect it and inspect\nit here. A certificate is contained in a file " + Config.userCertFileName + ", whereas a\ncertificate request is stored in a file " + Config.userCertReqFileName + ". If you have a\ncertificate ready, you can proceed to the page \"Register with GRIDportal\".\nIf you just have a certificate request, you will have to proceed with \"Mail\nsigning request\". If you have neither, you will have to start with \"Create\ncertificate request\".\n \nPlease note that this step is required to complete some of the other tasks\nin this program.\n ");
		cert.add(instructions, BorderLayout.NORTH);
		cert.add(getCertPanel(), BorderLayout.CENTER);
		
		// Add master panel to pane
		setLayout(new BorderLayout());
		this.add(master, BorderLayout.CENTER);
		
	}
	
	
	private JPanel getCertPanel() {
		
		JPanel certPanel = new JPanel();
		certPanel.setLayout(new BorderLayout());
		
		// Set up info panel
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new SpringLayout());

		JLabel certLbl = new JLabel("Certificate:", JLabel.TRAILING);
		certField = new JTextField(fieldWidth);
		certField.setEditable(false);
		certLbl.setLabelFor(certField);
		
		JLabel keyLbl = new JLabel("Private key:", JLabel.TRAILING);
		keyField = new JTextField(fieldWidth);
		keyField.setEditable(false);
		keyLbl.setLabelFor(keyField);
		
		JLabel pathLbl = new JLabel("Search path:", JLabel.TRAILING);
		searchField = new JTextField(Config.globusDir, fieldWidth);
		searchField.setEditable(false);
		pathLbl.setLabelFor(searchField);

		infoPanel.add(certLbl);
		infoPanel.add(certField);
		
		infoPanel.add(keyLbl);
		infoPanel.add(keyField);
		
		infoPanel.add(pathLbl);
		infoPanel.add(searchField);
		
		SpringUtilities.makeCompactGrid(infoPanel, 3, 2, 	//rows, cols
																							6, 6, 	//initx, inity
																							6, 6);	//xpad, ypad

		certPanel.add(infoPanel, BorderLayout.NORTH);
		
		// Set up button panel
		JPanel buttonPanel = new JPanel();
		certPanel.add(buttonPanel, BorderLayout.CENTER);
		
		detectBtn = new JButton("Detect certificate");
		detectBtn.addActionListener(this);
		viewBtn = new JButton("View certificate");
		viewBtn.setEnabled(false);
		viewBtn.addActionListener(this);
		
		buttonPanel.add(detectBtn);
		buttonPanel.add(viewBtn);
	 
		
		return certPanel;
	}
	 
	 
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(detectBtn))
			detectCert();
		/*	{LibMyProxy myproxy = new LibMyProxy(null);
			myproxy.setParams(Config.myProxyHost, Config.myProxyPort);
			myproxy.getProxy();}
			*/
		else if (event.getSource().equals(viewBtn)) {
			viewcertDlg.setupTree(Config.manager.getCertObject().getAttributes());
			viewcertDlg.setVisible(true);
		}
	}
	
	
	private void detectCert() {
		
		// Clear from last detect
		viewBtn.setEnabled(false);
		clearField(certField);
		clearField(keyField);
		
		
		// Attempt to read private key
		try {
			Config.manager.getKey();
			reportSuccess(keyField, "Found in " + Config.userKeyFileName
				+ ", loaded successfully");
		} catch (Exception e) {
			reportError(keyField, e.getMessage());
		}
		
		
		int error = 0;
		
		// First, attempt to read certificate
		try {
			Config.manager.getCert();
			reportSuccess(certField, "Found in " + Config.userCertFileName
				+ ", loaded successfully");
			viewBtn.setEnabled(true);
			myproxyPanel.setFormStatus(true);
		} catch (NorduGridCertCorruptException e) {
			error = 1;
			reportError(certField, e.getMessage());
			viewBtn.setEnabled(false);
		} catch (Exception e) {
			error = 2;
			reportError(certField, e.getMessage());
		}
		
		// If no errors found, return now
		if (error == 0) return;
		
		// Certificate not valid, disable functions which require it
		myproxyPanel.setFormStatus(false);
		
		// Otherwise try to read certificate request
		try {
			Config.manager.getCertReq();
			if (error != 1) {
				reportWarning(certField, "Found in " + Config.userCertReqFileName
					+ ", needs signing");
				viewBtn.setEnabled(true);
				mailPanel.fillInForm();
			}
		} catch (Exception e) {
			reportError(certField, e.getMessage());
		}

		//parent.pack();
	}
	
	
	private void clearField(JTextField field) {
		field.setText(null);
		GuiToolbox.unmarkField(field);
	}
	
	
	private void reportError(JTextField field, String msg) {
		field.setText(msg);
		GuiToolbox.markFieldError(field);
	}
	
	
	private void reportWarning(JTextField field, String msg) {
		field.setText(msg);
		GuiToolbox.markFieldWarning(field);
	}
	
	
	private void reportSuccess(JTextField field, String msg) {
		field.setText(msg);
		GuiToolbox.markFieldSuccess(field);
	}
	
	
	private void reportMessage(JTextField field, String msg) {
		field.setText(msg);
		GuiToolbox.unmarkField(field);
	}
		
}


/**
 * This class is the certificate viewer, a simple text area which displays
 * the details of the certificate.
 *
 * @author Martin Matusiak
 */
class GuiDialogViewCertificate extends JDialog implements ActionListener {

	private static final int labelWidth = 25;

	JTextArea text;


	public GuiDialogViewCertificate(Frame parent, String title) {
		
		// Set dialog title
		super(parent, title);
	
		// Set up gui components
		setupGui();
		
		// Finalize
		this.pack();
		//this.setSize(500,400);
		this.setLocationRelativeTo(getParent());
	}
		
	
	public void setupGui() {
	
		// Set up certview panel
		JPanel certviewPanel = new JPanel();
		certviewPanel.setLayout(new BorderLayout());
		
		// Set border for panel
		certviewPanel.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("Certificate info")
		));
		
		text = new JTextArea(20, 55);
		text.setEditable(false);
		text.setFont(new Font("DialogInput", Font.PLAIN, 11));
		JScrollPane pane = new JScrollPane(text);
		certviewPanel.add(pane, BorderLayout.CENTER);
		//tree.setScrollsOnExpand(true);
		
		// Set up button panel
		JPanel buttonPanel = new JPanel();
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(this);
		buttonPanel.add(closeBtn);
		
		
		// Add panel to content pane
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(certviewPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	
/**
 * This method processes the data from NorduGridCertManager.getCertObject().
 * I meant to use a JTreeTable object for this information but abandoned
 * that idea in favor of a simpler solution.
 */	
	public void setupTree(Vector[] data) {
		text.setText("");
		
		for (int i=0; i < data.length; i++) {
		Vector attributes = data[i];
		
 			for (int j=0; j < attributes.size(); j++) {
 				Vector pair = (Vector) attributes.get(j);
 				
 				boolean isHeading = false;
 				if (j == 0) isHeading = true;

				// print header if not attribute
				if (isHeading) { 
					text.append("+++++++++++  ");
					// print label
					text.append((String) pair.get(0));
					// print header if not attribute
					text.append("  +++++++++++");
				} else {
					text.append(GuiToolbox.getFixedWidthString((String) 
						pair.get(0) + ": ", labelWidth));
				}
				
				// print label
				if (pair.get(1) != null)
					text.append("   " + (String) pair.get(1));
				
				// end line if this isn't the last entry
				if (j-1 < attributes.size()) text.append("\n");

 			}
 		
 		// end line if this isn't the last entry
 		if (i-1 < data.length) text.append("\n");
		}
		
	}
	
	
	public void actionPerformed(ActionEvent event) {
		setVisible(false);
	}

}

