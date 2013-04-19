package servicios.mqseries;

/**
 * Insert the type's description here.
 * Creation date: (25-10-2000 02:03:39 PM)
 * @author: Leonardo Pino
 * 25-05-2001:	Agrega parametro del nombre de la estructura de conexion
 */
public class CommParms {
	protected java.lang.String cola = "";
	protected java.util.Properties cmdOptions;
	protected java.util.Properties attribs;
	protected java.lang.String mode = "";
	protected java.lang.String commBean = "";
	private java.lang.String connQMgrName;
/**
 * CommParms constructor comment.
 */
public CommParms() {
	super();
	cmdOptions = new java.util.Properties();
	attribs = new java.util.Properties();
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:06:47 PM)
 * @return java.util.Properties
 */
public java.util.Properties getAttribs() {
	return attribs;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:05:51 PM)
 * @return java.util.Properties
 */
public java.util.Properties getCmdOptions() {
	return cmdOptions;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:05:11 PM)
 * @return java.lang.String
 */
public java.lang.String getCola() {
	return cola;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:11:16 PM)
 * @return java.lang.String
 */
public java.lang.String getCommBean() {
	return commBean;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 02:14:54 PM)
 * @return java.lang.String
 */
public java.lang.String getConnQMgrName() {
	return connQMgrName;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 11:07:34 AM)
 * @return java.lang.String
 */
public java.lang.String getMode() {
	return mode;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:06:47 PM)
 * @param newAttribs java.util.Properties
 */
public void setAttribs(java.util.Properties newAttribs) {
	attribs = newAttribs;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:05:51 PM)
 * @param newCmdOptions java.util.Properties
 */
public void setCmdOptions(java.util.Properties newCmdOptions) {
	cmdOptions = newCmdOptions;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:05:11 PM)
 * @param newCola java.lang.String
 */
public void setCola(java.lang.String newCola) {
	cola = newCola;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:11:16 PM)
 * @param newCommBean java.lang.String
 */
public void setCommBean(java.lang.String newCommBean) {
	commBean = newCommBean;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 02:14:54 PM)
 * @param newConnQMgrName java.lang.String
 */
public void setConnQMgrName(java.lang.String newConnQMgrName) {
	connQMgrName = newConnQMgrName;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 11:07:34 AM)
 * @param newMode java.lang.String
 */
public void setMode(java.lang.String newMode) {
	mode = newMode;
}
}
