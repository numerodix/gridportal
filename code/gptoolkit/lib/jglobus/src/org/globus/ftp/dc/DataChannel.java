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

import java.io.IOException;
import org.globus.ftp.vanilla.BasicServerControlChannel;
import org.globus.ftp.DataSource;

import org.globus.ftp.DataSink;

public interface DataChannel {

    public void startTransfer(DataSink sink, 
			      BasicServerControlChannel localControlChannel,
			      TransferContext context)
	throws Exception;
    
    public void startTransfer(DataSource source,
			      BasicServerControlChannel localControlChannel,
			      TransferContext context)
	throws Exception;

    public void close() throws IOException;
} 
