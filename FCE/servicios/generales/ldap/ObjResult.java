package servicios.generales.ldap;

import java.util.NoSuchElementException;

/**
 * Insert the type's description here.
 * Creation date: (6/25/2002 2:05:54 PM)
 * @author: Administrator
 */
public class ObjResult extends java.util.Vector implements java.util.Enumeration {
	private java.lang.Object objEnum = null;
/**
 * ListResult constructor comment.
 */
public ObjResult() {
	super();
}
/**
 * ListResult constructor comment.
 * @param initialCapacity int
 */
public ObjResult(int initialCapacity) {
	super(initialCapacity);
}
/**
 * ListResult constructor comment.
 * @param initialCapacity int
 * @param capacityIncrement int
 */
public ObjResult(int initialCapacity, int capacityIncrement) {
	super(initialCapacity, capacityIncrement);
}
/**
 * ListResult constructor comment.
 * @param c java.util.Collection
 */
public ObjResult(java.util.Collection c) {
	super(c);
}
	/**
	 * Tests if this enumeration contains more elements.
	 *
	 * @return  <code>true</code> if and only if this enumeration object
	 *           contains at least one more element to provide;
	 *          <code>false</code> otherwise.
	 */
@Override
public boolean hasMoreElements() {
	try {
		if (objEnum == null)
			objEnum = this.elements();
		
		return ((java.util.Enumeration)objEnum).hasMoreElements();
	}
	catch (Exception e) {
		return false;
	}
}
	/**
	 * Returns the next element of this enumeration if this enumeration
	 * object has at least one more element to provide.
	 *
	 * @return     the next element of this enumeration.
	 * @exception  NoSuchElementException  if no more elements exist.
	 */
@Override
public Object nextElement() {
	
	try {
		if (objEnum == null)
			objEnum = this.elements();
		
		return ((java.util.Enumeration)objEnum).nextElement();
	}
	catch (Exception e) {
		throw new java.util.NoSuchElementException();
	}
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
@Override
public String toString() {
	// Insert code to print the receiver here.
	// This implementation forwards the message to super. You may replace or supplement this.
	return super.toString();
}
}
