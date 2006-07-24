/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;


/**
 * This class provides some common gui functions.
 *
 * @author Martin Matusiak
 */
class GuiToolbox {

	private static final String lnf_met = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static final String lnf_win = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static final String lnf_mot = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private static final String lnf_gtk = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

	public static final int lnfsys = 0;
	public static final int lnfmet = 1;
	public static final int lnfmot = 2;


/**
 * A CompoundBorder can only use two borders, this method takes a
 * border and wraps it with two EmptyBorders to get some space between
 * the border and the content.
 *
 * @param innerwidth padding inside the border
 * @param outerwidth padding outside the border
 * @param border the border to wrap around
 */
	public static Border createGuiBorder(int innerwidth, int outerwidth, 
		Border border) {
		
		// Create a Compound border, then wrap in a second compound
		
		CompoundBorder comp1 = new CompoundBorder(
			BorderFactory.createEmptyBorder(outerwidth,outerwidth,
				outerwidth,outerwidth), border);
				
		CompoundBorder comp2 = new CompoundBorder(comp1,
			BorderFactory.createEmptyBorder(innerwidth-2,innerwidth,
				innerwidth-5,innerwidth));
				
		return comp2;
	}
	
	
	public static void markFieldError(JTextField field) {
		field.setBackground(Config.errorColor);
	}
	
	
	public static void markFieldWarning(JTextField field) {
		field.setBackground(Config.warningColor);
	}
	
	
	public static void markFieldSuccess(JTextField field) {
		field.setBackground(Config.successColor);
	}
	
	
	public static void unmarkField(JTextField field) {
		field.setBackground(new JTextField().getBackground());
	}
	
	
/**
 * Reset the marking done by the makrField* methods.
 */	
	public static void resetFieldMarkers(ArrayList inputFields) {
		JTextField dummy = new JTextField();
		for (int i=0; i < inputFields.size(); i++) {
			JTextField field = (JTextField) inputFields.get(i);
			field.setBackground(dummy.getBackground());
		}
	}
	
	
	public static JPanel getMultilineJLabel(String text) {
		return getMultilineJLabel(text, null);
	}
	
	
/**
 * JLabels can not be multiline. This method takes a string with linebreaks
 * and creates a JLabel on a JPanel for every segment of the string between
 * linebreaks.
 *
 * @param text The text of the label
 * @param color The color of the text
 */
	public static JPanel getMultilineJLabel(String text, Color color) {
		JPanel panel = new JPanel();

		StringTokenizer tok = new StringTokenizer(text, "\n");
		
		panel.setLayout(new GridLayout(tok.countTokens(), 1, 1, 1));
		
		while (tok.hasMoreTokens()) {
			JLabel label = new JLabel(tok.nextToken());
			panel.add(label);
			if (color != null)
				label.setForeground(color);
		}

		return panel;
	}
	

/**
 * Take a string of unknown size and expand it to a fixed size.
 *
 * @param string The string to expand
 * @param width The requested size
 */
	public static String getFixedWidthString(String string, int width) {
		String empty = "";
		
		if (string.length() < width) {
			for (int i=0; i<(width-string.length()); i++) {
				empty += " ";
			}
		}
		
		return string += empty;
	}
	
	
/**
 * Set the look and feel of the application. The input is an ArrayList
 * of Window objects, which make up the application.
 *
 * @param lnf The look and feel (as public constant of the class)
 * @param windows The collection of Window objects to set the lnf for
 */	
	public static void setUI(int lnf, ArrayList windows) {
		
		try {
			// not fool proof, known to fail on exotic linux configurations
/*			if ((lnf == lnfsys) && (System.getProperty("os.name").equals("Linux")))
				UIManager.setLookAndFeel(lnf_gtk);
			else */
			if (lnf == lnfsys)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else if (lnf == lnfmot)
				UIManager.setLookAndFeel(lnf_mot);
			else
				UIManager.setLookAndFeel(lnf_met);
			
			if (windows == null) return;
			// update all windows to new look and feel
			for (int i=0; i < windows.size(); i++) {
				Window window = (Window) windows.get(i);
				SwingUtilities.updateComponentTreeUI(window);
				window.pack();
				window.pack();
			}
		} catch (Exception e) {
			// java sets UI to the default one, like so:
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
	}

}
