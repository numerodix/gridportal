/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;


/**
 * The main gui class of the application. Lays out the various panels 
 * on a tabbed pane.
 *
 * @author Martin Matusiak
 */
class GuiMainWindow extends JFrame implements ActionListener {

	private ArrayList windows = new ArrayList();
	
	
	public GuiMainWindow(String appName) {
	
		// Send appName to superclass
		super(appName);
		
		windows.add(this);
	}
	
	
	private void buildGui() {
	
/*
	__master___________________________
	|		__________________________		|
	|		|	______________________	|		|
	|		|	|											|	|		|
	|		|	|		GuiPanel_X				|	|		|
	|		|	|_____________________|	|		|
	|		|	|											|	|		|
	|		|	|		GuiPanelStatusBar	|	|		|
	|		|	|_____________________|	|		|
	|		|													|		|
	|		|			tab_X								|		|
	|		|_________________________|		|
	|																	|
	|				tabbedPane								|
	|_________________________________|
	|																	|
	|				lower											|
	|_________________________________|
	
*/
	
		// Set up master panel
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
	
		// Set up tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
		// Set up tabs in order
		GuiPanelMyproxyReg myproxyPanel = new GuiPanelMyproxyReg(this,
			windows);
		GuiPanelMailRequest mailPanel = new GuiPanelMailRequest(this,
			windows);
		
		tabbedPane.addTab("Summary", getPanel(
			new GuiPanelSummary(this, windows, mailPanel, myproxyPanel)));
		tabbedPane.addTab("Create certificate request", getGenericPanel(
			new GuiPanelCertReqCreate(this, windows)));
		tabbedPane.addTab("Mail signing request",
			mailPanel);
		tabbedPane.addTab("Register with GRIDportal",
			getGenericPanel(myproxyPanel));
		tabbedPane.addTab("About GridPortalToolkit", getPanel(
			new GuiPanelAbout(this, windows)));
		
		// Set up lower panel
		JPanel lower = new JPanel();
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(this);
		lower.add(exitBtn);
		
		// Populate master panel
		master.add(tabbedPane, BorderLayout.NORTH);
		master.add(lower, BorderLayout.SOUTH);
		
		// Add master panel to content pane
		this.getContentPane().add(master);
	}
	
	
	public void actionPerformed(ActionEvent event) {
			System.exit(0);
	}
	
	
/**
 * Returns a GenericPanel, a panel with a statusbar.
 */	
	public JPanel getGenericPanel(GuiPanelGeneric genericPanel) {
	
		// Set up panel
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		// Set up statusbar on genericPanel
		GuiPanelStatusBar status = new GuiPanelStatusBar();
		genericPanel.setStatusBar(status);
		
		// Populate panel
		panel.add(genericPanel, BorderLayout.NORTH);
		panel.add(status, BorderLayout.SOUTH);
		
		return panel;
	}
	
	
	public JPanel getPanel(JPanel contentPanel) {
		// Set up panel
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		// Populate panel
		panel.add(contentPanel, BorderLayout.CENTER);
		
		return panel;
	}
	

	public void initGui() {
	
		// Ask for window decorations provided by the look and feel.
		//this.setDefaultLookAndFeelDecorated(true);
		
		// Exit when frame is closed
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Assemble gui
		this.buildGui();

		this.pack();
		this.setLocationRelativeTo(getParent());
 		//this.setSize(700,600);
		this.setResizable(false);
		this.setVisible(true);
	}

}