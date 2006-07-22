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
 * Holds the parameters for the <code>changePassword</code> operation.
 */
public class ChangePasswordParams
    extends Params {

    private String newPassphrase;
    private String credentialName;
    
    public ChangePasswordParams() {
        super(MyProxy.CHANGE_PASSWORD);
    }
    
    public void setNewPassphrase(String newPassphrase) {
        checkPassphrase(newPassphrase);
        this.newPassphrase = newPassphrase;
    }
    
    public String getNewPassphrase() {
        return this.newPassphrase;
    }
    
    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }
    
    public String getCredentialName() {
        return this.credentialName;
    }
    
    protected String makeRequest(boolean includePassword) {
        StringBuffer buf = new StringBuffer();
        buf.append(super.makeRequest(includePassword));
        buf.append(NEW_PHRASE);
        if (includePassword) {
            buf.append(newPassphrase);
        } else {
            for (int i=0;i<this.newPassphrase.length();i++) {
                buf.append('*');
            }
        }
        buf.append(CRLF);
        add(buf, CRED_NAME, credentialName);
        return buf.toString();
    }
    
}
