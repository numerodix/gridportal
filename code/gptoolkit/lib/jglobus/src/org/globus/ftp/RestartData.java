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
   Represents a container for restart data
   capable for representing it in the format of FTP REST command argument.
 **/
public interface RestartData {
    /**
       @return the restart data in the format of REST command argument.
       For instance (in GridFTP) "4-50,62-75"
     **/
    public String toFtpCmdArgument();
}
