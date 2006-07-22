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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.ftp.exception.FTPException;

/**
 * 
 * 
 * 
 * 
 * 
 */
public class MlsxEntry {

   private static Log logger = 
	LogFactory.getLog(MlsxEntry.class.getName());

    public static final String SIZE = "size";
    public static final String MODIFY = "modify";
    public static final String CREATE = "create";
    public static final String TYPE = "type";
    public static final String UNIQUE = "unique";
    public static final String PERM = "perm";
    public static final String LANG = "lang";
    public static final String MEDIA_TYPE = "media-type";
    public static final String CHARSET = "charset";
    
    public static final String TYPE_FILE = "file";
    public static final String TYPE_CDIR = "cdir";
    public static final String TYPE_PDIR = "pdir";
    public static final String TYPE_DIR = "dir";
    
    private String fileName = null;
    private Hashtable facts = new Hashtable();

    /**
     * Constructor for MlsxEntry.
     * @param mlsxEntry
     * @throws FTPException
     */
    public MlsxEntry(String mlsxEntry) throws FTPException {
        this.parse(mlsxEntry);
    }
    
    /**
     * Method parse.
     * @param mlsxEntry
     */
    private void parse(String mlsxEntry) {
        
        StringTokenizer tokenizer = new StringTokenizer(mlsxEntry, ";");

        while (tokenizer.hasMoreTokens()) {
            
            String token = tokenizer.nextToken();
            
            if (tokenizer.hasMoreTokens()) {
                
                //next fact
                String fact = token;
                logger.debug("fact: " + fact);
                int equalSign = fact.indexOf('=');
                String factName = fact.substring(0, equalSign).trim().toLowerCase();
                String factValue =
                    fact.substring(equalSign + 1, fact.length());

                facts.put(factName, factValue);

            } else {

                // name: trim leading space
                this.fileName = token.substring(1, 
                                                token.length());
                logger.debug("name: " + fileName);

            }
        }
    }

    public void set(String factName, String factValue) {
        facts.put(factName, factValue);
    }
    
    public String getFileName() {
        return this.fileName;
    }

    public String get(String factName) {
        return (String) facts.get(factName);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Enumeration e = facts.keys();
        
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = (String)facts.get(key);
            buf.append( key + "=" + value +";");
        }
        
        buf.append( " " + fileName);
        
        return buf.toString();		
    }
	
}
