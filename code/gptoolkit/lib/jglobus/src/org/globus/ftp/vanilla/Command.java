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
package org.globus.ftp.vanilla;

/**
 * Represents an FTP Control Channel Command
 */
public class Command {

    public static final Command FEAT = new Command("FEAT");
    public static final Command ABOR = new Command("ABOR");
    public static final Command CDUP = new Command("CDUP");
    public static final Command PWD  = new Command("PWD");
    public static final Command QUIT = new Command("QUIT");
    public static final Command PASV = new Command("PASV");
    public static final Command SPAS  = new Command("SPAS");
    public static final Command EPSV = new Command("EPSV");


    ///////////////////////////////////////
    // attributes

    private String name;
    private String parameters;

    ///////////////////////////////////////
    // operations


    /**
     * @param name the command name, eg. "PUT"
     * @param parameters the command parameters; in other words everything that
     * is contained between the space after the command name and the trailing 
     * Telnet EOL, excluding both the mentioned space and EOL. For instance,
     * in command "STOR /tmp/file.txt\r\n", the parameters would be:
     * "/tmp/file.txt"
     * and trailing EOL.
     */
    public  Command(String name, String parameters) 
	throws IllegalArgumentException{
	initialize(name, parameters);
    } // end Command        
    
    public  Command(String name) 
	throws IllegalArgumentException{
	initialize(name, null);
    }
    
    private void initialize(String name, String parameters) 
	throws IllegalArgumentException {
        if (name == null) {
	    throw new IllegalArgumentException("null name");
	}
	if (parameters != null && parameters.endsWith(FTPControlChannel.CRLF)) {
	    throw new IllegalArgumentException("parameters end with EOL");
	}
        this.name = name;
	this.parameters = parameters;
    } // end initialize

    /**
     * @return a String representation of this object, that is 
     * <name> <sp> <parameters> <CRLF>
     * </p>
     */
    public static String toString(Command command) {       
	return command.toString();
    }

    public String toString() {
	if (parameters == null) {
	    return name + FTPControlChannel.CRLF;
	} else {
	    return name + " " + parameters + FTPControlChannel.CRLF;
	}
    }

} // end Command



