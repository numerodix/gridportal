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
package org.globus.util.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This is a simple log4j appender that ignores all log messages. 
 */
public class NullAppender extends AppenderSkeleton {
    
    private static NullAppender appender;

    public static synchronized NullAppender getInstance() {
	if (appender == null) {
	    appender = new NullAppender();
	}
	return appender;
    }

    public void close() {
    }

    public boolean requiresLayout() {
	return false;
    }

    public void append(LoggingEvent event) {
    }

}
