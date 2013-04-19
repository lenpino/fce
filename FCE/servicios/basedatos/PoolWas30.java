package servicios.basedatos;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (23-05-2001 12:23:11 PM)
 * @author: Administrator
 */
public class PoolWas30 extends PoolWas implements Serializable{
	private static final long serialVersionUID = -6001345476771608265L;
	private javax.sql.DataSource ds;
/**
 * PoolWas30 constructor comment.
 */
public PoolWas30() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (23-05-2001 12:55:51 PM)
 * @return com.ibm.db2.jdbc.app.stdext.javax.sql.DataSource
 */
public javax.sql.DataSource getDs() {
	return ds;
}
/**
 * Insert the method's description here.
 * Creation date: (23-05-2001 12:55:51 PM)
 * @param newDs com.ibm.db2.jdbc.app.stdext.javax.sql.DataSource
 */
public void setDs(javax.sql.DataSource newDs) {
	ds = newDs;
}
}
