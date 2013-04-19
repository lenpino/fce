package servicios.generales.ldap;

/**
 * Insert the type's description here.
 * Creation date: (19-03-2001 04:24:26 PM)
 * @author: Administrator
 */
public class LdapConfig {
	protected java.lang.String direccion = "";
	protected java.lang.String baseNode = "";
	protected java.lang.String classObject = "";
	protected java.util.Vector attribs = new java.util.Vector();
	public java.lang.String loginAdmin = "";
	public String claveAdmin = "";
	public java.lang.String claseLdap = "";
	public java.lang.String llave = "";

/**
 * LdapConfig constructor comment.
 */
public LdapConfig() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:34:42 PM)
 * @return java.util.Vector
 */
public java.util.Vector getAttribs() {
	return attribs;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:29:57 PM)
 * @return java.lang.String
 */
public java.lang.String getBaseNode() {
	return baseNode;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 10:21:09 AM)
 * @return java.lang.String
 */
public java.lang.String getClaseLdap() {
	return claseLdap;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:30:26 PM)
 * @return java.lang.String
 */
public java.lang.String getClassObject() {
	return classObject;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:29:23 PM)
 * @return java.lang.String
 */
public java.lang.String getDireccion() {
	return direccion;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:34:42 PM)
 * @param newAttribs java.util.Vector
 */
public void setAttribs(java.util.Vector newAttribs) {
	attribs = newAttribs;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:29:57 PM)
 * @param newBaseNode java.lang.String
 */
public void setBaseNode(java.lang.String newBaseNode) {
	baseNode = newBaseNode;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 10:21:09 AM)
 * @param newClaseLdap java.lang.String
 */
public void setClaseLdap(java.lang.String newClaseLdap) {
	claseLdap = newClaseLdap;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:30:26 PM)
 * @param newClassObject java.lang.String
 */
public void setClassObject(java.lang.String newClassObject) {
	classObject = newClassObject;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 9:21:40 AM)
 * @return java.lang.String
 */
public java.lang.String getLoginAdmin() {
	return loginAdmin;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 9:22:05 AM)
 * @param newClaveAdmin int
 */
public void setClaveAdmin(String newClaveAdmin) {
	claveAdmin = newClaveAdmin;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 04:29:23 PM)
 * @param newDireccion java.lang.String
 */
public void setDireccion(java.lang.String newDireccion) {
	direccion = newDireccion;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 9:22:05 AM)
 * @return String
 */
public String getClaveAdmin() {
	return claveAdmin;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 10:45:13 AM)
 * @param newLlave java.lang.String
 */
public void setLlave(java.lang.String newLlave) {
	llave = newLlave;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 10:45:13 AM)
 * @return java.lang.String
 */
public java.lang.String getLlave() {
	return llave;
}
/**
 * Insert the method's description here.
 * Creation date: (30-Apr-2002 9:21:40 AM)
 * @param newLoginAdmin java.lang.String
 */
public void setLoginAdmin(java.lang.String newLoginAdmin) {
	loginAdmin = newLoginAdmin;
}
}