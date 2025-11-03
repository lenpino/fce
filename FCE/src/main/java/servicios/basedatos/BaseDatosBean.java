package servicios.basedatos;

/**
 * Insert the type's description here.
 * Creation date: (05-09-2000 02:03:09 PM)
 * @author: Leonardo Pino
 */
 
import javax.sql.DataSource;

public class BaseDatosBean {
	protected DataSource ds				= null;
	protected java.lang.String userID	= null;
	protected java.lang.String password	= null;
	protected java.lang.String SQLString= null;
/**
 * BaseDatosBean constructor comment.
 */
public BaseDatosBean() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (05-09-2000 03:46:13 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 * @param fuenteData com.ibm.db2.jdbc.app.stdext.javax.sql.DataSource
 */
public BaseDatosBean(String userID, String password, DataSource fuenteData) {
	this.userID = userID;
	this.password = password;
	this.ds = fuenteData;
}
/**
 * Insert the method's description here.
 * Creation date: (14-08-2000 04:21:03 PM)
 * @return java.lang.String
 */
public String getPassword() {
	return this.password;
}
	/*****************************************************************************
	* Get method for the SQLString property
	* 
	* @return the value of the property
	*/
	public java.lang.String getSQLString() {
		return SQLString;
	}
/**
 * Insert the method's description here.
 * Creation date: (14-08-2000 04:20:21 PM)
 * @return java.lang.String
 */
String getUserId() {
	return this.userID;
}
/**
 * Insert the method's description here.
 * Creation date: (04-08-2000 04:19:15 PM)
 * @param valor com.ibm.db2.jdbc.app.stdext.javax.sql.DataSource
 */
public void setDataSource(DataSource valor) {
	this.ds = valor;
}
	/*****************************************************************************
	* Set method for the password property
	* 
	* @param value the new value for the proprety
	*/
	public void setPassword(java.lang.String value) {
		this.password = value;
	}
/**
 * Este método se creó en VisualAge.
 */
public void setSQLString(String consultaSql) {
	SQLString = consultaSql;
}
	/*****************************************************************************
	* Set method for the userID property
	* 
	* @param value the new value for the proprety
	*/
	public void setUserID(java.lang.String value) {
		this.userID = value;
	}
}
