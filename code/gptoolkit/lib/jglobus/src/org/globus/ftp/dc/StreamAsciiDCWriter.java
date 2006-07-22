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

import org.globus.ftp.Buffer;

public class StreamAsciiDCWriter extends StreamImageDCWriter {

    protected AsciiTranslator translator;

    public StreamAsciiDCWriter() {
	// check for \r\n and \n separators 
	// output tokens with \r\n line separators
	translator = new AsciiTranslator(true, true, AsciiTranslator.CRLF);
    }

    public void write(Buffer buffer) 
	throws IOException {
	if (buffer == null) return;
	super.write( translator.translate(buffer) );
    }
    
}
