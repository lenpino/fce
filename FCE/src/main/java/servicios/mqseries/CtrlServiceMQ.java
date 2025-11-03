package servicios.mqseries;

import java.nio.ByteBuffer;



/**
 * Insert the type's description here.
 * Creation date: (29-11-2000 10:01:07 AM)
 * @author: Leonardo Pino
 * 25-05-2001:	Modificaciones para utilizar multiples QManagers
 */
public class CtrlServiceMQ implements servicios.wsapi.CtrlServiceInterface {
	protected java.lang.String fileXML = "";
	private jakarta.servlet.ServletConfig servletConfig = null;
	private java.lang.String tipoBean = "";
	protected jakarta.servlet.http.HttpServletRequest request = null;
	protected servicios.generales.OutputServiceBean resultado;
	protected MqServiceReader lectorServicioMQ;
	public CommParms paramsMQ;
	protected java.lang.String modo = "";
	protected java.lang.Object cnxStruct;
	private java.util.Properties listaConnQMgr;
	private java.lang.String conectorName;
	private String mensaje= null;
	private ByteBuffer idMensaje = null;
/**
 * CtrlServiceMQ constructor comment.
 */
public CtrlServiceMQ() {
	super();
}
/**
 * execute method comment.
 */
@Override
public void execute() throws servicios.generales.WSException {
	servicios.wsapi.CommBeanInterface CommBean = null;
	try {
		CommBean = Class.forName("servicios.mqseries." + getTipoBean())
		        .asSubclass(servicios.wsapi.CommBeanInterface.class)
		        .getDeclaredConstructor()
		        .newInstance();
		//Inicializo las estructuras de datos del bean
		CommBean.init();
		//Me conecto a la via de comunicacion, esto debe se mas general!!!
		CommBean.connect(getCnxStruct());
		//Configuro los parametros que el bean necesita
		CommBean.setProperties(paramsMQ);
		//Abro la via de comunicacion
		CommBean.open();
		if (getModo().equalsIgnoreCase("in")) {
			//Obtengo el mensaje que debio haber sido puesto en el request por algun servicio anterior
			CommBean.sendMsg(getRequest().getAttribute("mensaje"));
			this.idMensaje = ByteBuffer.allocate(CommBean.getMessageId().length);
//			printIDMQ(CommBean.getMessageId());
			this.idMensaje.put(CommBean.getMessageId());
			//Si el id del mensaje es rescatado se guarda en el request en caso de que algun servicio lo necesite
			if(this.idMensaje != null){
				getRequest().setAttribute("idMensaje",this.idMensaje);
//				printIDMQ(this.idMensaje.array());
			}
		}
		else if (getModo().equalsIgnoreCase("out")) {
			//Recibe el mensaje y lo coloca en una variable para retornarlo en el getResultado.
			//Rescata el id de mensaje si este existe en el request (poco generico)
			this.mensaje = ((ResultadoMsg)CommBean.recvMsg(((ByteBuffer)getRequest().getAttribute("idMensaje")).array())).getDataMsg();
		}
		//Cierro la via de comunicacion
		CommBean.close();
		//Desconecto de la via de comunicacion
		CommBean.disconnect();
	}
	catch (Exception e) {
		if(CommBean != null){
			//Cierro la cola
			CommBean.close();
			//Desconecto de la via de comunicacion
			CommBean.disconnect();
		}
		if(e instanceof servicios.generales.WSException)
			throw (servicios.generales.WSException)e;
		else
			throw new servicios.generales.WSException("Clase: CtrlServiceMQ - Error: Error al ejecutar el servicio - Msg: " + e.getMessage());
	}
	finally{
		if(CommBean != null){
			//Cierro la cola
			CommBean.close();
			//Desconecto de la via de comunicacion
			CommBean.disconnect();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:55:20 PM)
 * @return java.lang.Object
 */
public java.lang.Object getCnxStruct() {
	return cnxStruct;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 01:57:20 PM)
 * @return java.lang.String
 */
public java.lang.String getConectorName() {
	return conectorName;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:57:15 PM)
 * @return servicios.mqseries.MqServiceReader
 */
public MqServiceReader getLectorServicioMQ() {
	return lectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 01:56:26 PM)
 * @return java.util.Properties
 */
public java.util.Properties getListaConnQMgr() {
	return listaConnQMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:12:08 PM)
 * @return jakarta.servlet.http.HttpServletRequest
 */
public jakarta.servlet.http.HttpServletRequest getRequest() {
	return request;
}
/**
 * getResultado method comment.
 * Este metodo retorna un mensaje como String en caso de que sea un GET
 * y el id del mensaje cuando lo pida en el caso de un PUT
 */
@Override
public Object getResultado() {
	if (getModo().equalsIgnoreCase("in")) {
		return this.idMensaje;
	}
	else if (getModo().equalsIgnoreCase("out")) {
		return this.mensaje;
	}
	else
		return null;
}
/**
 * @return jakarta.servlet.ServletConfig
public jakarta.servlet.ServletConfig getServletConfig() {
 * @return javax.servlet.ServletConfig
 */
public jakarta.servlet.ServletConfig getServletConfig() {
	return servletConfig;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 12:50:15 PM)
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
		setLectorServicioMQ(params.getLectorServicioMQ());
		setListaConnQMgr(params.getListaQMgrs());
		resultado = new servicios.generales.OutputServiceBean();
	}
	catch (Exception e) {
		System.out.println("Error al inicializar el controlador del servicio de MQ");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:55:20 PM)
 * @param newCnxStruct java.lang.Object
 */
public void setCnxStruct(java.lang.Object newCnxStruct) {
	cnxStruct = newCnxStruct;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 01:57:20 PM)
 * @param newConectorName java.lang.String
 */
public void setConectorName(java.lang.String newConectorName) {
	conectorName = newConectorName;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:57:15 PM)
 * @param newLectorServicioMQ servicios.mqseries.MqServiceReader
 */
public void setLectorServicioMQ(MqServiceReader newLectorServicioMQ) {
	lectorServicioMQ = newLectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 01:56:26 PM)
 * @param newListaConnQMgr java.util.Properties
 */
public void setListaConnQMgr(java.util.Properties newListaConnQMgr) {
	listaConnQMgr = newListaConnQMgr;
}
/**
 * setParameters method comment.
 */
@Override
public void setParameters(jakarta.servlet.http.HttpServletRequest request, servicios.control.Service servicio) throws servicios.generales.WSException {
	jakarta.servlet.http.HttpSession session = request.getSession(false);
	servicios.generales.WSException error = null;
	//Configuro el modo de operacion en que operara el bean
	setModo(servicio.getModoOpera());
	if (servicio.needSession()) {
		if (session == null || session.getAttribute("estado") == null || ((Integer) session.getAttribute("estado")).intValue() == 1) {
			if (session == null)
				error = new servicios.generales.WSException("Clase: CtrlServiceMQ Error:Sin sesion");
			else if(session.getAttribute("estado") == null)
				error = new servicios.generales.WSException("Clase: CtrlServiceMQ Error: Sin estado");
			else
				error = new servicios.generales.WSException("Clase: CtrlServiceMQ Error: Estado no valido, valor = " + ((Integer) session.getAttribute("estado")).intValue());
			error.setErrorCode(100);
			throw error;
		}
	}
	try {
		if (servicio.getTipoParser().equalsIgnoreCase("MQ")) {
			paramsMQ = getLectorServicioMQ().getMqParms(servicio.getNombreConsul());
			setTipoBean(paramsMQ.getCommBean());
			setRequest(request);
			setCnxStruct(listaConnQMgr.get(paramsMQ.getConnQMgrName()));
		}
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al configurar parametros en el controlador del servicio - Msg:" + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 03:12:08 PM)
 * @param newRequest jakarta.servlet.http.HttpServletRequest
 */
public void setRequest(jakarta.servlet.http.HttpServletRequest newRequest) {
	request = newRequest;
}
/**
 * @param newServletConfig jakarta.servlet.ServletConfig
public void setServletConfig(jakarta.servlet.ServletConfig newServletConfig) {
 * @param newServletConfig javax.servlet.ServletConfig
 */
public void setServletConfig(jakarta.servlet.ServletConfig newServletConfig) {
	servletConfig = newServletConfig;	
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 12:50:15 PM)
 * @param newTipoBean java.lang.String
 */
public void setTipoBean(java.lang.String newTipoBean) {
	tipoBean = newTipoBean;
}
	/**
	 * @return
	 */
	public java.lang.String getModo() {
		return modo;
	}

	/**
	 * @param string
	 */
	public void setModo(java.lang.String string) {
		modo = string;
	}
	public void printIDMQ(byte[] data){
		for(int i=0;i < data.length;i++){
			System.out.print(data[i]);
		}
	}
}