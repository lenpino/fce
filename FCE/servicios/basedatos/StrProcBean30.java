package servicios.basedatos;

/**
 * Insert the type's description here.
 * Creation date: (05-12-2000 05:54:47 PM)
 * @author: Leonardo Pino
 */
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Timestamp;

import servicios.generales.WSException;
public class StrProcBean30 extends BaseDatosBean implements servicios.wsapi.DBBeanInterface {
		
	protected servicios.basedatos.PoolWas30 myPool;
	protected java.sql.CallableStatement stmt;
	protected java.sql.ResultSet resultOriginal;
	protected java.sql.Connection conn;
	protected int numFilas = 0;
	protected DBParserParams parametros;

/** 
 * 24-03-2003:	Se agrega el registro de parametros de salida
 * 27-03-2003:	Se indexa los parametros a partir de la llave del hashtable
 * 11-07-2003:	Uso de setNull para poder insertar nulls en forma correcta en la BD
 * 25-08-2004:	HH - Cierre de conexiones preguntando si está previamente cerrada
 * */
	private void regParams(CallableStatement stmt) throws WSException {
		int index = 1;
		try {
			if(this.parametros != null && this.parametros.entrada != null){
				java.util.Enumeration enumeracion = this.parametros.entrada.keys();
				ConsSQLparams aux = null;
				Integer llave = null;
				for (int i = 0; i < this.parametros.entrada.size(); i++) {
					llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsSQLparams)(this.parametros.entrada.get(llave));
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.INTEGER);
							else
								stmt.setInt(index,Integer.parseInt(aux.valparam));
							break;
						}
						case 'T':{ 
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else
								stmt.setTimestamp(index,Timestamp.valueOf(aux.valparam));
							break;
						}
						case 'C':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else 
								stmt.setString(index,aux.valparam);
							break;
						}
						case 'B':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.BIT);
							else
								stmt.setBoolean(index,(new Boolean(aux.valparam)).booleanValue());
							break;
						}							
						case 'D':{
							stmt.setObject(index,aux.valparam,java.sql.Types.DATE);
							break;
						}
						case 'U':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else{
								BigDecimal bdecimal = new BigDecimal(aux.valparam);
								stmt.setBigDecimal(index,bdecimal);
							}
							break;
						}
						default:
							throw new WSException("Clase: StrProcBean30 Msg: No existe definición (parametro de entrada) en consulta " + getSQLString() + " para el tipo " +aux.typeparam );
					}
				}
			}
			if(this.parametros != null && this.parametros.salida != null){
				java.util.Enumeration enumeracion = this.parametros.salida.keys();
				ConsSQLparams aux = null;
				Integer llave = null;
				for (int i = 0; i < this.parametros.salida.size(); i++) {
					llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsSQLparams)(this.parametros.salida.get(llave));
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							stmt.registerOutParameter(index,java.sql.Types.INTEGER);
							break;
						}
						case 'T':{ 
							stmt.registerOutParameter(index,java.sql.Types.TIMESTAMP);
							break;
						}
						case 'C':{ 
							stmt.registerOutParameter(index,java.sql.Types.VARCHAR);
							break;
						}
						case 'D':{
							stmt.registerOutParameter(index,java.sql.Types.DATE);
							break;
						}
						case 'U':{
//							BigDecimal bdecimal = new BigDecimal(aux.valparam);
							stmt.registerOutParameter(index,java.sql.Types.DECIMAL);
							break;
						}
						default:
							throw new WSException("Clase: StrProcBean30 Msg: No existe definición (parametro de salida) en consulta " + getSQLString() + " para el tipo " +aux.typeparam );
					}
				}
			}
		} 
		catch (Exception e) {
			if (e instanceof WSException)
				throw (WSException)e;
			else
				throw new WSException("Clase: StrProcBean30 Msg: Error al registrar parametros para consulta: " + getSQLString() + " Error: "+ e.getMessage());
		}
	}

	/**
	 * StrProcJDBCBean constructor comment.
	 */
	public StrProcBean30() {
		super();
	}
	/**
	 * closeConn method comment.
	 */
	@Override
	public void closeConn() throws servicios.generales.WSException {
		try {
			if (this.conn != null && !this.conn.isClosed())
				this.conn.close();
		} catch (Exception e) {
			throw new servicios.generales.WSException(
				"Clase StrProcJDBCBean - Error al cerrar la conexion, Mensaje "
					+ e.getMessage());
		}
	}
	/**
	 * closeResultSet method comment.
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
	 * closeStmt method comment.
	 */
	@Override
	public void closeStmt() throws servicios.generales.WSException {
		try {
			if (this.stmt != null)
				this.stmt.close();
		} catch (Exception e) {
			throw new servicios.generales.WSException(
				"Clase: StrProcJDBCBean ERROR: Problema el cerrar el statement Mensaje: "
					+ e.getMessage());
		}
	}
	/**
	 * execute method comment.
	 */
	@Override
	public synchronized void execute() throws servicios.generales.WSException, java.sql.SQLException {
		try {
			initialize();
			stmt = conn.prepareCall(this.SQLString);
			regParams(stmt);
			if (this.parametros.salida != null && this.parametros.salida.size() > 0)
				stmt.execute();
			else{
				resultOriginal = stmt.executeQuery();
			}
		}
		catch (java.lang.Exception e) {
			if (stmt != null)
				stmt.close();
			if (resultOriginal != null)
				resultOriginal.close();
			if (conn != null)
				conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: StrProcJDBCBean - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}
	/**
	 * 17-06-2003:	Retorno del numero de filas modificadas en la ejecución
	 */
	@Override
	public void executeUpdate() throws java.sql.SQLException, servicios.generales.WSException {
		try {
			initialize();
			stmt = conn.prepareCall(getSQLString());
			regParams(stmt);
			this.numFilas = stmt.executeUpdate();
		}
		catch (java.lang.Exception e) {
			stmt.close();
			conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: StrProcJDBCBean - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}
	/**
	 * getResult method comment.
	 */
	@Override
	public Object getResult() {
		if(this.parametros.salida == null || this.parametros.salida.size() == 0)
			return this.resultOriginal;
		else{
			java.util.Vector params = new java.util.Vector();
			params.addElement(this.stmt);
			params.addElement(parametros);
			return params;
		}
	}
	/**
	 * getResultUpdate method comment.
	 */
	@Override
	public int getResultUpdate() {
		return numFilas;
	}
	/*****************************************************************************
	* Initializes the data acess beans
	* 
	* @exception com.ibm.db.DataException when a database access exception occurs
	* @exception java.io.IOException when an IO error occurs
	* @exception com.ibm.servlet.connmgr.IBMConnMgrException when an error occurs in the database connection manager
	*/
	protected void initialize() throws servicios.generales.WSException {
		try {
			if (myPool.getUsuario() == null || myPool.getUsuario() == "")
				conn = myPool.getDs().getConnection();
			else
				conn = myPool.getDs().getConnection(myPool.getUsuario(), myPool.getClave());
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Error al conectarse " + e.getMessage());
		}
	}
	/**
	 * setPool method comment.
	 */
	@Override
	public void setPool(java.lang.Object elPool) {
		myPool = (PoolWas30) elPool;
	}
	@Override
	public void setParams(Object params){
		if(params!=null){
			this.parametros =(DBParserParams)params;
		}
	}

}
