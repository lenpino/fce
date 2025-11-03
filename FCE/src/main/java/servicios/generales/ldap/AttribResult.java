package servicios.generales.ldap;

/**
 * Insert the type's description here.
 * Creation date: (6/25/2002 3:16:29 PM)
 * @author: Administrator
 */
public class AttribResult {
	private java.lang.String name = null;
	private ValueResult values = null;
/**
 * AttribResult constructor comment.
 */
public AttribResult() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 3:21:15 PM)
 * @param vName java.lang.String
 * @param vValues directory.util.ValueResult
 */
public AttribResult(String vName, ValueResult vValues) {
	setName(vName);
	setValues(vValues);
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 3:18:51 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 3:20:01 PM)
 * @return directory.util.ValueResult
 */
public ValueResult getValues() {
	return values;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 3:18:51 PM)
 * @param newName java.lang.String
 */
void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 3:20:01 PM)
 * @param newValues directory.util.ValueResult
 */
void setValues(ValueResult newValues) {
	values = newValues;
}
}
