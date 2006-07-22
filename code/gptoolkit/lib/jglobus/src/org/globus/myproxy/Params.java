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
package org.globus.myproxy;

/**
 * A generic class for representing basic parameters
 * needed for all MyProxy operations.
 */
public abstract class Params
    implements MyProxyConstants {

    private int command;

    protected String username;
    protected String passphrase;
    protected int lifetime;
    
    public Params(int command) {
        setCommand(command);
    }

    public Params(int command,
                  String username,
                  String passphrase) {
        setCommand(command);
        setUserName(username);
        setPassphrase(passphrase);
    }
    
    protected void setCommand(int command) {
        this.command = command;
    }

    public void setUserName(String username) {
        this.username = username;
    }
    
    public String getUserName() {
        return this.username;
    }

    public void setPassphrase(String passphrase) {
        checkPassphrase(passphrase);
        this.passphrase = passphrase;
    }

    public String getPassphrase() {
        return this.passphrase;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
        
    public int getLifetime() {
        return this.lifetime;
    }

    protected void checkPassphrase(String passphrase) {
        if (passphrase == null) {
            throw new IllegalArgumentException("Password is not specified");
        }
        if (passphrase.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " +
                                               MIN_PASSWORD_LENGTH + 
                                               " characters long");
        }
    }

    public String makeRequest() {
        return makeRequest(true);
    }

    /**
     * Serializes the parameters into a MyProxy request.
     * Subclasses should overwrite this function and
     * append the custom parameters to the output of
     * this function.
     */
    protected String makeRequest(boolean includePassword) {
        StringBuffer buf = new StringBuffer();
        buf.append(VERSION).append(CRLF);
        buf.append(COMMAND).append(String.valueOf(command)).append(CRLF);
        buf.append(USERNAME).append(this.username).append(CRLF);
        buf.append(PASSPHRASE);
        if (includePassword) {
            buf.append(getPassphrase());
        } else {
            for (int i=0;i<getPassphrase().length();i++) {
                buf.append('*');
            }
        }
        buf.append(CRLF);
        buf.append(LIFETIME).append(String.valueOf(lifetime)).append(CRLF);
        return buf.toString();
    }
    
    protected void add(StringBuffer buf, String prefix, String value) {
        if (value == null) {
            return;
        }
        buf.append(prefix).append(value).append(CRLF);
    }

    public String toString() {
        return makeRequest(false);
    }
    
}
