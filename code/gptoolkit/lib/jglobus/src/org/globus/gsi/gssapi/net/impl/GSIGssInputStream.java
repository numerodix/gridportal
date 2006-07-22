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

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssInputStream;
import org.globus.gsi.gssapi.SSLUtil;

import org.ietf.jgss.GSSContext;

public class GSIGssInputStream extends GssInputStream {
  
    // 32Mb 
    private static final int MAX_LEN = 32 * 1024 * 1024;

    protected byte [] header;
    protected int mode;

    public GSIGssInputStream(InputStream in, GSSContext context) {
        super(in, context);
        this.header = new byte[5];
        this.mode = -1;
    }
  
    protected void readMsg()
        throws IOException {
        byte [] token = readToken();
        if (token == null) {
            this.buff = null;
        } else {
            this.buff = unwrap(token);
        }
        this.index = 0;
    }

    public int getWrapMode() {
        return this.mode;
    }

    public byte[] readHandshakeToken()
        throws IOException {
        byte [] token = readToken();
        if (token == null) {
            throw new EOFException();
        }
        return token;
    }

    protected byte[] readToken()
        throws IOException {
        byte[] buf = null;
        if (SSLUtil.read(this.in, this.header, 0, this.header.length-1) < 0) {
            return null;
        }
        if (SSLUtil.isSSLv3Packet(this.header)) {
            this.mode = GssSocket.SSL_MODE;
            // read the second byte of packet length field
            if (SSLUtil.read(this.in, this.header, 4, 1) < 0) {
                return null;
            }
            int len = SSLUtil.toShort(this.header[3], this.header[4]);
            buf = new byte[this.header.length + len];
            System.arraycopy(this.header, 0, buf, 0, this.header.length);
            if (SSLUtil.read(this.in, buf, this.header.length, len) < 0) {
                return null;
            }
        } else if (SSLUtil.isSSLv2HelloPacket(this.header)) {
            this.mode = GssSocket.SSL_MODE;
            // SSLv2 - assume 2-byte header 
            // read extra 2 bytes so subtract it from total len
            int len = (((header[0] & 0x7f) << 8) | header[1]) - 2;
            buf = new byte[this.header.length-1 + len];
            System.arraycopy(this.header, 0, buf, 0, this.header.length-1);
            if (SSLUtil.read(this.in, buf, this.header.length-1, len) < 0) {
                return null;
            }
        } else {
            this.mode = GssSocket.GSI_MODE;
            int len = SSLUtil.toInt(this.header, 0);
            if (len > MAX_LEN) {
                throw new IOException("Token length " + len + " > " + MAX_LEN);
            }
            buf = new byte[len];
            if (SSLUtil.read(this.in, buf, 0, buf.length) < 0) {
                return null;
            }
        }
        return buf;
    }
    
}
