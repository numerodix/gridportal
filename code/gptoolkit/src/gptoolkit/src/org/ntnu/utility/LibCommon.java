/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.awt.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

import java.io.File;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.*;


/**
 * A class to store common functions needed in the application.
 *
 * @author Martin Matusiak
 */
class LibCommon {


	public String readFileFromJAR(String filepath) {
		
		String out = "";
		
		try {
		
			// setup input buffer
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream instream = cl.getResourceAsStream(filepath);
			BufferedReader filereader =
				new BufferedReader(new InputStreamReader(instream));
			
			// read lines
			String line = filereader.readLine();
			while (line != null) {
				out += "\n" + line;
				line = filereader.readLine();
			}
			
			filereader.close();
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return out;
		
	}
	
	
	public ImageIcon readImageFromJAR(String filepath) {
		
		ImageIcon img = null;
		
		try {
		
			URL url = this.getClass().getResource(filepath);
			img = new ImageIcon(url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return img;
		
	}
	
	
	public static boolean fileExists(String path) {
		return new File(path).exists();
	}


	public static boolean fileEmpty(String path) {
		return (new File(path).length() == 0L);
	}

	
/**
 * Some classes depend on the BouncyCastle provider to function. The
 * provider has to be registered with the java Security class before use.
 * Call this method early in the init of the application to ensure
 * it is invoked before the code which depends on the provider.
 */
	public static void initProvider() {
		// Add a RSA security provider
		try {
			Security.addProvider(new BouncyCastleProvider());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
/**
 * Set the environment variable to indicate where the CA cert is found
 * on the filesystem. LibMyProxy relies on this.
 */
	public static void setCoGProperties() {
		System.setProperty("X509_CERT_DIR", getCurrentDir());
	}
	
	
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}
	
	
	public static void printSystemProperties() {
		java.util.Enumeration e = System.getProperties().propertyNames();
		while( e.hasMoreElements() )
		{
			String prop = (String)e.nextElement();
			String out = prop;
			out += " = ";
			out += System.getProperty(prop);
			out += "\n";
			System.out.println(out);
		}
	}
	
}