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
package org.globus.gsi;

import java.util.StringTokenizer;

/**
 * Provides some old convenience functions. 
 *
 * @deprecated These functions will only work with Globus legacy proxies.
 */
public class OldCertUtil {

    /**
     * Converts certificate dn into globus dn (with "/")
     * and returns the base dn without CN=proxy or
     * CN=limited proxy
     */
    public static String toGlobusDN(String certDN, boolean skipProxy) {
	StringTokenizer tokens = new StringTokenizer(certDN, ",");
	StringBuffer buf = new StringBuffer();
	String token;
	
	while(tokens.hasMoreTokens()) {
	    token = tokens.nextToken().trim();
	    if (skipProxy && 
		(token.equalsIgnoreCase("CN=proxy") ||
		 token.equalsIgnoreCase("CN=limited proxy"))) {
		continue;
	    }
	    buf.insert(0, token);
	    buf.insert(0, "/");
	}
	
	return buf.toString();
    }

    public static String getBase(String dn, boolean reverse) {

	int start;
	int i;
	char ch;

	if (reverse) {
	
	    start = dn.length()-1;

	    for (;;) {

		for (i=start;i>=0;i--) {
		    ch = dn.charAt(i);
		    if (ch > ' ' && ch != ',') {
			break;
		    }
		}

		if (dn.regionMatches(true, i-7, "cn=proxy", 0, 8)) {
		    start = i-8;
		} else if (dn.regionMatches(true, i-15, "cn=limited proxy", 0, 16)) {
		    start = i-16;
		} else {
		    return dn.substring(0, i+1);
		}
	    }

	} else {
	    
	    start = 0;
	    int len = dn.length();
	    
	    for (;;) {
		
		for (i=start;i<len;i++) {
		    ch = dn.charAt(i);
		    if (ch > ' ' && ch != ',') {
			break;
		    }
		}
		
		if (dn.regionMatches(true, i, "cn=proxy", 0, 8)) {
		    start = i+8;
		} else if (dn.regionMatches(true, i, "cn=limited proxy", 0, 16)) {
		    start = i+16;
		} else {
		    return dn.substring(i);
		}
	    }
	    
	}
    }

    // Use only if necessary
    public static int getProxyType(String dn, boolean reverse) {
	String proxyName = getProxyName(dn, reverse);

	if (proxyName == null) {
	    return -1;
	}
	
	if (proxyName.equalsIgnoreCase("proxy")) {
	    return GSIConstants.GSI_2_PROXY;
	} else if (proxyName.equalsIgnoreCase("limited proxy")) {
	    return GSIConstants.GSI_2_LIMITED_PROXY;
	} else {
	    return -1;
	}
    }

    // Use only if necessary
    private static String getProxyName(String dn, boolean reverse) {
	int start = -1;
	int end = -1;
	if (reverse) {
	    start = dn.lastIndexOf(',');
	    end = dn.length();
	} else {
	    start = 0;
	    end = dn.indexOf(',');
	}
	if (start < 0 || end < 0) return null;
	start = dn.indexOf('=', start);
	if (start < 0 || start > end) return null;
	start++;
	return dn.substring(start, end).trim();
    }

}
