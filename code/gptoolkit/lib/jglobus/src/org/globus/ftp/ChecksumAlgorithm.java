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
 * Represents the algorithm used for checksum operation.
 **/
public class ChecksumAlgorithm {

    public static final ChecksumAlgorithm MD5 =
        new ChecksumAlgorithm("MD5");
    
    protected String argument;
    
    public ChecksumAlgorithm(String name) {
        this.argument = name;
    }
    
    public String toFtpCmdArgument() {
        return argument;
    }
    
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ChecksumAlgorithm) {
            ChecksumAlgorithm otherObj = 
                (ChecksumAlgorithm)other;
            return (this.argument.equals(otherObj.argument));
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return (this.argument == null) ? 1 : this.argument.hashCode();
    }
    
}
