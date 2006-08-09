/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.awt.Color;
import java.io.*;
import java.util.Properties;


/**
 * This class serves as a global configuration module for the whole
 * application. Variables are public and static so they can be reused
 * by all classes. The class also contains methods for handling the
 * .ini configuration file on the filesystem.
 *
 * @author Martin Matusiak
 */
class Config {

	public static final String appName = "GridPortalToolkit";
	public static final String appVersion = "1.0.2";
	
	public static final Color errorColor = Color.PINK;
	public static final Color warningColor = Color.YELLOW;
	public static final Color successColor = Color.GREEN;
	
	// immutable constants set up by default
	public static final String userCertFileName = "usercert.pem";
	public static final String userCertReqFileName = "usercert_request.pem";
	public static final String userKeyFileName = "userkey.pem";
	public static final String globusDir = System.getProperty("user.home") +
		System.getProperty("file.separator") + ".globus" +
		System.getProperty("file.separator");
	
	// extra constants for easy access
	public static final String userCertFile = globusDir + userCertFileName;
	public static final String userCertReqFile = globusDir + userCertReqFileName;
	public static final String userKeyFile = globusDir + userKeyFileName;
		
	public static String myProxyHost = "norgrid.ntnu.no";
	public static String myProxyPort = "7512";
	
	public static String requestMsgSubject =
		"Request for certificate signature";
	public static String requestMsgAddress =
		"ca@nbi.dk";
	
	public static NorduGridManager manager = new NorduGridManager();
	
	
	private static Properties properties = new Properties();
	private static final String propsfilename = "toolkit.ini";
	
	
/**
 * Read configuration from .ini file.
 */
	public static boolean loadPropertiesFromFile() {
	
		try {
		
			// if file doesn't exist, create the file and write the default config
			if (!LibCommon.fileExists(LibCommon.getCurrentDir() +
				System.getProperty("file.separator") + propsfilename)) {
				savePropertiesToFile();
				return false;
			}
	
			File propfile = new File(LibCommon.getCurrentDir() + 
				System.getProperty("file.separator") + propsfilename);
			InputStream instream = new FileInputStream(propfile);

			properties.load(instream);
			
		} catch (Exception e) {
			System.out.println("Could not read configuration from " + propsfilename);
			return false;
		}
		
		loadProperties();
		return true;
	}
	
	
/**
 * Write configuration to .ini file.
 */	
	public static boolean savePropertiesToFile() {
		String fileheader = "Configuration file for " +
			Config.appName + " " + Config.appVersion +
		"\n#\n#If you make changes in this file, please make sure\n#you know what you are doing. If misconfigured, this\n#file may cause the program to fail.\n#";
		
		saveProperties();
		
		try {
	
			File propfile = new File(LibCommon.getCurrentDir() + 
				System.getProperty("file.separator") + propsfilename);
			OutputStream instream = new FileOutputStream(propfile);

			properties.store(instream, fileheader);
			
		} catch (Exception e) {
			System.out.println("Could not save configuration to " + propsfilename);
			return false;
		}
		
		return true;
	}
	
	
	private static void loadProperties() {
		myProxyHost = properties.getProperty("myProxyHost");
		myProxyPort = properties.getProperty("myProxyPort");
		requestMsgAddress = properties.getProperty("requestMsgAddress");
	}
	
	
	private static void saveProperties() {
		properties.setProperty("myProxyHost", myProxyHost);
		properties.setProperty("myProxyPort", myProxyPort);
		properties.setProperty("requestMsgAddress", requestMsgAddress);
	}

}