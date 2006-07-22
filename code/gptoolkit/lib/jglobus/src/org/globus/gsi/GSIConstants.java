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

/** 
 * Defines common constants used by GSI.
 */
public interface GSIConstants {
    
    /** The character sent on the wire to request delegation */
    public static final char DELEGATION_CHAR = 'D';

    /** Null ciphersuite supported in older Globus servers */
    public static final String[] GLOBUS_CIPHER  = {"SSL_RSA_WITH_NULL_MD5"};

    /** Indicates no delegation */
    public static final int DELEGATION_NONE = 1;

    /** Indicates limited delegation. 
     * Depending on the settings it might mean GSI-2 limited delegation
     * or GSI-3 limited delegation. */
    public static final int DELEGATION_LIMITED = 2;

    /** Indicates full delegation. 
     * Depending on the settings it might mean GSI-2 full delegation
     * or GSI-3 impersonation delegation. */
    public static final int DELEGATION_FULL = 3;

    /** Indicates GSI mode (allows for delegation during authentication). 
     */
    public static final Integer MODE_GSI = new Integer(1);
    
    /** Indicates SSL compatibility mode (does not allow for delegation 
     * during authentication). */
    public static final Integer MODE_SSL = new Integer(2);
    
    /** Indicates full delegation. */
    public static final Integer DELEGATION_TYPE_FULL 
	= new Integer(GSIConstants.DELEGATION_FULL);
    
    /** Indicates limited delegation. */
    public static final Integer DELEGATION_TYPE_LIMITED 
	= new Integer(GSIConstants.DELEGATION_LIMITED);
    
    /** Indicates End-Entity Certificate, e.g. user certificate */
    public static final int EEC = 3;

    /** Indicates Certificate Authority certificate */
    public static final int CA = 4;
    
    /** Indicates legacy full Globus proxy */
    public static final int GSI_2_PROXY         = 10;

    /** Indicates legacy limited Globus proxy */
    public static final int GSI_2_LIMITED_PROXY = 11;

    /** Indicates proxy draft compliant restricted proxy.
     * A proxy with embedded policy. */
    public static final int GSI_3_RESTRICTED_PROXY    = 12;

    /** Indicates proxy draft compliant independent proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#INDEPENDENT
     * ProxyPolicy.INDEPENDENT} policy language OID.*/
    public static final int GSI_3_INDEPENDENT_PROXY   = 13;

    /** Indicates proxy draft compliant impersonation proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#IMPERSONATION 
     * ProxyPolicy.IMPERSONATION} policy language OID.*/
    public static final int GSI_3_IMPERSONATION_PROXY = 14;

    /** Indicates proxy draft compliant limited impersonation proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#LIMITED 
     * ProxyPolicy.LIMITED} policy language OID.*/
    public static final int GSI_3_LIMITED_PROXY       = 15;

    /** Indicates RFC 3820 compliant restricted proxy.
     * A proxy with embedded policy. */
    public static final int GSI_4_RESTRICTED_PROXY    = 16;

    /** Indicates RFC 3820 compliant independent proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#INDEPENDENT
     * ProxyPolicy.INDEPENDENT} policy language OID.*/
    public static final int GSI_4_INDEPENDENT_PROXY   = 17;

    /** Indicates RFC 3820 compliant impersonation proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#IMPERSONATION 
     * ProxyPolicy.IMPERSONATION} policy language OID.*/
    public static final int GSI_4_IMPERSONATION_PROXY = 18;

    /** Indicates RFC 3820 compliant limited impersonation proxy.
     * A proxy with {@link org.globus.gsi.proxy.ext.ProxyPolicy#LIMITED 
     * ProxyPolicy.LIMITED} policy language OID.*/
    public static final int GSI_4_LIMITED_PROXY       = 19;

    /** GSI Transport protection method type
     * that will be used or was used to protect the request.
     * Can be set to:
     * {@link GSIConstants#SIGNATURE SIGNATURE} or
     * {@link GSIConstants#ENCRYPTION ENCRYPTION} or
     * {@link GSIConstants#NONE NONE}.
     */
    public static final String GSI_TRANSPORT =
        "org.globus.security.transport.type";

    /** integrity message protection method. */
    public static final Integer SIGNATURE
        = new Integer(1);

    /** privacy message protection method. */
    public static final Integer ENCRYPTION
        = new Integer(2);

    /** none message protection method. */
    public static final Integer NONE =
        new Integer(Integer.MAX_VALUE);

}
