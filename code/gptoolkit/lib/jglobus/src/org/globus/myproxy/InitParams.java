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
 * Holds the parameters for the <code>put</code> operation.
 */
public class InitParams
    extends Params {
    
    private String retriever;
    private String renewer;
    private String credentialName;
    private String credentialDescription;

    public InitParams() {
        super(MyProxy.PUT_PROXY);
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getCredentialName() {
        return this.credentialName;
    }

    public void setCredentialDescription(String description) {
        this.credentialDescription = description;
    }

    public String getCredentialDescription() {
        return this.credentialDescription;
    }

    public void setRetriever(String retriever) {
        this.retriever = retriever;
    }

    public String getRetriever() {
        return this.retriever;
    }

    public void setRenewer(String renewer) {
        this.renewer = renewer;
    }

    public String getRenewer() {
        return this.renewer;
    }

    /**
     * If the passpharse is not set returns
     * an empty string.
     */
    public String getPassphrase() {
        String pwd = super.getPassphrase();
        return (pwd == null) ? "" : pwd;
    }

    protected String makeRequest(boolean includePassword) {
        StringBuffer buf = new StringBuffer();
        buf.append(super.makeRequest(includePassword));
        
        add(buf, RETRIEVER, retriever);
        add(buf, RENEWER, renewer);
        add(buf, CRED_NAME, credentialName);
        add(buf, CRED_DESC, credentialDescription);
        
        return buf.toString();
    }
}
