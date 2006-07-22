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
   Represents data channel authentication mode.
   Use static variables SELF or NONE.
 **/
public class DataChannelAuthentication {

    public static final DataChannelAuthentication NONE =
	new DataChannelAuthentication("N");

    public static final DataChannelAuthentication SELF = 
	new DataChannelAuthentication("A");

    protected String argument;

    protected DataChannelAuthentication() {
    }

    protected DataChannelAuthentication(String argument) {
	this.argument = argument;
    }

    protected void setArgument(String argument) {
	this.argument = argument;
    }

    public String toFtpCmdArgument() {
	return argument;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof DataChannelAuthentication) {
            DataChannelAuthentication otherObj = 
                (DataChannelAuthentication)other;
            return (this.argument.equals(otherObj.argument));
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return (this.argument == null) ? 1 : this.argument.hashCode();
    }
    
}
