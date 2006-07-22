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
package org.globus.mds.gsi.common;

import java.io.InputStream;
import java.io.IOException;

import org.globus.gsi.gssapi.SSLUtil;
import org.globus.gsi.gssapi.net.GssInputStream;

import org.ietf.jgss.GSSContext;

public class SaslInputStream extends GssInputStream {
  
    private byte [] lenBuf;

    public SaslInputStream(InputStream in, GSSContext context) {
	super(in, context);
	this.lenBuf = new byte[4];
    }
  
    protected void readMsg()
	throws IOException {
	SSLUtil.readFully(this.in, this.lenBuf, 0, this.lenBuf.length);
        int len = GSIMechanism.networkByteOrderToInt(this.lenBuf, 0, 4);
	byte [] msg = new byte[len];
	SSLUtil.readFully(this.in, msg, 0, len);
	this.buff = msg;
	this.index = 0;
    }
    
}
