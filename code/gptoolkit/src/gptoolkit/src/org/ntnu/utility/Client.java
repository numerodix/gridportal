/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;

import java.lang.*;


/**<pre>
 * GridPortalToolkit client side application in support of GRIDportal 
 * (http://gridportal.dynalias.org/)
 *
 * This client offers the following basic functions:
 * + certificate request generation
 * + MyProxy registration
 *
 * The code is based mostly on the cog-jglobus library 
 * (http://www.cogkit.org/)
 * </pre>
 * @author Martin Matusiak
 */
public class Client {


	public static void main(String[] args) {
		System.out.println(Config.appName + " version " + 
		Config.appVersion + " starting");
		
		// set some global settings
		LibCommon.initProvider();
		LibCommon.setCoGProperties();
		
		// load toolkit.ini configuration file
		Config.loadPropertiesFromFile();
	
		GuiToolbox.setUI(GuiToolbox.lnfsys, null);
		
		Config conf = new Config();
		
		GuiMainWindow gui = new GuiMainWindow(conf.appName);
		gui.initGui();
		
//		System.out.println(System.getProperty("os.name"));
	}


}
