package servicios.control;

import java.util.HashMap;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import servicios.generales.Today;
import servicios.generales.WSException;
public class ServiceMgr {
	private java.lang.String fileXML = "";
	private  servicios.basedatos.MgrCons consMgr;
	protected servicios.generales.InitParams params;
	protected ProgramReader programLoader;
	public java.util.Properties xmlList;
	protected java.util.Properties todasRanas;
	protected servicios.mensajeria.MsgTemplateReader msgReader;
	protected servicios.mqseries.MqServiceReader lectorServicioMQ;
	protected servicios.generales.EtcReader lectorServicioETC;
	private java.util.Properties poolList;
	private java.util.Properties QMgrsList;
	public servicios.generales.Logger logs;
	public String pathReal;
	private HashMap listaXsl;
/**
 * ServiceMgr constructor comment.
 */
public ServiceMgr() {
	super();
	consMgr = new servicios.basedatos.MgrCons();
	params = new servicios.generales.InitParams();
//	programLoader = new servicios.control.ProgramReader();
	lectorServicioMQ = new servicios.mqseries.MqServiceReader();
	lectorServicioETC = new servicios.generales.EtcReader();
}
/**
 * Insert the method's description here.
 * Creation date: (18-10-2000 11:09:37 AM)
 */
public String errorManager(Exception error, jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) throws WSException {
	// Se invalida la sesion para que no se hagan ejecucion de servicios sin estar dentro de la aplicacion
	jakarta.servlet.http.HttpSession session = request.getSession(false);
	//Se invalida SSI no existe el flag de invalidacion
	if(session!=null && session.getAttribute("flagInvalidacion") == null){
		this.logs.debug("Invalidando la sesion " + session.getId());
		//Extrae el contexto en caso de errores
		session.invalidate();
	}
	//Graba el error en el log para su posterior analisis
	this.logs.error(error.getMessage());
	this.logs.logPop();
	if (error instanceof servicios.generales.WSException) {
//		servicios.basedatos.hibernate.HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		if(request.getParameter("fmtSalida") == null || !request.getParameter("fmtSalida").equalsIgnoreCase("sf")){
			String key = Integer.toString(((servicios.generales.WSException)error).getErrorCode());
			String paginaError = this.getTodasRanas().getProperty(key);
			if (paginaError != null)
				return paginaError;
			else{
				//Si el codigo de error es -1 y es un WSException no se imprime el stacktrace
				if(((servicios.generales.WSException)error).getErrorCode() != -1)
					error.printStackTrace();
				throw new servicios.generales.WSException(error.getMessage());
			}
		}
		else{
			WSException wsError = (WSException)error;
			throw wsError;
            /*
			StringBuffer msgError = new StringBuffer();
			msgError.append("<error>\n\t<codigo>-1</codigo>\n\t<descripcion>");
			msgError.append(((WSException)error).getMessage());
			msgError.append("</descripcion>\n</error>");
			response.setContentType("text/plain");
			java.io.PrintWriter out;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				throw new servicios.generales.WSException(error.getMessage());
			}
			out.print(msgError.toString());
			out.flush();
			out.close();
			*/
		}
		//return "";
	}
	else
		throw new servicios.generales.WSException(error.getMessage());
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 11:14:40 AM)
 * @return servicios.generales.MgrCons
 */
public servicios.basedatos.MgrCons getConsMgr() {
	return consMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 10:25:37 AM)
 * @return java.lang.String
 */
public java.lang.String getFileXML() {
	return fileXML;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 06:46:50 PM)
 * @return servicios.generales.EtcReader
 */
public servicios.generales.EtcReader getLectorServicioETC() {
	return lectorServicioETC;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:12:19 PM)
 * @return servicios.mqseries.MqServiceReader
 */
public servicios.mqseries.MqServiceReader getLectorServicioMQ() {
	return lectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:36:38 PM)
 * @return servicios.mensajeria.MsgTemplateReader
 */
public servicios.mensajeria.MsgTemplateReader getMsgReader() {
	return msgReader;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:28:00 PM)
 * @return servicios.generales.InitParams
 */
public servicios.generales.InitParams getParams() {
	return params;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 10:36:32 AM)
 * @return java.util.Properties
 */
public java.util.Properties getPoolList() {
	return poolList;
}
/**
 * Insert the method's description here.
 * Creation date: (17-09-2000 12:28:31 PM)
 * @return servicios.control.ProgRequest
 */
public ProgramReader getProgramLoader() {
	return programLoader;
}
/**
 * Insert the method's description here.
 * Creation date: (18-10-2000 02:51:56 PM)
 * @return java.util.Properties
 */
public java.util.Properties getTodasRanas() {
	return todasRanas;
}
/**
 * Insert the method's description here.
 * Creation date: (22-09-2000 09:28:31 AM)
 * @return java.util.Properties
 */
public java.util.Properties getXmlList() {
	return xmlList;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 12:39:19 PM)
 */

//31/01/2001: Se diferencia si es que existe o no un archivo de configuración
//			para evitar errores en la inicialización del servlet (solo MQ)
//04/04/2001:	Se modifica para una inicializacion del XML del servicio ETC
//24/05/2001: Modifica para utilizar multiples pools de conecciones
//04/01/2003: Agrega la inicialización del objeto de logeo permitiendo entregarle
//						el parametro de donde esta su archivo de configuración
/**
 * 05-06-2003:	Se agrega la mejora para el uso de path relativos en la configuracion
 * 						de los servicios
 * 28-07-2003:	Nuevo atributo con el path en el file system de la aplicacion
 * 05-08-2004:	Se inicializa los parametros de BD en el inicio de la aplicacion y no en cada consulta
 * 05-07-2005:	Cambios para poder manejar el nuevo tipo de XML
 * 21-12-2005:	Nueva variable de instancia con la lista de paginas XSL
 * */

public void init(ServletConfig config) {
	//Extraigo el contexto del servlet para saber los path reales de los recursos en la maquina
	ServletContext contexto = config.getServletContext();
	//Configuro la información del path en el file system en donde se encuentra el home de esta aplicación
	pathReal = contexto.getRealPath("/");
	try {
		logs = new servicios.generales.Logger(ServiceMgr.class,contexto.getRealPath(config.getInitParameter("logPropertiesPath")));
	}
	catch (Exception e){
		System.out.println("Error al configurar el path del log");
	}
	//Si no viene el parametro del tipo de XML se usa el modo antiguo
	if(config.getInitParameter("tipoXml") == null)
		programLoader = new servicios.control.ProgramReader();
	else if((config.getInitParameter("tipoXml")).equalsIgnoreCase("new"))
		programLoader = new servicios.control.XProgramReader();
	else if((config.getInitParameter("tipoXml")).equalsIgnoreCase("old"))
		programLoader = new servicios.control.ProgramReader();
	else
		logs.error("ERROR, valor incorrecto del parametro tipoXml ");
	//Carga el programa y los archivos XML de los servicios y de inicialización de los Queue Managers de MQSeries
	try {
		programLoader.init(contexto.getRealPath(config.getInitParameter("appProgram")));
	}
	catch (Exception e) {
		logs.error("Error al cargar el archivo XML del programa PATH: " + contexto.getRealPath(config.getInitParameter("appProgram")) + "MSG: " + e.getMessage());
	}
	//Crea la lista de pools
	try{
		poolList = programLoader.getPools();
	}
	catch(Exception e){
			logs.error("Clase: ServiceMgr Error: Problema al crear los pools de conecciones Msg: " + e.getMessage());
	}			
	//Configura el objeto Properties que contiene la lista de XMLs
	try {
		setXmlList(programLoader.getXmlFiles());
	}
	catch (Exception e) {
		logs.error("Error al configurar los archivos XML " + e.getMessage());
	}
	//Configura el objeto Properties que contiene la lista de las paginas de error
	try {
		setTodasRanas(programLoader.getTodosErrores());
	}
	catch (Exception e) {
		logs.error("Error al configurar los JSP de error " + e.getMessage());
	}
	//Entrega el XML para configurar el servicio de Bases de Datos
	try {
		setFileXML(getXmlList().getProperty("BD"));
		getConsMgr().load(contexto.getRealPath(getFileXML()));
		getConsMgr().createParameters();
	}
	catch (Exception e) {
		logs.error("Error al cargar el archivo XML de las consultas");
	}
	//Entrega el XML para configurar el servicio de Mensajeria
	/*	try {
	setFileXML(getXmlList().getProperty("MSG"));
	getMsgReader().load(getFileXML());
	}
	catch (Exception e) {
	System.out.println("Error al cargar el archivo XML de la mensajeria");
	} */
	//Entrega el XML para configurar el servicio de parametros de MQ
	try {
		if (getXmlList().getProperty("MQCOMM") != null) {
			setFileXML(getXmlList().getProperty("MQCOMM"));
			getLectorServicioMQ().load(contexto.getRealPath(getFileXML()));
		}
	}
	catch (Exception e) {
		logs.error("Error al cargar el archivo XML de la configuracion MQ " + e.getMessage());
	}
	//Entrega el XML para configurar los subservicios de ETC
	try {
		if (getXmlList().getProperty("ETC") != null) {
			setFileXML(getXmlList().getProperty("ETC"));
			getLectorServicioETC().init(contexto.getRealPath(getFileXML()));
		}
	}
	catch (Exception e) {
		System.out.println("Error al cargar el archivo XML de la configuracion ETC " + e.getMessage());
	}
	//Configura y se conecta al Queue Manager especificado en el archivo .servlet si es que se requiere
	try {
			//Solo si existe archivo de configuracion para MQ se intenta conectar al QM
			if (getXmlList().getProperty("MQCOMM") != null)
				QMgrsList = programLoader.getQMgrs();
	}
	catch (Exception e) {
		if (e instanceof com.ibm.mq.MQException)
			logs.error("Un error MQ ocurrio al conectarse al Queue Manager : Completion code " + ((com.ibm.mq.MQException) e).completionCode + " Reason code " + ((com.ibm.mq.MQException) e).reasonCode);
		else
			logs.error("Error al configurar y conectarse al Queue Manager");
	}
	params.setServletConfig(config);
	params.setMgrConsulta(getConsMgr());
	params.setMgrTemplates(getMsgReader());
	params.setLectorServicioMQ(getLectorServicioMQ());
	params.setLectorServicioETC(getLectorServicioETC());
	params.setListaPools(poolList);
	params.setListaQMgrs(QMgrsList);
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 11:14:40 AM)
 * @param newConsMgr servicios.generales.MgrCons
 */
public void setConsMgr(servicios.basedatos.MgrCons newConsMgr) {
	consMgr = newConsMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 10:25:37 AM)
 * @param newFileXML java.lang.String
 */
public void setFileXML(java.lang.String newFileXML) {
	fileXML = newFileXML;
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 06:46:50 PM)
 * @param newLectorServicioETC servicios.generales.EtcReader
 */
public void setLectorServicioETC(servicios.generales.EtcReader newLectorServicioETC) {
	lectorServicioETC = newLectorServicioETC;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:12:19 PM)
 * @param newLectorServicioMQ servicios.mqseries.MqServiceReader
 */
public void setLectorServicioMQ(servicios.mqseries.MqServiceReader newLectorServicioMQ) {
	lectorServicioMQ = newLectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:36:38 PM)
 * @param newMsgReader servicios.mensajeria.MsgTemplateReader
 */
public void setMsgReader(servicios.mensajeria.MsgTemplateReader newMsgReader) {
	msgReader = newMsgReader;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:28:00 PM)
 * @param newParams servicios.generales.InitParams
 */
public void setParams(servicios.generales.InitParams newParams) {
	params = newParams;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 10:36:32 AM)
 * @param newPoolList java.util.Properties
 */
public void setPoolList(java.util.Properties newPoolList) {
	poolList = newPoolList;
}
/**
 * Insert the method's description here.
 * Creation date: (17-09-2000 12:28:31 PM)
 * @param newProgramLoader servicios.control.ProgRequest
 */
public void setProgramLoader(ProgramReader newProgramLoader) {
	programLoader = newProgramLoader;
}
/**
 * Insert the method's description here.
 * Creation date: (18-10-2000 02:51:56 PM)
 * @param newTodasRanas java.util.Properties
 */
public void setTodasRanas(java.util.Properties newTodasRanas) {
	todasRanas = newTodasRanas;
}
/**
 * Insert the method's description here.
 * Creation date: (22-09-2000 09:28:31 AM)
 * @param newXmlList java.util.Properties
 */
public void setXmlList(java.util.Properties newXmlList) {
	xmlList = newXmlList;
}
public HashMap getListaXsl() {
	return listaXsl;
}
public void setListaXsl(HashMap listaXsl) {
	this.listaXsl = listaXsl;
}
}
