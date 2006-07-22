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
 * This class is the Mail certificate signing request pane. It simply
 * display instructions on how to request a certificate signature from the
 * Certificate Authority.
 *
 * @author Martin Matusiak
 */
class GuiPanelMailRequest extends JPanel {

	private JTextField toField;
	private JTextField sjbField;
	private JTextField attachField;
	private JTextArea bodyField;

	private Frame parent;

	private ArrayList windows;
	
	
	public GuiPanelMailRequest(Frame parent, ArrayList windows) {

		this.parent = parent;
		this.windows = windows;

		// Set the layout (rows, cols, hspace, vspace)
		this.setLayout(new BorderLayout());
		
		// Set border
		this.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("Mail certificate signing request")
		));
		
		JPanel instructionsPanel = getInstructionsPanel();
		this.add(instructionsPanel, BorderLayout.NORTH);
		
		JPanel messagePanel = getMessagePanel();
		this.add(messagePanel, BorderLayout.CENTER);

	}
	
	
	private JPanel getInstructionsPanel() {
		JPanel instructions = GuiToolbox.getMultilineJLabel("Once you have created a certificate (to verify this, detect your certificate\non the \"Summary\" page), you must request for it to be signed before you\ncan log into GRIDportal. The way to do this is to mail your request to the\nCertificate Authority and request a signature. The form below shows what\nyou should include in your request.\n \n");
		
		return instructions;
	}
	
	
	private JPanel getMessagePanel() {
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		
		// Set up message header
		JPanel header = new JPanel();
		header.setLayout(new SpringLayout());
		messagePanel.add(header, BorderLayout.CENTER);
		
		JLabel toLbl = new JLabel("To:");
		JLabel sbjLbl = new JLabel("Subject:");
		JLabel atttachLbl = new JLabel("Attachment:");
		JLabel bodyLbl = new JLabel("Message:");
		
		toField = new JTextField();
		sjbField = new JTextField();
		attachField = new JTextField();
		
		bodyField = new JTextArea(6, 25);
		JScrollPane scroll = new JScrollPane(bodyField);
		bodyField.setAutoscrolls(true);
		bodyField.setLineWrap(true);
		bodyField.setWrapStyleWord(true);
		bodyField.setFont((new JTextField()).getFont());
		bodyField.setText("<No certificate request detected, detect your certificate request on the \"Summary\" page>");
		
		header.add(toLbl);
		header.add(toField);
		
		header.add(sbjLbl);
		header.add(sjbField);
		
		header.add(bodyLbl);
		header.add(scroll);		
		
		header.add(atttachLbl);
		header.add(attachField);
		
		SpringUtilities.makeCompactGrid(header, 4, 2, 	//rows, cols
																							6, 6, 	//initx, inity
																							6, 6);	//xpad, ypad

		JPanel noteLbl = GuiToolbox.getMultilineJLabel("\n \nNOTE: Do not send your private key (" + Config.userKeyFileName + ") along, it should be\nkept confidential at all times!", Color.RED);
		messagePanel.add(noteLbl, BorderLayout.SOUTH);

		return messagePanel;
	}
	
	
	public void fillInForm() {
		try {
			String dn = Config.manager.getCertReq().getDN();
			toField.setText(Config.requestMsgAddress);
			sjbField.setText(Config.requestMsgSubject);
			attachField.setText(Config.userCertReqFile);
			bodyField.setText("Certificate subject:\n" + dn +
			"\n\n<you may want to include some information about the project you are involved in>");
		} catch (Exception e) {
			e.printStackTrace();
			bodyField.setText("<Error reading certificate request.>");
		}

	}
	

}
