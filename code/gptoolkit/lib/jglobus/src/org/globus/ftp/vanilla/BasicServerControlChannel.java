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
   Basic subset of server side control channel functionality.
   Using this class, local server can send replies but not receive
   commands.
 **/
public interface BasicServerControlChannel{

    /**
       write reply to the control channel
     **/
    public void write(Reply reply);
    /**
       @return number of replies sent so far
     **/
    public int getReplyCount();
    /**
       set reply count to 0. If this function is used consequently
       at the beginning of each transfer,
       then reply count will always indicate number of messages
       of last transfer.
     **/
    public void resetReplyCount();
} //BasicServerControlChannel



