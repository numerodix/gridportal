/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;


/**
 * This class provides the status bar for GuiPanelGeneric.
 *
 * @author Martin Matusiak
 */
class GuiPanelStatusBar extends JPanel {

	private JTextArea status;
	private JScrollPane scroll;


	public GuiPanelStatusBar() {
	
		// Create notification area
		status = new JTextArea(7, 35);
		status.setEditable(false);
		status.setAutoscrolls(true);
		status.setLineWrap(true);
		status.setWrapStyleWord(true);
		
		// creaty dummy field to extract font
		status.setFont((new JTextField()).getFont());
		
		// Add status bar to scrollpane
		scroll = new JScrollPane(status);
		
		// Add component to panel
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		this.add(scroll);
	
	}
	
	
	public void addMessage(String msg) {
		if (status.getText().equals(""))
			status.setText(msg);
		else
			status.append("\n" + msg);
			
		// Scroll the text area
		autoScroll();
	}


	public void addProcessIndicator(String p) {
		if (p == null)
			status.append(".");
		else
			status.append(p);

		// Scroll the text area
		autoScroll();
	}
	
	
	public void addStatus(String msg) {
		addMessage("Status: " + msg);
	}
	
	
	public void addError(String msg) {
		addMessage("======= Error =======\n" + msg);
	}	
	
	
	private void autoScroll() {
		// Determine whether the scrollbar is currently at the very bottom
		JScrollBar vbar = scroll.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		// now scroll if we were already at the bottom.
		if( autoScroll ) status.setCaretPosition( status.getDocument().getLength() );	
	}

}