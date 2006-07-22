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
package org.globus.util.deactivator;

import java.util.*;

public class Deactivator {

    private static Hashtable modules = new Hashtable();

    public static void deactivateAll() {
	DeactivationHandler handler = null;
	Enumeration e = modules.keys();
	while(e.hasMoreElements()) {
	    handler = (DeactivationHandler)e.nextElement();
	    handler.deactivate();
	}
	modules.clear();
    }
    
    public static void registerDeactivation(DeactivationHandler handler) {
	modules.put(handler, "");
    }
    
    public static void unregisterDeactivation(DeactivationHandler handler) {
	modules.remove(handler);
    }
    
}
