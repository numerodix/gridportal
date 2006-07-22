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
package org.globus.gsi.gssapi.net.impl;

import java.io.OutputStream;
import java.io.IOException;

import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssOutputStream;
import org.globus.gsi.gssapi.SSLUtil;

import org.ietf.jgss.GSSContext;

public class GSIGssOutputStream extends GssOutputStream {

    protected byte [] header;
    protected int mode;
    
    public GSIGssOutputStream(OutputStream out, GSSContext context) {
	this(out, context, GssSocket.SSL_MODE);
    }

    public GSIGssOutputStream(OutputStream out, GSSContext context, int mode) {
	super(out, context);
	this.header = new byte[4];
	setWrapMode(mode);
    }

    public void flush() 
	throws IOException {
	if (this.index == 0) return;
	writeToken(wrap());
	this.index = 0;
    }

    public void setWrapMode(int mode) {
	this.mode = mode;
    }

    public int getWrapMode() {
	return this.mode;
    }

    public void writeToken(byte[] token)
	throws IOException {
	if (this.mode == GssSocket.GSI_MODE) {
	    SSLUtil.writeInt(token.length, this.header, 0);
	    this.out.write(this.header);
	}
	this.out.write(token);
	this.out.flush();
    }
    
}
