package servicios.basedatos;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import servicios.generales.WSException;
import servicios.wsapi.DBBeanInterface;
/**
 * @autor lpino
 * @Fecha 14-jul-03
*/
public class TrxBean extends BaseDatosBean implements DBBeanInterface {
	protected servicios.basedatos.PoolWas30 myPool;
	protected java.sql.CallableStatement stmt;
	protected java.sql.ResultSet resultOriginal;
	protected java.sql.Connection conn;
	protected boolean flagUpd = false;
	protected int numFilas = 0;
	protected ArrayList parametros;
	/**
	 * Constructor for TrxBean.
	 */
	public TrxBean() {
		super();
	}
	/**
	 * Constructor for TrxBean.
	 * @param userID
	 * @param password
	 * @param fuenteData
	 */
	public TrxBean(String userID, String password, DataSource fuenteData) {
		super(userID, password, fuenteData);
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#closeConn()
	 */
	@Override
	public void closeConn() throws WSException {
		try {
			if(!this.conn.isClosed())
				this.conn.close();
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Clase TrxBean - Error al cerrar la conexion, Mensaje " + e.getMessage());
		}
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#closeResultSet()
	 */
	@Override
	public void closeResultSet() throws WSException {
		if (this.resultOriginal == null) {
			return;
		}
		try {
			resultOriginal.close();
		}
		catch (java.sql.SQLException e) {
			throw new servicios.generales.WSException("Error al cerrar el ResultSet");
		}
		return;
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#closeStmt()
	 */
	@Override
	public void closeStmt() throws WSException {
		try {
			if (this.stmt != null)
				this.stmt.close();
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Clase: TrxBean ERROR: Problema el cerrar el statement Mensaje: " + e.getMessage());
		}
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#execute()
	 */
	@Override
	public void execute() throws WSException, SQLException {
		try {
			DatabaseMetaData dbmeta;
			initialize();
			dbmeta = conn.getMetaData();
			int numfilas = 0;
			if(dbmeta.supportsTransactions())
				conn.setAutoCommit(false);
			else
				throw new WSException("Clase: TrxBean Error: El driver no soporta transacciones");
			//Se toma la sgte o la primera consulta desde la lista
			for(int i=0;i < this.parametros.size();i++){
				TxParams consultaI = (TxParams)parametros.get(i);
				//Encuentro el maximo numero de parametros entre los de entrada (no deben haber parametros de salida para el uso de batch updates)
				int maxParams = consultaI.numeroPars;
				//Si la consulta tiene un set de parametros mayor a uno entonces nos vamos por el batch update
				CallableStatement stmt = conn.prepareCall(consultaI.getConsulta()); 
				//Mientras exista una lista de parametros los registro
				for(int j=0;j<maxParams;j++){
					regParams(stmt,consultaI.getParams(),j);
					//Si el driver soporta batch updates
					if(dbmeta.supportsBatchUpdates())
						//Agrego la consulta a la lista
						stmt.addBatch();
					else{
						//Si no se soporta se ejecuta el SP altiro por cada una de las listas de parametros disponibles
						numfilas = stmt.executeUpdate();
						//Si el resultado no afecto a ninguna fila se debe ignorar esta transaccion
						if(numfilas == 0){
							flagUpd = false;
							//No ejecuto mas ya que ya se presento el error
							break;
						}
					}
				}								
				//Si soporta batch updates ejecuto la lista de SPs
				if(dbmeta.supportsBatchUpdates()){
					int[] updateCounts = stmt.executeBatch();
					//Reviso todos los resultados para ver si alguno fallo
					for(int k=0;k<updateCounts.length;k++){
						if(updateCounts[k] == -3){
							//Si falla se marca para hacer un roolback
							flagUpd = true;
						}
					}
				}
			//Si existen mas consultas entonces se continua con los siguientes
			}
			//Al terminar hago commit de todas las actividades hechas si no ha habido ningun error al modificar o al insertar
			if(flagUpd){
				conn.rollback();
			}
			else{
				conn.commit();
			}
		}
		catch (java.lang.Exception e) {
			//Deshago la transaccion
			conn.rollback();
			if (stmt != null)
				stmt.close();
			if (resultOriginal != null)
				resultOriginal.close();
			if (conn != null)
				conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: TrxBean - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#executeUpdate()
	 */
	@Override
	public void executeUpdate() throws WSException, SQLException, IOException {
		try {
			initialize();
			stmt = conn.prepareCall(getSQLString());
//			regParams(stmt);
			this.numFilas = stmt.executeUpdate();
		}
		catch (java.lang.Exception e) {
			stmt.close();
			conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: TrxBean - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#getResult()
	 */
	@Override
	public Object getResult() {
		return new Boolean(flagUpd);
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#getResultUpdate()
	 */
	@Override
	public int getResultUpdate() {
		return numFilas;
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#setPool(Object)
	 */
	@Override
	public void setPool(Object elPool) {
		myPool = (PoolWas30) elPool;
	}
	/**
	 * @see servicios.wsapi.DBBeanInterface#setParams(Object)
	 */
	@Override
	public void setParams(Object params) {
		if(params!=null){
			this.parametros =(ArrayList)params;
		}
	}
/** 
 * 24-03-2003:	Se agrega el registro de parametros de salida
 * 27-03-2003:	Se indexa los parametros a partir de la llave del hashtable
 * 11-07-2003:	Uso de setNull para poder insertar nulls en forma correcta en la BD
 * */
	private void regParams(CallableStatement stmt,DBParserParams consultaI, int parametroJ) throws WSException {
		int index = 1;
		try {
			if(consultaI.entrada != null){
				java.util.Enumeration enumeracion = consultaI.entrada.keys();
				ConsParamsX aux = null;
				Integer llave = null;
				for (int i = 0; i < consultaI.entrada.size(); i++) {
					llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsParamsX)(consultaI.entrada.get(llave));
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							if(aux.valparams == null)
								stmt.setNull(index,java.sql.Types.INTEGER);
							else if(aux.valparams.size() == 0)
								stmt.setNull(index,java.sql.Types.INTEGER);
							else if(aux.valparams.size() <= parametroJ)
								stmt.setNull(index,java.sql.Types.INTEGER);
							else if(aux.valparams.get(parametroJ) == null)
								stmt.setNull(index,java.sql.Types.INTEGER);
							else if(((String)aux.valparams.get(parametroJ)).equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.INTEGER);
							else
								stmt.setInt(index,Integer.parseInt((String)aux.valparams.get(parametroJ)));
							break;
						}
						case 'T':{ 
							if(aux.valparams == null)
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else if(aux.valparams.size() == 0)
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else if(aux.valparams.size() <= parametroJ)
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else if(aux.valparams.get(parametroJ) == null)
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else if(((String)aux.valparams.get(parametroJ)).equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else
								stmt.setTimestamp(index,Timestamp.valueOf((String)aux.valparams.get(parametroJ)));
							break;
						}
						case 'C':{
							if(aux.valparams == null)
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else if(aux.valparams.size() == 0)
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else if(aux.valparams.size() <= parametroJ)
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else if(aux.valparams.get(parametroJ) == null)
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else if(((String)aux.valparams.get(parametroJ)).equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else
								stmt.setString(index,(String)aux.valparams.get(parametroJ));
							break;
						}
						case 'B':{
							if(aux.valparams == null)
								stmt.setNull(index,java.sql.Types.BIT);
							else if(aux.valparams.size() == 0)
								stmt.setNull(index,java.sql.Types.BIT);
							else if(aux.valparams.size() <= parametroJ)
								stmt.setNull(index,java.sql.Types.BIT);
							else if(aux.valparams.get(parametroJ) == null)
								stmt.setNull(index,java.sql.Types.BIT);
							else if(((String)aux.valparams.get(parametroJ)).equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.BIT);
							else
								stmt.setBoolean(index,(new Boolean((String)aux.valparams.get(parametroJ))).booleanValue());
							break;
						}							
						case 'D':{
							stmt.setObject(index,aux.valparam,java.sql.Types.DATE);
							break;
						}
						case 'U':{
							if(aux.valparams == null)
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else if(aux.valparams.size() == 0)
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else if(aux.valparams.size() <= parametroJ)
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else if(aux.valparams.get(parametroJ) == null)
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else if(((String)aux.valparams.get(parametroJ)).equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else{
								BigDecimal bdecimal = new BigDecimal((String)aux.valparams.get(parametroJ));
								stmt.setBigDecimal(index,bdecimal);
							}
							break;
						}
						default:
							throw new WSException("Clase: TrxBean Msg: No existe definición (parametro de entrada) para el tipo " +aux.typeparam );
					}
				}
			}
		} 
		catch (Exception e) {
			if (e instanceof WSException)
				throw (WSException)e;
			else
				throw new WSException("Clase: TrxBean Msg: Error al registrar parametros Error: "+ e.getMessage());
		}
	}
	
	/*****************************************************************************
	* Initializes the data acess beans
	* 
	* @exception com.ibm.db.DataException when a database access exception occurs
	* @exception java.io.IOException when an IO error occurs
	* @exception com.ibm.servlet.connmgr.IBMConnMgrException when an error occurs in the database connection manager
	*/
	private void initialize() throws servicios.generales.WSException {
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

}
