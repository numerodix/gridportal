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

import org.apache.log4j.Level;

/**
   This class introduces a new level level called TRACE. TRACE has
   lower level than DEBUG.
 */
public class CoGLevel extends Level {

    static public final int TRACE_INT   = Level.DEBUG_INT - 1;

    private static String TRACE_STR  = "TRACE";

    public static final CoGLevel TRACE = new CoGLevel(TRACE_INT, TRACE_STR, 7);

    protected CoGLevel(int level, String strLevel, int syslogEquiv) {
	super(level, strLevel, syslogEquiv);
    }

    /**
       Convert the string passed as argument to a level. If the
       conversion fails, then this method returns {@link #TRACE}. 
    */
    public static Level toLevel(String sArg) {
	return (Level) toLevel(sArg, CoGLevel.TRACE);
    }

    public static Level toLevel(String sArg, Level defaultValue) {

	if(sArg == null) {
	    return defaultValue;
	}
	String stringVal = sArg.toUpperCase();
    
	if(stringVal.equals(TRACE_STR)) {
	    return CoGLevel.TRACE;
	}
      
	return Level.toLevel(sArg, (Level) defaultValue);    
    }

    public static Level toLevel(int i) 
	throws IllegalArgumentException {
	switch(i) {
	case TRACE_INT: return CoGLevel.TRACE;
	}
	return Level.toLevel(i);
    }
    
}
