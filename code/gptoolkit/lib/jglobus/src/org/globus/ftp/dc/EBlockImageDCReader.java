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
package org.globus.ftp.dc;

import java.io.InputStream;
import java.io.IOException;
import java.io.DataInputStream;

import org.globus.ftp.Buffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EBlockImageDCReader
    extends EBlockAware
    implements DataChannelReader {

    boolean eodReceived = false;
    boolean willCloseReceived = false;
    
    private static Log logger =
        LogFactory.getLog(EBlockImageDCReader.class.getName());
    
    protected DataInputStream input;
    
    public void setDataStream(InputStream in) {
        input = new DataInputStream(in);
    }
    
    /** @return true if at least once received
	the "server will close the connection" signal
    */
    public boolean willCloseReceived() {
        return willCloseReceived;
    }
    
    public Buffer read() throws IOException {
        
        //EOD received in previous read
        if (eodReceived) {
            return null;
        }
        
        // WILL_CLOSE received in previous read
        if (willCloseReceived) {
            return null;
        }
        
        byte desc = input.readByte();
        long size = input.readLong();
        long offset = input.readLong();
        
        boolean eof = (desc & EOF) != 0;
        boolean eod = (desc & EOD) != 0;
        
        if (logger.isDebugEnabled()) {
            logger.debug(desc + " " + size + " " + offset);
        }

        // if closing flag not yet received,
        // check this buffer for closing flag
        willCloseReceived = (desc & WILL_CLOSE) != 0;
        if (willCloseReceived) {
            logger.debug("Received the CLOSE flag");
        }
        
        if (eod) {
            this.eodReceived = true;
            context.eodTransferred();
            if (logger.isDebugEnabled()) {
                logger.debug(
                         "Received EOD. Still expecting: "
                         + ((context.getEodsTotal() == context.UNDEFINED)
                            ? "?"
                            : Integer.toString(
                                               context.eodsTotal - context.eodsTransferred)));
            }
        }
        
        if (eof) {
            context.setEodsTotal((int) offset);
            if (logger.isDebugEnabled()) {
                logger.debug("Received EODC. Expecting total EODs: "
                             + context.getEodsTotal());
            }
            return null;
            
        } else {
            byte[] bt = new byte[(int) size];
            input.readFully(bt);
            return new Buffer(bt, (int) size, offset);
        }
    }
    
    public void close() throws IOException {
        // we want to reuse the socket
        input.close();
    }
}
