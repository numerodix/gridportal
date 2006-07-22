/** 
 * GridProxyProperties.java
 *
 * Manages necessary credential properties
 *
 * $Id: GridProxyProperties.java,v 1.8 2005/04/18 15:36:46 gawor Exp $
 */

/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.globus.tools.ui.proxy;

import java.io.*;
import java.util.*;
import org.globus.security.*;

public class GridProxyProperties {

    public static final String sep   = System.getProperty("file.separator");
    public static final String home  = System.getProperty("user.home") + sep;


    private static int hours = 24;
    private static int bits = 512;
    private static boolean limited = false;
    private static final String propsFile = "proxy.properties"; 
    private static final String gridPropsFile = home + propsFile;
    
    private static String userCertFile = home + ".globus" + sep + "usercert.pem";
    private static String userKeyFile  = home + ".globus" + sep + "userkey.pem";
    private static String  CACertFile  = home + ".globus" + sep + "certificates" + sep + "42864e48.0";
    private static String  proxyFile   = sep + "tmp" + sep + "myproxy";


    /* Myproxy properties */
    final static int MYPROXY_PORT = 7512;
    private static int credLifetime = 168;
    private static int portalLifetime = 2;
    private static String myproxyServer = "localhost";
    private static int myproxyPort = MYPROXY_PORT;

    public GridProxyProperties() {
	/* load Properties */
	if( hasProperties() )
	    loadProperties();
    } 

    public void setHours(int hours) {
	this.hours = hours;
    }

    public int getHours() {
	return hours;
    }

    public void setBits(int bits) {
	this.bits = bits;
    }

    public int getBits() {
	return bits;
    }

    public void setLimited(boolean limited) {
	this.limited = limited;
    }

    public boolean getLimited() {
	return limited;
    }

    public void setProxyFile(String proxyFile) {
	this.proxyFile = proxyFile;
    }

    public String getProxyFile() {
	return proxyFile;
    }

    public void setUserCertFile(String userCertFile) {
	this.userCertFile = userCertFile;
    }

    public String getUserCertFile() {
	return userCertFile;
    }

    public void setUserKeyFile(String userKeyFile) {
	this.userKeyFile = userKeyFile;
    }

    public String getUserKeyFile() {
	return userKeyFile;
    }

    public void setCACertFile(String CACertFile) {
	this.CACertFile = CACertFile;
    }

    public String getCACertFile() {
	return CACertFile;
    }

    public void setPortalLifetime(int portalLifetime) {
	this.portalLifetime = portalLifetime;
    }

    public int getPortalLifetime() {
	return portalLifetime;
    }

    public void setCredLifetime(int credLifetime) {
	this.credLifetime = credLifetime;
    }

    public int getCredLifetime() {
	return credLifetime;
    }
    
    public void setMyproxyServer(String myproxyServer) {
	this.myproxyServer = myproxyServer;
    }

    public String getMyproxyServer() {
	return myproxyServer;
    }

    public void setMyproxyPort(int myproxyPort) {
	this.myproxyPort = myproxyPort;
    }

    public int getMyproxyPort() {
	return myproxyPort;
    }

    public boolean hasProperties() {
	File pfile = new File(gridPropsFile);
        if (pfile.exists())
            return true;
        return false;
    }                     

    public void loadProperties() {
	String tmpHours = "";
	String tmpBits = "";
	String tmpLimited = "";
	String tmpUserCert = "";
	String tmpUserKey = "";
	String tmpProxy = "";
	String tmpCaCert = "";
	String tmpCredL = "";
	String tmpPortalL = "";
	String tmpProxyHost = "";
	String tmpProxyPort = "";

	Properties props = new Properties();
	
	try {
	    FileInputStream in = new FileInputStream(gridPropsFile);
	    props.load(in);
	    in.close();
	} catch (FileNotFoundException fnfe) {
	    System.err.println("loadGridProxyProperties: FileNotFoundException"); 
	} catch (IOException ioe) {
	   System.err.println("loadGridProxyProperties: IOException error"); 
	}
	
	tmpHours = props.getProperty("hours");
	if (!tmpHours.equals("")) 
	    hours = new Integer(tmpHours).intValue();
	tmpBits = props.getProperty("bits");
	if (!tmpBits.equals("")) 
	    bits = new Integer(tmpBits).intValue();
	tmpLimited = props.getProperty("limited");
	if (tmpLimited.equals("true") || tmpLimited.equals("TRUE")) {
	    limited = true;
	} else {
	    limited = false;
	}
	tmpUserCert = props.getProperty("usercert");
	if (!tmpUserCert.equals("")) 
	    userCertFile = tmpUserCert;
	tmpUserKey = props.getProperty("userkey");
	if (!tmpUserKey.equals("")) 
	    userKeyFile = tmpUserKey;
	tmpProxy = props.getProperty("proxy");
	if (!tmpProxy.equals("")) 
	    proxyFile = tmpProxy;
	tmpCaCert = props.getProperty("cacert");
	if (!tmpCaCert.equals("")) 
	    CACertFile = tmpCaCert;
	tmpCredL = props.getProperty("cred_lifetime");
	if (!tmpCredL.equals(""))
	    credLifetime = new Integer(tmpCredL).intValue();
	tmpPortalL = props.getProperty("portal_lifetime");
	if (!tmpPortalL.equals(""))
	    portalLifetime = new Integer(tmpPortalL).intValue();
	tmpProxyHost = props.getProperty("myproxy_server");
	if (!tmpProxyHost.equals(""))
	    myproxyServer = tmpProxyHost;
	tmpProxyPort = props.getProperty("myproxy_port");
	if (!tmpProxyPort.equals(""))
	    myproxyPort = new Integer(tmpProxyPort).intValue();
    }

    public boolean saveProperties() {

	Properties props = new Properties();
	props.setProperty("hours", new Integer(hours).toString());
	props.setProperty("bits", new Integer(bits).toString());
	props.setProperty("limited", new Boolean(limited).toString());
	props.setProperty("usercert", userCertFile);
	props.setProperty("userkey", userKeyFile);
	props.setProperty("proxy", proxyFile);
	props.setProperty("cacert", CACertFile);
	props.setProperty("cred_lifetime", new Integer(credLifetime).toString());
	props.setProperty("portal_lifetime", new Integer(portalLifetime).toString());
	props.setProperty("myproxy_server", myproxyServer);
	props.setProperty("myproxy_port", new Integer(myproxyPort).toString());

	String gridPropsFile = System.getProperty("user.home") +
	    System.getProperty("file.separator") + propsFile;
	
	try {
	    FileOutputStream out = new FileOutputStream(gridPropsFile);
	    props.store(out, "GridProxyInit properties");
	    out.close();
	} catch (FileNotFoundException fnfe) {
	    System.err.println("saveGridProxyProperties: FileNotFoundException");
	    return false;
	} catch (IOException ioe) {
	   System.err.println("saveGridProxyProperties: IOException");
	   return false;
	}
	return true;
    }

}
