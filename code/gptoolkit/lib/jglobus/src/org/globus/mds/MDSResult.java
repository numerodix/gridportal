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
package org.globus.mds;

import java.util.Vector;
import java.util.Enumeration;

/** Simple wrapper for org.globus.common.MVHashtable used in MDS applications.
 * 
 * @see org.globus.common.MVHashtable
 */
public class MDSResult extends org.globus.common.MVHashtable {
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();

    Enumeration dnEnum = keys();
    while ( dnEnum.hasMoreElements() ) {
      String dn = (String)dnEnum.nextElement();
      Vector values = get( dn );

      Enumeration valueEnum = values.elements();
      while ( valueEnum.hasMoreElements() ) {
	stringBuffer.append( dn + "=" + valueEnum.nextElement() + "\n" );
      }
    }
    return stringBuffer.toString();
  }
}
