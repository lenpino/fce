package servicios.generales.ldap;

/**
 * Insert the type's description here.
 * Creation date: (19-03-2001 04:31:02 PM)
 * @author: Administrator
 */
public class LdapAttrib {
	protected java.lang.String attribName = "";
	protected java.lang.String attribValue = "";
/**
 * LdapAttrib constructor comment.
 */
public LdapAttrib() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:31:58 PM)
 * @return java.lang.String
 */
public java.lang.String getAttribName() {
	return attribName;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:32:23 PM)
 * @return java.lang.String
 */
public java.lang.String getAttribValue() {
	return attribValue;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:31:58 PM)
 * @param newAttribName java.lang.String
 */
public void setAttribName(java.lang.String newAttribName) {
	attribName = newAttribName;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:32:23 PM)
 * @param newAttribValue java.lang.String
 */
public void setAttribValue(java.lang.String newAttribValue) {
	attribValue = newAttribValue;
}
}
