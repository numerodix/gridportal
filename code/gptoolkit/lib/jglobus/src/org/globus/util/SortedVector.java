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

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

public class SortedVector extends Vector {
    
    //*** Re-implemented methods ***
    /** This constructor creates a new vector and inserts, sorted, the elements
     *    contained in the specified collection into the vector.
     */
    public SortedVector() {
        super();
    }
    
    /** This constructor creates a new vector and inserts, sorted, the elements
     *    contained in the specified collection into the vector.
     */
    public SortedVector( Collection c ) {
        super();
        addAll( c );
    }
    
    /** This method adds, sorted, the specified element into the vector.
     *
     *    @param element the element to be added to the vector
     */
    public boolean add( Object element ) {
        int index = findInsertPoint(element);
        super.insertElementAt( element, index );
        return (elementAt( index ) == element);
    }

    /** Find insert point.
     * @param element object to insert.
     * @return insertion point index.
     */
    public int findInsertPoint(Object element) {
        if ( size() == 0 ) return 0;
        Object entry;
        int high = size();
        int low = -1;
        int index;
        while (high - low > 1) {
            index = (high + low) / 2;
            entry = get(index);
            if ( ((java.util.Comparator)entry).compare(entry, element) > 0 )
                high = index;
            else
                low = index;
        }
        return low == -1 ? 0 : low+1;
    }
    
    /** This method inserts, sorted, the elements contained in the specified
     *    collection into the vector.
     *
     *    @param c the Collection of elements to be added to the sorted vector
     *    @return true if all elements were successfully added to the vector;
     *                    false otherwise
     */
    public boolean addAll( Collection c ) {
        boolean returnCode = true;
        if( c != null ) {
            Iterator iter = c.iterator();
            while( iter.hasNext() ) {
                boolean rc =
                    add( iter.next() );
                if ( rc == false ) {
                    returnCode = false;
                }
            }
        }
        return returnCode;
    }
    
    //*** Re-mapped methods ***
    /** This method has been remapped to add( Object element ).
     *    Since this class sorts all elements placed in the Vector,
     *    adding elements at a specific index is not allowed.
     */
    public void add( int index, Object element ) {
        add( element );
    }
    
    /** This method has been remapped to add( Object element ).
     */
    public void addElement( Object obj ) {
        add( obj );
    }
    
    /** This method has been remapped to addAll( Object element ).
     *    Since this class sorts all elements placed in the Vector,
     *    adding elements at a specific index is not allowed.
     */
    public boolean addAll( int index, Collection c ) {
        return addAll( c );
    }
    
    /** This method has been remapped to add( Object element ).
     *    Since this class sorts all elements placed in the Vector,
     *    adding elements at a specific index is not allowed.
     */
    public void insertElementAt( Object obj, int index ) {
        add( obj );
    }
    
    /** This method has been altered to replace the object at the
     *    specified index with the specified element but at an appropriate
     *    new index in the sorted vector corresponding to the new element value.
     *    Since this class sorts all elements placed in the Vector,
     *    setting elements at a specific index is not allowed.
     */
    public Object set( int index, Object element ) {
        Object returnElement = elementAt( index );
        removeElementAt( index );
        add( element );
        return returnElement;
    }
    
    /** This method has been altered to replace the object at the
     *    specified index with the specified element but at an appropriate
     *    new index in the sorted vector corresponding to the new element value.
     *    Since this class sorts all elements placed in the Vector,
     *    setting elements at a specific index is not allowed.
     */
    public void setElementAt( int index, Object obj ) {
        removeElementAt( index );
        add( obj );
    }
}
