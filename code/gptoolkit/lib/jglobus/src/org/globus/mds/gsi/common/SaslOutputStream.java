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

import java.io.OutputStream;
import java.io.IOException;

import org.globus.gsi.gssapi.net.GssOutputStream;

import org.ietf.jgss.GSSContext;

public class SaslOutputStream extends GssOutputStream {
    
    private byte [] lenBuf;

    public SaslOutputStream(OutputStream out, GSSContext context) {
	super(out, context);
	this.lenBuf = new byte[4];
    }

    public void flush() 
	throws IOException {
	if (index == 0) return;
	byte [] token = wrap();
	GSIMechanism.intToNetworkByteOrder(token.length, lenBuf, 0, 4);
	this.out.write(lenBuf);
	this.out.write(token);
	this.out.flush();
	index = 0;
    }
    
}
