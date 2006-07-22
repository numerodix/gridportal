/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;


/**
 * This class is redundant and exists as a reference for possible
 * future use.
 *
 * @author Martin Matusiak
 */
class GuiTreeTableModelCertificate extends AbstractTreeTableModel {

	private String[] columnNames = {"Attribute", "Value"};
	
	

	public GuiTreeTableModelCertificate(Object root) {
			super(root);
	}
	

	public Object getChild(Object parent, int index) {
		return null;
	}
	
	
	public int getChildCount(Object parent) {
		return 0;
	}
	
	
	public int getColumnCount() {
		return 0;
	}
	
	
	public String getColumnName(int column) {
		return null;
	}
	
	
	public Object getValueAt(Object node, int column) {
		return null;
	}

}