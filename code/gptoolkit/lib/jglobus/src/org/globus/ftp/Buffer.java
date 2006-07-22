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

/**
   Represents a chunk of data cut out of a larger data volume.
   Buffer is characterized by offset at which it belongs to the
   larger data volume, and length.
   The internal data array always starts at 0 and ends at (length -1).
   Its indexing has nothing to do with offset.
 **/
public class Buffer {

    protected byte[] buf;
    protected int length;
    protected long offset;

    /**
     * @param buf the data buffer (always starts at 0)
     * @param length length of the data in the buffer
     */
    public Buffer(byte [] buf, int length) {
	this(buf, length, -1);
    }

    /**
     * @param buf the data buffer (always starts at 0)
     * @param length length of the data in the buffer
     * @param offset offset of the data the buffer was read from.
     *              
     */
    public Buffer(byte [] buf, int length, long offset) {
	this.buf = buf;
	this.length = length;
	this.offset = offset;
    }

    public byte[] getBuffer() {
	return buf;
    }

    public int getLength() {
	return length;
    }

    /**
     * Returns offset of the data the buffer was read from.
     * Value -1 indicates that offset is not supported.
     * For instance, this will happen if the buffer represents
     * a chunk of data read off the data channel in the stream
     * mode. 
     *
     */
    public long getOffset() {
	return offset;
    }

}
