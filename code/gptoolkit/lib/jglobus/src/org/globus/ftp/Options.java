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
package org.globus.ftp;

/**
   Represents FTP command options, as defined in RFC 2389.
 */
public abstract class Options {

    protected String command;

    /**
       @param cmd command whose options are represent by this object
     */
    public Options(String cmd) {
	command = cmd;
    }

    public String toFtpCmdArgument() {
	return command + " " + getArgument();
    }
    
    /**
       Subclasses should implement this method. It should
       return the right side of the options line,
       in the format of OPTS command. It should not include the
       command name.
     */
    public abstract String getArgument();
    
}
