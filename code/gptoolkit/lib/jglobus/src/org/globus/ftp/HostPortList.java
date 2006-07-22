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

import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.Vector;

/**
 * Utility class for parsing
 * and converting host-port information from SPAS 
 * and SPOR FTP commands. Represents a list of host-port pairs.
 */
public class HostPortList {

    /*
      This class is internally represented as String or as vector.
      In case of third party transfer, there is no need to convert the String
      to vector, since we receive a string from server A and send a similar
      string to server B. However in client-server transfer, we do need internal
      vector representation.
      Internally, if the constructor gets string as parameter, the default 
      representation remains string. Whenever you call a modifier method like
      add(), the string becomes out of date and then the vector is used.
      The string can become up to date again if updateString() called.
      The vector is not usable until updateVector() is first called,
      and then it always remains up to date.
    */
    
    //internal string, in form of parameters to SPOR command
    private String sporCommandParam;
    private Vector vector = null;

    /**
     * Parses host-port from the reply to SPAS command.
     *
     * @param spasReplyMsg reply message for the SPAS command
     */
    public HostPortList(String spasReplyMsg) {
        try {
            parseFormat(spasReplyMsg, false);
        } catch (IOException e) {
            // this should never happen
        }
    }

    /**
     * Creates an empty list
     **/
    public HostPortList() {
    }

    /**
     * Adds an element to the list
     **/
    public void add(HostPort hp) {
        if (this.vector == null) {
            this.vector = new Vector();
        }
        this.vector.add(hp);
        this.sporCommandParam = null;
    }
    
    /**
     * @return number of elements in the list
     **/
    public int size() {
        return (this.vector == null) ? 0 : this.vector.size();
    }

    /**
     * @return element of the specified index
     **/
    public HostPort get(int index) {
        return (this.vector == null) ? 
            null : (HostPort)this.vector.elementAt(index);
    }

    /**
     * Returns the host-port infromation in the
     * format used by SPOR command. 
     *
     * @return host-port information in SPOR command parameter
     *         representation.
     */
    public String toFtpCmdArgument() {
        if (this.sporCommandParam == null && this.vector != null) {
            StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < this.vector.size(); i ++) {
                HostPort hp = (HostPort)this.vector.get(i);
                if (i != 0) {
                    cmd.append(' ');
                }
                cmd.append(hp.toFtpCmdArgument());
            }
            this.sporCommandParam = cmd.toString();
        }
        return this.sporCommandParam;
    }

    private void parseFormat(String msg, boolean ipv6) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(msg));
        StringBuffer command = null;
        String line = null;

        line = reader.readLine();
        while( (line = reader.readLine()) != null) {
            if (!line.startsWith(" ")) {
                if (line.startsWith("229")) {
                    break;
                } else {
                    throw new IllegalArgumentException(
                         "Not a proper reply message " +
                         "->" + line  + "<-");
                }    
            }
            line = line.trim();
            if (line.startsWith("229")) {
                break;
            }
            if (this.vector == null) {
                this.vector = new Vector();
            }
            if (ipv6) {
                this.vector.add(new HostPort6(line));
            } else {
                this.vector.add(new HostPort(line));
            }
            if (command == null) {
                command = new StringBuffer();
            } else {
                command.append(' ');
            }
            command.append(line);
        }
        if (this.vector == null) {
            throw new IllegalArgumentException(
                         "Not a proper reply message " +
                         "->" + line  + "<-");
        }
        this.sporCommandParam = command.toString();
    }

    public static HostPortList parseIPv6Format(String message) {
        HostPortList list = new HostPortList();
        try {
            list.parseFormat(message, true);
        } catch (IOException e) {
            // this should never happen
        }
        return list;
    }
    
    public static HostPortList parseIPv4Format(String message) {
        HostPortList list = new HostPortList();
        try {
            list.parseFormat(message, false);
        } catch (IOException e) {
            // this should never happen
        }
        return list;
    }
    
}
