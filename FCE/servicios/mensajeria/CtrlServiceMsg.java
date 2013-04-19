package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (11/14/00 4:08:08 PM)
 * @author: Administrator
 */
public class CtrlServiceMsg implements servicios.wsapi.CtrlServiceInterface {
	protected java.lang.String fileXML = "";
	private javax.servlet.ServletConfig servletConfig = null;
	private java.lang.String tipoBean = "";
	protected javax.servlet.http.HttpServletRequest request = null;
	protected servicios.generales.OutputServiceBean resultado;
	protected servicios.mensajeria.TemplateMessage templateMsg = null;
	protected MsgTemplateReader lectorMsg;
/**
 * CtrlServiceMsg constructor comment.
 */
public CtrlServiceMsg() {
	super();
}
/**
 * execute method comment.
 */
@Override
public void execute() throws servicios.generales.WSException {}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 00:38:06)
 * @return java.lang.String
 */
public String getFileXML() {
	return fileXML;
}
/**
 * Insert the method's description here.
 * Creation date: (27-11-2000 02:43:34 PM)
 * @return servicios.mensajeria.MsgTemplateReader
 */
public MsgTemplateReader getLectorMsg() {
	return lectorMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 00:45:03)
 * @return java.lang.String
 * @param request javax.servlet.http.HttpServletRequest
 * @param parameterName java.lang.String
 * @param checkRequestParameter java.lang.Boolean
 * @param checkInitParameter java.lang.Boolean
 * @param isParameterRequired java.lang.Boolean
 * @param defaultValue java.lang.String
 * @exception java.lang.Exception La descripción de excepción.
 */
public String getParameter(javax.servlet.http.HttpServletRequest request, String parameterName, boolean checkRequestParameters, boolean checkInitParameters, boolean isParameterRequired, String defaultValue) throws java.lang.Exception {

	java.lang.String[] parameterValues = null;
	java.lang.String paramValue = null;

	if (checkRequestParameters) {
		parameterValues = request.getParameterValues(parameterName);

		if (parameterValues != null)
			paramValue = parameterValues[0];
	}

	if ( (checkInitParameters) && (paramValue == null) )
		paramValue = getServletConfig().getInitParameter(parameterName);

	if ( (isParameterRequired) && (paramValue == null) )
		throw new Exception(parameterName + " parameter was not specified.");

	if (paramValue == null)
		paramValue = defaultValue;

	return paramValue;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:05:54)
 * @return javax.servlet.http.HttpServletRequest
 */
public javax.servlet.http.HttpServletRequest getRequest() {
	return request;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 18:09:13)
 * @return servicios.basedatos.ResultSetService
 */
@Override
public Object getResultado() {
	return resultado;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 00:48:38)
 * @return javax.servlet.ServletConfig
 */
public javax.servlet.ServletConfig getServletConfig() {
	return servletConfig;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:11:39)
 * @return servicios.mensajeria.TemplateMessage
 */
public servicios.mensajeria.TemplateMessage getTemplateMsg() {
	return templateMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:00:15)
 * @return java.lang.String
 */
public java.lang.String getTipoBean() {
	return tipoBean;
}
/**
 * init method comment.
 */
@Override
public void init(Object datos) throws servicios.generales.WSException {
	
	try {
		servicios.generales.InitParams params = (servicios.generales.InitParams) datos;
		setServletConfig(params.getServletConfig());
		setLectorMsg(params.getMgrTemplates());
		resultado = new servicios.generales.OutputServiceBean();
	}
	catch (Exception e) {
		System.out.println("Error al inicializar el controlador del servicio de Mensajería");
	}
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 00:50:36)
 * @param newFileXML java.lang.String
 */
public void setFileXML(String newFileXML) {
	fileXML = newFileXML;	
}
/**
 * Insert the method's description here.
 * Creation date: (27-11-2000 02:43:34 PM)
 * @param newLectorMsg servicios.mensajeria.MsgTemplateReader
 */
public void setLectorMsg(MsgTemplateReader newLectorMsg) {
	lectorMsg = newLectorMsg;
}
/**
 * setParameters method comment.
 */
@Override
public void setParameters(javax.servlet.http.HttpServletRequest request, servicios.control.Service servicio) throws servicios.generales.WSException {
	javax.servlet.http.HttpSession session = request.getSession(false);
	if (servicio.needSession()) {
		if (session == null || session.getAttribute("estado") == null || ((Integer) session.getAttribute("estado")).intValue() == 1) {
			servicios.generales.WSException error = new servicios.generales.WSException("Sin sesion");
			error.setErrorCode(100);
			throw error;
		}
	}
	try {
		//getResultado().clearRS();
		if (servicio.getTipoParser().equalsIgnoreCase("MQ")) {
			//Se trata de un Mensaje MQ, por lo tanto, se instancia un objeto MsgBeanMQ
			servicios.mensajeria.MsgService configParms = getLectorMsg().getMsgService(servicio.getNombreConsul());
		}
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al configurar parametros en el controlador del servicio");
	}	
	
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:05:54)
 * @param newRequest javax.servlet.http.HttpServletRequest
 */
public void setRequest(javax.servlet.http.HttpServletRequest newRequest) {
	request = newRequest;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 18:09:13)
 * @param newResultado servicios.basedatos.ResultSetService
 */
public void setResultado(servicios.generales.OutputServiceBean newResultado) {
	resultado = newResultado;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 18:02:53)
 * @param newServletConfig javax.servlet.ServletConfig
 */
public void setServletConfig(javax.servlet.ServletConfig newServletConfig) {
	servletConfig = newServletConfig;	
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:11:39)
 * @param newTemplateMsg servicios.mensajeria.TemplateMessage
 */
public void setTemplateMsg(servicios.mensajeria.TemplateMessage newTemplateMsg) {
	templateMsg = newTemplateMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:00:15)
 * @param newTipoBean java.lang.String
 */
public void setTipoBean(java.lang.String newTipoBean) {
	tipoBean = newTipoBean;
}
}
