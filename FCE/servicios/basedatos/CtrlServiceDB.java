package servicios.basedatos;
/**
 * Clase control del servicio hacia la Base de Datos
 * Creation date: (08-09-2000 04:23:27 PM)
 * @author: Leonardo Pino Werlinger
 * 24-05-2001:	Cambian los metodos para rescatar el nombre correcto del pool
 */
public class CtrlServiceDB implements servicios.wsapi.CtrlServiceInterface {
	protected java.lang.String fileXML = "";
	private servicios.basedatos.MgrCons mgrConsultas;
	private javax.servlet.ServletConfig servletConfig;
	protected servicios.generales.OutputServiceBean resultado = null;
	protected SelectBean30 jdbcDbBean;
	private String tipoBean = "";
	private String consulta = "";
	protected javax.servlet.http.HttpServletRequest request;
	protected java.lang.String modo = "";
	private int numRegModified = 0;
	private java.util.Properties poolList;
	private java.lang.String poolPropName;
	private servicios.wsapi.DBBeanInterface DBBean = null;
	private Object inoutVals = null;
	private boolean trxState = false;
	/**
	 * CtrlServiceDB constructor comment.
	 */
	public CtrlServiceDB() {
		super();
	}
	/**
	 * Ejecucion de la consulta o store procedure
	 * Creation date: (11-09-2000 11:47:23 AM)
	 * 23-05-2001:	Se eliminan los metodos para configurar la password y el login para conectarse
	 *							a la base de datos
	 * 24-05-2001:	Agregan los nuevos metodos de la interfaz de Base de datos para operar con multiples pools
	 * 06-05-2002:	Incluyo bloque finally para asegurar el cierre de los recursos de la BD
	 * 11-02-2003:	Se agrega el metodo para el paso de parametros a los beans para ocupar las funciones
	 * 						de JDBC en el registro de parametros
	 * 14-03-2003:	Se elimino el bloque finally debido a errores producidos en Tomcat despues de multiples
	 * 						llamadas al pool de DB. Se cierran las conexiones y se entregan cerradas desde el pool 
	 * 						posible bug de Tomcat o del driver de DB2
	 * 01-08-2003:	Nuevo tipo de uso tx para el soporte de transacciones
	 * 03-06-2005:	Los recursos se cierran siempre a traves del finally no dentro del codigo
	 */
	@Override
	public void execute() throws servicios.generales.WSException {
		try {
			DBBean = (servicios.wsapi.DBBeanInterface) Class.forName("servicios.basedatos." + getTipoBean()).newInstance();
			DBBean.setPool(getPoolList().get(getPoolPropName()));
			DBBean.setSQLString(consulta);
			DBBean.setParams(getParametros());
			//Si se ejecuta esperando un resultado
			if (getModo().equalsIgnoreCase("out")) {
				DBBean.execute();
				try {
					//Si el resultado no es nulo
					if (DBBean.getResult() != null){ //Configuro el objeto de salida con el ResultSet u otro tipo de objeto de resultado
						this.resultado.setValues(DBBean.getResult());
					}
				}
				catch (Exception e) {
					//Si existe un error, se cierran los recursos
					DBBean.closeResultSet();
					DBBean.closeStmt();
					DBBean.closeConn();
					throw new servicios.generales.WSException("Clase: CtrlServiceDB - ERROR al configurar el conjunto de resultados " + consulta + " Mensaje " + e.getMessage());
				}
			}
			//Si se ejecuta modificando datos
			else if (getModo().equalsIgnoreCase("in")) {
				DBBean.executeUpdate();
				//Indica el numero de registros modificados para saber si fue o no existoso la modificacion (muy orientado a BD)
				setNumRegModified(DBBean.getResultUpdate());
			}
			//Si se trata de una modalidad transaccional se ejecutan todas las consultas
			else if (getModo().equalsIgnoreCase("tx")) {
				DBBean.execute();
				setTrxState(((Boolean) DBBean.getResult()).booleanValue());
			}
			else //Si el XML esta mal formado
				throw new servicios.generales.WSException(
					"Clase: CtrlServiceDB - Error al ejecutar consulta" + consulta + "Mensaje: Error de la configuracion del modo de operacion del servicio desde el archivo XML");
			//Se cierran los recursos
//			DBBean.closeResultSet();
//			DBBean.closeStmt();
//			DBBean.closeConn();
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else if (e instanceof java.sql.SQLException)
				throw new servicios.generales.WSException("Clase: CtrlServiceDB - ERROR al cerrar la conexion, " + e.getMessage());
			else if (e instanceof java.lang.ClassNotFoundException)
				throw new servicios.generales.WSException("Clase: CtrlServiceDB - ERROR al instanciar la clase " + getTipoBean() + " Mensaje " + e.getMessage());
			else
				throw new servicios.generales.WSException("Clase: CtrlServiceDB - ERROR al ejecutar la consulta " + consulta + " Mensaje " + e.getMessage());
		}
				finally {
					if(DBBean != null){
						DBBean.closeResultSet();
						DBBean.closeStmt();
						DBBean.closeConn();
					}
				} 
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28-09-2000 10:33:23 AM)
	 * @return java.lang.String
	 */
	public String getConsulta() {
		return this.consulta;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 09:50:48 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getFileXML() {
		return fileXML;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 02:21:00 PM)
	 * @return servicios.basedatos.SelectBean30
	 */
	public SelectBean30 getJdbcDbBean() {
		return jdbcDbBean;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 02:02:55 PM)
	 * @return servicios.generales.MgrCons
	 */
	private servicios.basedatos.MgrCons getMgrConsultas() {
		return mgrConsultas;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-04-2001 10:18:22 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getModo() {
		return modo;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16-04-2001 10:29:59 AM)
	 * @return int
	 */
	public int getNumRegModified() {
		return numRegModified;
	}
	public java.lang.String getParameter(
		javax.servlet.http.HttpServletRequest request,
		java.lang.String parameterName,
		boolean checkRequestParameters,
		boolean checkInitParameters,
		boolean isParameterRequired,
		java.lang.String defaultValue)
		throws java.lang.Exception {
		java.lang.String[] parameterValues = null;
		java.lang.String paramValue = null;
		if (checkRequestParameters) {
			parameterValues = request.getParameterValues(parameterName);
			if (parameterValues != null)
				paramValue = parameterValues[0];
		}
		if ((checkInitParameters) && (paramValue == null))
			paramValue = getServletConfig().getInitParameter(parameterName);
		if ((isParameterRequired) && (paramValue == null))
			throw new Exception(parameterName + " parameter was not specified.");
		if (paramValue == null)
			paramValue = defaultValue;
		return paramValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24-05-2001 11:27:48 AM)
	 * @return java.util.Properties
	 */
	public java.util.Properties getPoolList() {
		return poolList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24-05-2001 02:45:24 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getPoolPropName() {
		return poolPropName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29-09-2000 10:13:11 AM)
	 * @return javax.servlet.http.HttpServletRequest
	 */
	public javax.servlet.http.HttpServletRequest getRequest() {
		return request;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 11:17:09 AM)
	 * @return servicios.basedatos.ResultSetService
	 */
	@Override
	public Object getResultado() {
		if (getModo().equalsIgnoreCase("in")) {
			Integer numFilas = new Integer(this.getNumRegModified());
			return numFilas;
		}
		else if (getModo().equalsIgnoreCase("none"))
			return null;
		else if (getModo().equalsIgnoreCase("tx"))
			return new Boolean(this.trxState);
		else
			return resultado;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 03:37:25 PM)
	 * @return javax.servlet.ServletConfig
	 */
	public javax.servlet.ServletConfig getServletConfig() {
		return servletConfig;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28-09-2000 10:30:36 AM)
	 * @return java.lang.String
	 */
	public String getTipoBean() {
		return this.tipoBean;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 11:49:30 AM)
	 * @param param servicios.generales.MgrCons
	 */
	@Override
	public void init(Object datos) {
		try {
			servicios.generales.InitParams params = (servicios.generales.InitParams) datos;
			setMgrConsultas(params.getMgrConsulta());
			setServletConfig(params.getServletConfig());
			resultado = new servicios.generales.OutputServiceBean();
			setPoolList(params.getListaPools());
		}
		catch (Exception e) {
			System.out.println("Error al inicializar el controlador del servicio de Bases de Datos");
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28-09-2000 10:32:40 AM)
	 * @param valor java.lang.String
	 */
	public void setConsulta(String valor) {
		this.consulta = valor;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 09:50:48 AM)
	 * @param newFileXML java.lang.String
	 */
	public void setFileXML(java.lang.String newFileXML) {
		fileXML = newFileXML;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 02:21:00 PM)
	 * @param newJdbcDbBean servicios.basedatos.SelectBean30
	 */
	public void setJdbcDbBean(SelectBean30 newJdbcDbBean) {
		jdbcDbBean = newJdbcDbBean;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 02:02:55 PM)
	 * @param newMgrConsultas servicios.generales.MgrCons
	 */
	private void setMgrConsultas(servicios.basedatos.MgrCons newMgrConsultas) {
		mgrConsultas = newMgrConsultas;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-04-2001 10:18:22 AM)
	 * @param newModo java.lang.String
	 */
	public void setModo(java.lang.String newModo) {
		modo = newModo;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16-04-2001 10:29:59 AM)
	 * @param newNumRegModified int
	 */
	public void setNumRegModified(int newNumRegModified) {
		numRegModified = newNumRegModified;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 11:45:44 AM)
	 * @param request javax.servlet.http.HttpServletRequest
	 * @param session javax.servlet.http.HttpSession
	 * 01-08-2003:	Nuevo tipo de soporte para transacciones
	 */
	@Override
	public void setParameters(javax.servlet.http.HttpServletRequest request, servicios.control.Service servicio) throws servicios.generales.WSException {
		javax.servlet.http.HttpSession session = request.getSession(false);
		servicios.generales.WSException error = null;
		if (servicio.needSession()) {
			if (session == null || session.getAttribute("estado") == null || ((Integer) session.getAttribute("estado")).intValue() == 1) {
				if (session == null) {
					error = new servicios.generales.WSException("Clase: CtrlServiceDB Error:Sin sesion");
				}
				else if (session.getAttribute("estado") == null) {
					error = new servicios.generales.WSException("Clase: CtrlServiceDB Error: Sin estado");
				}
				else{
					error = new servicios.generales.WSException("Clase: CtrlServiceDB Error: Sesion invalida o no reconocida ");
				}
				error.setErrorCode(100);
				throw error;
			}
		}
		try {
			//Limpio el result set
			this.resultado.clearRS();
			//Respaldo el request para usarlo mas tarde
			setRequest(request);
			//Configuro el modo de operacion en que operara el bean
			setModo(servicio.getModoOpera());
			if (servicio.getTipoParser().equalsIgnoreCase("SP")) {
				//Creo la estructura para contener los parametros
				inoutVals = new DBParserParams();
				//Si se trata de un store procedure se arma un onjeto consulta que genera los strings que corresponden
				ConsultaSP conssp = getMgrConsultas().getConsultaSP(servicio.getNombreConsul());
				//Crea los Properties con los parametros y sus atributos tanto de entrada como de salida
				conssp.setParams(getMgrConsultas().listaParams,(DBParserParams)inoutVals);
				//Mapea parametros del request y sesion al store procedure
				conssp.setParameters(request, session, (DBParserParams)inoutVals);
				//Rescata el tipo de Bean para instanciar en forma dinamica
				setTipoBean(conssp.getConsbean());
				//Rescata el store procedure para su posterior ejecucion
				setConsulta(conssp.getConsulta((DBParserParams)inoutVals));
				//Coloca el id para buscar el pool correcto dentro de la lista
				setPoolPropName(conssp.getIdPool());
			}
			else if (servicio.getTipoParser().equalsIgnoreCase("SQL")) {
				//Crea el objeto que posee lo necesario para realizar la consulta a partir del DOM. Inicializando la lista completa de parametros y los
				//parametros propios de esta consulta
				ConsultaSQL conssql = getMgrConsultas().getConsultaSQL(servicio.getNombreConsul());
				//Lee desde el request el valor de las variables de la consulta y los asigna a cada ConsSQLParams
				conssql.setParameters(request);
				//Configura el tipo de bean a ejecutarse
				setTipoBean(conssql.getConsbean());
				//Asigna la consulta a partir de la que se leyo desde el arbol DOM (XML de base de datos)
				setConsulta(conssql.getConsulta());
				//Entrega la lista de parametros (ConsSQLParams) con sus nombres, tipos y valores
				setParametros(conssql.getConsparam());
				//Entrega el nombre de la conexion a la base de datos
				setPoolPropName(conssql.getIdPool());
			}
			else if (servicio.getTipoParser().equalsIgnoreCase("TX")) {
				//Creo la estructura para contener los parametros
				inoutVals = new DBParserParams();
				//Esto debe extraerse desde el objeto servicio!!
				ConsultaTX constx = getMgrConsultas().getConsultaTX(servicio.getNombreConsul());
				//Rescata los vectores que deben venir en el request
				constx.setParameters(request, session);
				//Devuelve el tipo de bean para que se instancie dinamicamente
				setTipoBean(constx.getConsbean());
				//El controlador recibe un vector con todas las consultas y todos sus conjuntos de parametros
				setParametros(constx.getConsparam());
				//Se entrega el nombre del pool asociado a esta operacion (ojo que hay que revisar los tiempos de conexion)
				//Este pool debiera se especializado en la ejecucion de transacciones y por lo tanto debiera tener un tiempo 
				//mas largo permitido de conexion
				setPoolPropName(constx.getIdPool());
			}
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: CtrlServiceDB Msg: Error al configurar parametros en el controlador del servicio Error: " + e.getMessage());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24-05-2001 11:27:48 AM)
	 * @param newPoolList java.util.Properties
	 */
	public void setPoolList(java.util.Properties newPoolList) {
		poolList = newPoolList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24-05-2001 02:45:24 PM)
	 * @param newPoolPropName java.lang.String
	 */
	public void setPoolPropName(java.lang.String newPoolPropName) {
		poolPropName = newPoolPropName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29-09-2000 10:13:11 AM)
	 * @param newRequest javax.servlet.http.HttpServletRequest
	 */
	public void setRequest(javax.servlet.http.HttpServletRequest newRequest) {
		request = newRequest;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 11:17:09 AM)
	 * @param newResultado servicios.basedatos.ResultSetService
	 */
	public void setResultado(servicios.generales.OutputServiceBean newResultado) {
		resultado = newResultado;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 03:37:25 PM)
	 * @param newServletConfig javax.servlet.ServletConfig
	 */
	public void setServletConfig(javax.servlet.ServletConfig newServletConfig) {
		servletConfig = newServletConfig;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28-09-2000 10:31:20 AM)
	 */
	public void setTipoBean(String valor) {
		this.tipoBean = valor;
	}
	/**
	 * Returns the parametros.
	 * @return ConsSQLparams
	 */
	public Object getParametros() {
		return inoutVals;
	}
	public void setParametros(Object parametros) {
		this.inoutVals = parametros;
	}
	/**
	 * Returns the trxState.
	 * @return boolean
	 */
	public boolean isTrxState() {
		return trxState;
	}
	/**
	 * Sets the trxState.
	 * @param trxState The trxState to set
	 */
	public void setTrxState(boolean trxState) {
		this.trxState = trxState;
	}
}