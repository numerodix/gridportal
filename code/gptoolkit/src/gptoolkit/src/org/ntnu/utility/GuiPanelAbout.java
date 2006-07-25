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
 * This class is the about pane, containing some information about the
 * application, a logo and some buttons.
 *
 * @author Martin Matusiak
 */
class GuiPanelAbout extends JPanel implements ActionListener {

	private LibCommon common = new LibCommon();

	private GuiViewLicence licence;
	
	private Frame parent;
	private JButton metalLNF;
	private JButton motifLNF;
	private JButton sysLNF;

	private ArrayList windows;
	

	public GuiPanelAbout(Frame parent, ArrayList windows) {
	
		this.parent = parent;
		this.windows = windows;
	
		// Set up licence dialog
		licence = new GuiViewLicence(parent, "Show licence");
		windows.add(licence);
	
		// Setup master panel
		JPanel master = new JPanel();
	
		// Set layout
		master.setLayout(new BorderLayout());
	
		// Set border
		master.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder("About " + Config.appName)
		));
		
		// Set up upper panel
		JPanel upper = new JPanel();
		upper.setLayout(new GridLayout(1, 2, 10, 10));
		
		JPanel textLeft = GuiToolbox.getMultilineJLabel(
			Config.appName + " " + Config.appVersion +
			"\nWritten by Martin Matusiak." +
			"\n \nArchitecture: " + System.getProperty("os.arch") +
			"\nOS name: " + System.getProperty("os.name") +
			"\nOS version: " + System.getProperty("os.version") +
			"\n \nJava vendor: " + System.getProperty("java.vendor") +
			"\nJava version: " + System.getProperty("java.version")
		);
		
		ImageIcon logo = common.readImageFromJAR("/images/logotoolkit.png");
		JLabel logoLbl = new JLabel(logo);
		
		// Add components in order
		upper.add(textLeft);
		upper.add(logoLbl);
		
		master.add(upper, BorderLayout.NORTH);
		
		// Set up middle section
		JPanel copy = new JPanel();
		copy.setLayout(new BorderLayout());
		copy.add(GuiToolbox.getMultilineJLabel(
			"\n \nCopyright (c) 2005 Norwegian University of Science and Technology" +
			"\nWritten for use with GRIDportal (http://grid-portal.sourceforge.net/)" +
			"\nVisit the project site for source or binary distribution." +
			"\n \nNOTE: Source code used on licence from other projects."
		), BorderLayout.NORTH);
		master.add(copy, BorderLayout.CENTER);
		
		// Set up button
		JButton licenceBtn = new JButton("View licence");
		licenceBtn.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(licenceBtn);
		master.add(buttonPanel, BorderLayout.SOUTH);
		
		// Add LNF controls
		JPanel lowerButtons = new JPanel();
		JLabel lnfLbl = new JLabel("Set Look 'n' Feel:");
		metalLNF = new JButton("Metal");
		metalLNF.addActionListener(this);
		motifLNF = new JButton("Motif");
		motifLNF.addActionListener(this);		
		sysLNF = new JButton("System");
		sysLNF.addActionListener(this);
		lowerButtons.add(lnfLbl);
		lowerButtons.add(metalLNF);
		lowerButtons.add(motifLNF);
		lowerButtons.add(sysLNF);
		
		// Add master panel to pane
		setLayout(new BorderLayout());
		add(master, BorderLayout.CENTER);
		add(lowerButtons, BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed(ActionEvent event) {
	
		if (event.getSource().equals(metalLNF))
			GuiToolbox.setUI(GuiToolbox.lnfmet, windows);
		else if (event.getSource().equals(motifLNF))
			GuiToolbox.setUI(GuiToolbox.lnfmot, windows);
		else if (event.getSource().equals(sysLNF))
			GuiToolbox.setUI(GuiToolbox.lnfsys, windows);
		else
			licence.setVisible(true);
	}
	
}


/**
 * This is the window displaying the licence, triggered by the button
 * the about pane.
 *
 * @author Martin Matusiak
 */
class GuiViewLicence extends JDialog implements ActionListener {

	private LibCommon common = new LibCommon();


	public GuiViewLicence(Frame parent, String title) {
		
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
	
		JPanel panel = new JPanel();
		
		// Set layout
		panel.setLayout(new BorderLayout());
		
		// Set border for panel
		panel.setBorder(GuiToolbox.createGuiBorder(10, 5,
			BorderFactory.createTitledBorder(Config.appName + " licence")
		));
	
		// Add textpane with licence text
		JTextArea licence = new JTextArea(20,45);
		licence.setEditable(false);
		licence.setText(common.readFileFromJAR("org/ntnu/LICENCE"));
		
		JScrollPane scroll = new JScrollPane(licence);
		panel.add(scroll, BorderLayout.CENTER);
		
		// Add close button
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(closeBtn);
		
		// Add panel to content pane
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	
	public void actionPerformed(ActionEvent event) {
		setVisible(false);
	}
		
}
