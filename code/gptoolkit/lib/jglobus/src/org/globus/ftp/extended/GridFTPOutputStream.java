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
package org.globus.ftp.extended;

import java.io.OutputStream;
import java.io.IOException;

import org.globus.gsi.gssapi.net.GssOutputStream;
import org.globus.util.Base64;

import org.ietf.jgss.GSSContext;

public class GridFTPOutputStream extends GssOutputStream {
    
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte[] ADAT = "ADAT ".getBytes();
    private static final byte[] MIC  = "MIC ".getBytes();
    private static final byte[] ENC  = "ENC ".getBytes();
    
    public GridFTPOutputStream(OutputStream out, GSSContext context) {
	super(out, context);
    }
    
    public void flush() 
	throws IOException {
	if (this.index == 0) return;
	if (this.context.getConfState()) {
	    writeToken(ENC, wrap());
	} else {
	    writeToken(MIC, wrap());
	}
	this.index = 0;
    }
    
    public void writeHandshakeToken(byte [] token) 
	throws IOException {
	writeToken(ADAT, token);
    }

    private void writeToken(byte[] header, byte[] token)
        throws IOException {
        this.out.write(header);
        this.out.write(Base64.encode(token));
        this.out.write(CRLF);
        this.out.flush();
    }

}
