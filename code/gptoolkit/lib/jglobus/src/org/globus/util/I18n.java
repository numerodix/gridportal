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
package org.globus.util;

import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * An utility class for internationalized message handling.
 * Example usage::
 * <pre>
 * private static I18n i18n = I18n.getI18n("org.globus.resource");
 * ...
 * public void hello() {
 *    String filename = "file1";
 *    String msg = i18n.getMessage("noFile", new String[]{filename});
 *    ...
 * }
 * </pre>
 */
public class I18n {
    
    private static Map mapping = new HashMap();
    
    private ResourceBundle messages = null;

    protected I18n(ResourceBundle messages) {
	this.messages = messages;
    }

    /**
     * Retrieve a I18n instance by resource name.
     *
     * @param resource resource name. See {@link
     *        ResourceBundle#getBundle(String) ResourceBundle.getBundle()}
     */
    public static synchronized I18n getI18n(String resource) {
        return getI18n(resource, null);
    }

    /**
     * Retrieve a I18n instance by resource name 
     *
     * @param resource resource name. See {@link
     *        ResourceBundle#getBundle(String) ResourceBundle.getBundle()}
     * @param loader the class loader to be used to load
     *        the resource. This parameter is only used
     *        initially to load the actual resource. Once the resource
     *        is loaded, this argument is ignored.
     */
    public static synchronized I18n getI18n(String resource,
					    ClassLoader loader) {
	I18n instance = (I18n)mapping.get(resource);
	if (instance == null) {
            // try using context class loader
            if (loader == null) {
                loader = Thread.currentThread().getContextClassLoader();
            }
	    instance = new I18n(ResourceBundle.getBundle(resource, 
							 Locale.getDefault(),
							 loader));
	    mapping.put(resource, instance);
	}
	return instance;
    }
    
    /**
     * Gets a message from resource bundle.
     */
    public String getMessage(String key) 
	throws MissingResourceException {
        return messages.getString(key);
    }
    
    /**
     * Gets a formatted message from resource bundle
     */
    public String getMessage(String key, Object arg) 
	throws MissingResourceException {
        return getMessage(key, new Object[] {arg});
    }
    
    /**
     * Gets a formatted message from resource bundle
     */
    public String getMessage(String key, Object[] vars) 
	throws MissingResourceException {
	return MessageFormat.format(messages.getString(key), vars);
    }
    
}
