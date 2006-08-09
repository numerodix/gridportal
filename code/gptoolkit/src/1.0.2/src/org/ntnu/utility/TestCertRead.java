/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


/**
 * Helper class to read certificates [requests] and keys.
 *
 * @author Martin Matusiak
 */
class TestCertRead {

	public static void main(String[] args) throws Exception {

		init();
	
		try {
			print("\nAttempting to read certificate request in:\n" +
				Config.userCertReqFile);
			Config.manager.getCertReq();
			print(" => OK");
		} catch (Exception e) {
			printException(e);
		}

		try {
			print("\nAttempting to read certificate in:\n" + 
				Config.userCertFile);
			Config.manager.getCert();
			print(" => OK");
		} catch (Exception e) {
			printException(e);
		}

		try {
			print("\nAttempting to read private key in:\n" + 
				Config.userKeyFile);
			Config.manager.getKey();
			print(" => OK");
		} catch (Exception e) {
			printException(e);
		}

	
	}
	
	public static void print(String s) {
		System.out.println(s);
	}

	public static void printException(Exception e) {
		e.printStackTrace();
	}


	public static void init() {
		// set some global settings
		LibCommon.initProvider();
		LibCommon.setCoGProperties();
		
		// load toolkit.ini configuration file
		Config.loadPropertiesFromFile();
	}
}
 
