package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (13-11-2000 04:04:08 PM)
 * @author: Leonardo Pino
 * 08-08-2003:	Manejo del objeto ServletConfig y paso como parametro a la inicialización de los objetos de negocios
 */
public class CtrlServiceEtc implements servicios.wsapi.CtrlServiceInterface {
	protected java.lang.String tipoBean = "";
	protected javax.servlet.http.HttpServletRequest request;
	protected java.lang.Object otrosParams;
	private EtcReader confSubServices;
	protected java.lang.Object resultGen;
	protected java.lang.String modo = "";
	protected java.lang.String tipoConsulta = "";
	private javax.servlet.ServletConfig servletConfig;	
	private java.util.Properties poolList;
/**
 * CtrlServiceEtc constructor comment.
 */
public CtrlServiceEtc() {
	super();
}
/**
 * execute method comment.
 */
@Override
public void execute() throws servicios.generales.WSException{
	try {
		servicios.wsapi.ETCBeanInterface ETCBean = null;
		ETCBean = (servicios.wsapi.ETCBeanInterface)Class.forName( "servicios.generales." + getTipoBean()).newInstance();
		ETCBean.init(getServletConfig());
		ETCBean.setContext(getRequest());
		ETCBean.setParameters(getConfSubServices().getObject(getTipoBean(),getTipoConsulta()));
		ETCBean.execute();
		if(getModo().equalsIgnoreCase("out")){
			if(ETCBean.getResult() != null)
				setResultGen(ETCBean.getResult());
			else
				throw new servicios.generales.WSException("Clase: CtrlServiceEtc " + "Error: Modo de operacion mal configurado en definición de servicio en el XML");
		}
	}
	catch (Exception e) {
		if(e instanceof servicios.generales.WSException)
			throw (servicios.generales.WSException)e;
		else
			throw new servicios.generales.WSException("Error al ejecutar el servicio");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (20-03-2001 03:07:28 PM)
 * @return servicios.generales.EtcReader
 */
private EtcReader getConfSubServices() {
	return confSubServices;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 10:32:42 AM)
 * @return java.lang.String
 */
public java.lang.String getModo() {
	return modo;
}
/**
 * Insert the method's description here.
 * Creation date: (02-03-2001 11:47:21 AM)
 * @return java.lang.Object
 */
public java.lang.Object getOtrosParams() {
	return otrosParams;
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:32:09 PM)
 * @return javax.servlet.http.HttpServletRequest
 */
public javax.servlet.http.HttpServletRequest getRequest() {
	return request;
}
/**
 * getResultado method comment.
 */
@Override
public Object getResultado() {
	return resultGen;
}
/**
 * Insert the method's description here.
 * Creation date: (05-04-2001 10:58:28 AM)
 * @return java.lang.Object
 */
public java.lang.Object getResultGen() {
	return resultGen;
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:00:54 PM)
 * @return java.lang.String
 */
public java.lang.String getTipoBean() {
	return tipoBean;
}
/**
 * init method comment.
 */
@Override
public void init(Object datos) throws WSException {
		servicios.generales.InitParams params = (servicios.generales.InitParams) datos;
		setConfSubServices(params.getLectorServicioETC());
		//Extrae los datos de configuracion del servlet
		setServletConfig(params.getServletConfig());
		//Coloca en forma temporal los pool de conexion a la base de datos
		//para que sean usados por el objeto de LOGIN si se quiere colocar en la 
		//sesion
		this.poolList = params.getListaPools();
}
/**
 * Insert the method's description here.
 * Creation date: (20-03-2001 03:07:28 PM)
 * @param newConfSubServices servicios.generales.EtcReader
 */
private void setConfSubServices(EtcReader newConfSubServices) {
	confSubServices = newConfSubServices;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 10:32:42 AM)
 * @param newModo java.lang.String
 */
public void setModo(java.lang.String newModo) {
	modo = newModo;
}
/**
 * Insert the method's description here.
 * Creation date: (02-03-2001 11:47:21 AM)
 * @param newOtrosParams java.lang.Object
 */
public void setOtrosParams(java.lang.Object newOtrosParams) {
	otrosParams = newOtrosParams;
}
/**
 * setParameters method comment.
 */
@Override
public void setParameters(javax.servlet.http.HttpServletRequest request, servicios.control.Service servicio) throws WSException {
	javax.servlet.http.HttpSession session = request.getSession(false);
	servicios.generales.WSException error = null;
	if (servicio.needSession()) {
		if (session == null || session.getAttribute("estado") == null || ((Integer) session.getAttribute("estado")).intValue() == 1) {
			if (session == null)
				error = new servicios.generales.WSException("Clase: CtrlServiceEtc Error:Sin sesion");
			else if(session.getAttribute("estado") == null)
				error = new servicios.generales.WSException("Clase: CtrlServiceEtc Error: Sin estado");
			else
				error = new servicios.generales.WSException("Clase: CtrlServiceEtc Error: Estado no valido, valor = " + ((Integer) session.getAttribute("estado")).intValue());
			error.setErrorCode(100);
			throw error;
		}
	}
	try {
		//Respaldo el request
		setRequest(request);
		//Asigno el tipo de subservicio que se usuara
		setTipoBean(servicio.getTipoParser());
		//Asigno el tipo (nombre) de la consulta (sub-subservicio)
		setTipoConsulta(servicio.getNombreConsul());
		//Configuro el modo de operacion del subservicio
		setModo(servicio.getModoOpera());
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al configurar parametros en el controlador del servicio");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:32:09 PM)
 * @param newRequest javax.servlet.http.HttpServletRequest
 */
public void setRequest(javax.servlet.http.HttpServletRequest newRequest) {
	request = newRequest;
	//Coloca en forma temporal los pool de conexion a la base de datos
	//para que sean usados por el objeto de LOGIN si se quiere colocar en la 
	//sesion
	request.setAttribute("pools", this.poolList);
}
/**
 * Insert the method's description here.
 * Creation date: (05-04-2001 10:58:28 AM)
 * @param newResultGen java.lang.Object
 */
public void setResultGen(java.lang.Object newResultGen) {
	resultGen = newResultGen;
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:00:54 PM)
 * @param newTipoBean java.lang.String
 */
public void setTipoBean(java.lang.String newTipoBean) {
	tipoBean = newTipoBean;
}
	/**
	 * Returns the tipoConsulta.
	 * @return java.lang.String
	 */
	public java.lang.String getTipoConsulta() {
		return tipoConsulta;
	}

	/**
	 * Sets the tipoConsulta.
	 * @param tipoConsulta The tipoConsulta to set
	 */
	public void setTipoConsulta(java.lang.String tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	/**
	 * Returns the servletConfig.
	 * @return javax.servlet.ServletConfig
	 */
	public javax.servlet.ServletConfig getServletConfig() {
		return servletConfig;
	}

	/**
	 * Sets the servletConfig.
	 * @param servletConfig The servletConfig to set
	 */
	public void setServletConfig(javax.servlet.ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

}
