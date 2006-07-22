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
package org.globus.common;

/** This class allows to query the current version of the released software.
 * The release number is assambled by MAJOR.MINOR.PATCH
 */
public class Version {

    /** The major release number */
    public static final int MAJOR = 1;

    /** The minor release number */
    public static final int MINOR = 2;

    /** The patchlevel of the current release */
    public static final int PATCH = 0;

    /** Retruns the current version as string in the form MAJOR.MINOR.PATCH
     */
    public static String getVersion() {
	return getMajor() + "." + getMinor() + "." + getPatch();
    }

    /** Returns the major release number
     * 
     * @return the major release
     */
    public static int getMajor() {
	return MAJOR;
    }
    
    /** Returns the minor release number
     * 
     * @return the minor release number
     */
    public static int getMinor() {
	return MINOR;
    }
    
    /** Returns the patch level
     * 
     * @return the patch level
     */
    public static int getPatch() {
	return PATCH;
    }
    
    /** Returns the version for the Java CoG Kit as a readble string.
     * 
     * @param args 
     */
    public static void main(String [] args) {
	System.out.println("Java CoG version: " + getVersion());
    }

}
