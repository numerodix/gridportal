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

/**
   Represents a range of integers.
   The name reflects the fact that it is used with FTP extended mode restart markers,
   where it represents a range of transfered bytes.
 **/
public class ByteRange {
    /**
       lower range limit
     **/
    public long from;
    /**
       upper range limit
     **/
    public long to;

    /**
       @param from lower range limit
       @param to upper range limit
       @throws IllegalARgumentException if to < from
     **/
    public ByteRange(long from, long to) {
	if (to < from ) {
	    throw new IllegalArgumentException(
		   "Range upper boundary smaller than lower boundary");
	}
	this.from = from;
	this.to = to;
    }

    /**
       @return true if both object logically represent the same range
       (even if they are two separate ByteRange instances)
     **/
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ByteRange) {
            ByteRange otherObj = (ByteRange)other;
            return ( this.to == otherObj.to && this.from == otherObj.from); 
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (int)(this.to + this.from);
    }

    /**
      copying constructor
    */
    public ByteRange(ByteRange src) {
	this.copy(src);
    }

    private void copy(ByteRange other) {
	this.from = other.from;
	this.to = other.to;
    }

    public static final int THIS_BELOW = 1;
    public static final int ADJACENT = 2;
    public static final int THIS_SUPERSET = 3;
    public static final int THIS_SUBSET = 4;
    public static final int THIS_ABOVE = 5;
    /**
       If this range can be consolidated with the other one,
       modify this range so that it represents the result of merging
       this and the other range. 
       The parameter object remains intact.
       Return value indicates what operation has been performed.
       <ul>
       <li> If the two ranges were separate, then this range will remain unchanged.
       The return value will be THIS_BELOW if this range is below the other
       range, or THIS_ABOVE in the other case.
       <li>If this range was a superset of the other,
       then this range remains unchanged and THIS_SUPERSET will be returned.
       A special case of this situation is when both ranges were equal.
       <li>If other range was a superset of this, OTHER_SUPERSET will be returned.
       <li>Otherwise ADJACENT is returned, meaning that merge was possible
       but no range is a superset of the other.
       </ul>
       Note that two ranges are considered separate if there is at least
       one integer between them. For instance, "1-3" and "5-7" are separate
       but "1-3" and "4-7" are adjacent because merge is possible.
     **/
    public int merge(final ByteRange other) {
	/* notation:
	     t = this range
	     o = other range
	     - = the common subset of both
	   Thus there are 13 cases:
	     o t
	     ot
	     o-t
	     o-
	     o-o
	     -t
	     -
	     -o
	     t-t
	     t-
	     t-o
	     to
	     t o
	 */
	if (other.from < this.from) {

	    if (other.to + 1 < this.from) {
		// o t
		return THIS_ABOVE;
	    }

	    this.from = other.from;

	    if (this.to <= other.to) {
		// o-
		// o-o
		this.to = other.to;
		return THIS_SUBSET;
	    }
	    // ot
	    // o-t
	} else {
	
	    if (this.to + 1 < other.from) {
		// t o
		return THIS_BELOW;
	    }
	
	    if ( other.to <= this.to) {
		// -t
		// -
		// t-t
		// t-
		return THIS_SUPERSET;
	    }

	    this.to = other.to;
	    
	    if ( other.from == this.from) {
		// -o
		return THIS_SUBSET;
	    }
	    // t-
	    //to
	}

	return ADJACENT;
    }

    public String toString() {
	return Long.toString(from) + "-" + Long.toString(to);
    }
}

