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

import java.io.OutputStream;
import java.io.IOException;

import org.globus.ftp.Buffer;

public interface DataChannelWriter {

    public void setDataStream(OutputStream out);

    // looks like DataSink interface

    public void write(Buffer buffer)
	throws IOException;

    /*
      Send the mode-specific signal indicating that the data
      sending is done, but the underlying resources (the socket)
      will not necessarily be closed.
      E.g. in stream mode, do nothing. In Eblock mode, send EOD|EOF.
     */
    public void endOfData()
        throws IOException;

    /*
      Close the underlying resources (the socket)
     */
    public void close()
	throws IOException;
    
}
