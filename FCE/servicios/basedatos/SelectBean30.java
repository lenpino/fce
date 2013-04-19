package servicios.basedatos;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import servicios.generales.WSException;

/**
 * Insert the type's description here.
 * Creation date: (05-09-2000 03:12:06 PM)
 * @author: Leonardo Pino
 */

/**
 * 06-01-2003:	Se cambia el uso de Statement por el de PreparedStatement para optimizar las consultas
 */

public class SelectBean30
	extends BaseDatosBean
	implements servicios.wsapi.DBBeanInterface {
	private servicios.basedatos.PoolWas30 myPool;
	protected java.sql.PreparedStatement stmt;
	protected java.sql.ResultSet resultOriginal;
	private DBParserParams parametros;
	protected java.sql.Connection conn;
	protected int numFilas = 0;
	/**
	 * SelectJDBCBean constructor comment.
	 */
	public SelectBean30() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-11-2000 10:43:17 AM)
	 */
	@Override
	public void closeConn() throws servicios.generales.WSException {
		if (this.conn == null)
			return;
		try {
			this.conn.close();
		} catch (Exception e) {
			throw new servicios.generales.WSException(
				"Error al cerrar la conexion");
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 09:27:24 AM)
	 */
	@Override
	public void closeResultSet() throws servicios.generales.WSException {
		if (this.resultOriginal == null) {
			return;
		}
		try {
			resultOriginal.close();
		} catch (java.sql.SQLException e) {
			throw new servicios.generales.WSException(
				"Error al cerrar el ResultSet");
		}
		return;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 09:24:04 AM)
	 */
	@Override
	public void closeStmt() throws servicios.generales.WSException {
		if (getStmt() == null)
			return;
		else {
			try {
				getStmt().close();
			} catch (Exception e) {
				throw new servicios.generales.WSException(
					"Clase: SelectBean30 ERROR: Error al cerrar el statement Mensaje: "
						+ e.getMessage());
			}
		}
	}
	/*****************************************************************************
	* Execute the SQL statement
	* 
	* @exception com.ibm.db.DataException when a database access exception occurs
	* @exception java.io.IOException when an IO error occurs
	* @exception com.ibm.servlet.connmgr.IBMConnMgrException when an error occurs in the database connection manager
	*/
	@Override
	public void execute()
		throws servicios.generales.WSException, java.sql.SQLException {
		try {
			initialize();
			stmt = conn.prepareStatement(getSQLString());
			regParams(stmt);
			resultOriginal = stmt.executeQuery();
		} catch (java.lang.Exception e) {
			if (stmt != null)
				stmt.close();
			if (resultOriginal != null)
				resultOriginal.close();
			if (conn != null)
				conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException(
					"Clase: SelectJDBCBean - Error al ejecutar la consulta "
						+ getSQLString()
						+ "Mensaje "
						+ e.getMessage());
		}
	}
	/*****************************************************************************
	* Execute the SQL statement
	* 
	* @exception com.ibm.db.DataException when a database access exception occurs
	* @exception java.io.IOException when an IO error occurs
	* @exception com.ibm.servlet.connmgr.IBMConnMgrException when an error occurs in the database connection manager
	*/
	@Override
	public void executeUpdate() throws java.io.IOException, java.sql.SQLException {
		try {
			initialize();
			stmt = conn.prepareStatement(getSQLString());
			regParams(stmt);
			numFilas = stmt.executeUpdate();
		} catch (java.lang.Exception e) {
			stmt.close();
			if (resultOriginal != null)
				resultOriginal.close();
			conn.close();
			System.out.println(e.getMessage());
			if (e instanceof java.io.IOException)
				throw (java.io.IOException) e;
			if (e instanceof java.sql.SQLException)
				throw (java.sql.SQLException) e;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06-09-2000 11:26:47 AM)
	 * @return java.sql.ResultSet
	 */
	@Override
	public Object getResult() {
		return this.resultOriginal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16-04-2001 10:21:02 AM)
	 * @return int
	 */
	@Override
	public int getResultUpdate() {
		return numFilas;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 09:08:09 AM)
	 * @return java.sql.Statement
	 */
	public java.sql.Statement getStmt() {
		return this.stmt;
	}
	/*****************************************************************************
	* Initializes the data acess beans
	* 
	* @exception java.io.IOException when an IO error occurs
	* @exception com.ibm.servlet.connmgr.IBMConnMgrException when an error occurs in the database connection manager
	*/
	public void initialize() throws java.io.IOException, java.sql.SQLException {
		try {
			if (myPool.getUsuario() == null || myPool.getUsuario() == "")
				conn = myPool.getDs().getConnection();
			else
				conn =
					myPool.getDs().getConnection(
						myPool.getUsuario(),
						myPool.getClave());
		} catch (Exception e) {
			if (e instanceof java.io.IOException)
				throw (java.io.IOException) e;
			if (e instanceof java.sql.SQLException)
				throw (java.sql.SQLException) e;
		}
	}
	/**
	 * setPool method comment.
	 */
	@Override
	public void setPool(java.lang.Object elPool) {
		myPool = (PoolWas30) elPool;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 09:09:54 AM)
	 * @param param java.sql.ResultSet
	 */
	public void setResult(java.sql.ResultSet param) {
		this.resultOriginal = param;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 09:06:16 AM)
	 * @param param java.sql.Statement
	 */
	public void setStmt(java.sql.PreparedStatement param) {
		this.stmt = param;
	}
	@Override
	public void setParams(Object params){
		this.parametros =(DBParserParams)params;
	}
	private void regParams(PreparedStatement stmt) throws WSException {
		int index=1;
		try {
			if(this.parametros != null){
				java.util.Enumeration enumeracion = this.parametros.entrada.keys();
				ConsSQLparams aux = null;
				for (int i = 0; i < this.parametros.entrada.size(); i++) {
					Integer llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsSQLparams)(this.parametros.entrada.get(llave));
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							stmt.setInt(index,Integer.parseInt(aux.valparam));
							break;
						}
						case 'T':{ 
							stmt.setTimestamp(index,Timestamp.valueOf(aux.valparam));
							break;
						}
						case 'C':{ 
							stmt.setString(index,aux.valparam);
							break;
						}
						case 'D':{
							stmt.setObject(index,aux.valparam,java.sql.Types.DATE);
							break;
						}
						case 'U':{
							BigDecimal bdecimal = new BigDecimal(aux.valparam);
							stmt.setBigDecimal(index,bdecimal);
							break;
						}
						default:
							throw new WSException("Clase: SelectBean30 Msg: No existe definición para el tipo " +aux.typeparam );
					}
				}
			}
		} catch (Exception e) {
			if (e instanceof WSException)
				throw (WSException)e;
			else
				throw new WSException("Clase: SelectBean30 Msg: Error al registrar parametros Error: "+ e.getMessage());
		}
	}
}
