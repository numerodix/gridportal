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
   Represents an entity capable of receiving incoming markers.
   Implement it to provide your own methods of analyzing markers.
   @see GridFTPClient#transfer
   @see GridFTPClient#extendedTransfer
 **/
public interface MarkerListener{

    /**
       When writing your implementation, assume this method
       being called whenever a marker arrives.
     **/
    public void markerArrived(Marker m);

}
