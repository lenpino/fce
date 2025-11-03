package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (11/13/00 11:45:48 PM)
 * @author: Administrator
 */
public class fieldAttrib {
	
	public java.lang.String name = null;
	public java.lang.String type = null;
	public java.lang.Object value = null;
/**
 * fieldAttirb constructor comment.
 */
public fieldAttrib() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 11:54:31 PM)
 * @param strName java.lang.String
 * @param strType java.lang.String
 * @param objValue java.lang.Object
 */
public fieldAttrib(String strName, String strType, Object objValue) {
	name = strName;
	type = strType;
	value = objValue;
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
