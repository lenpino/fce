package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (11-09-2000 03:13:16 PM)
 * @author: Leonardo Pino
 * 24-05-2001:	Elimina atributos y metodos asociados al DataSource y se reemplaza por
 * 						la lista de pools
 * 25-05-2001:	Elimina atributos asociados al conector de MQ y agrega la lista de conectores
 */
public class InitParams {
	protected jakarta.servlet.ServletConfig servletConfig;
	protected servicios.basedatos.MgrCons mgrConsulta;
	protected servicios.mensajeria.MsgTemplateReader mgrTemplates;
	protected servicios.mqseries.MqServiceReader lectorServicioMQ;
	protected servicios.generales.EtcReader lectorServicioETC;
	private java.util.Properties listaPools;
	private java.util.Properties listaQMgrs;
/**
 * InitParams constructor comment.
 */
public InitParams() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (20-03-2001 03:02:14 PM)
 * @return servicios.generales.EtcReader
 */
public EtcReader getLectorServicioETC() {
	return lectorServicioETC;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:21:59 PM)
 * @return int
 */
public servicios.mqseries.MqServiceReader getLectorServicioMQ() {
	return lectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 10:49:51 AM)
 * @return java.util.Properties
 */
public java.util.Properties getListaPools() {
	return listaPools;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 11:06:26 AM)
 * @return java.util.Properties
 */
public java.util.Properties getListaQMgrs() {
	return listaQMgrs;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:16:45 PM)
 * @return servicios.generales.MgrCons
 */
public servicios.basedatos.MgrCons getMgrConsulta() {
	return mgrConsulta;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:52:55 PM)
 * @return servicios.mensajeria.MsgTemplateReader
 */
public servicios.mensajeria.MsgTemplateReader getMgrTemplates() {
	return mgrTemplates;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:15:01 PM)
 * @return jakarta.servlet.ServletConfig
 */
public jakarta.servlet.ServletConfig getServletConfig() {
	return servletConfig;
}
/**
 * Insert the method's description here.
 * Creation date: (20-03-2001 03:02:14 PM)
 * @param newLectorServicioETC servicios.generales.EtcReader
 */
public void setLectorServicioETC(EtcReader newLectorServicioETC) {
	lectorServicioETC = newLectorServicioETC;
}
/**
 * Insert the method's description here.
 * Creation date: (30-11-2000 02:21:59 PM)
 * @param newLectorServicioMQ int
 */
public void setLectorServicioMQ(servicios.mqseries.MqServiceReader newLectorServicioMQ) {
	lectorServicioMQ = newLectorServicioMQ;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 10:49:51 AM)
 * @param newListaPools java.util.Properties
 */
public void setListaPools(java.util.Properties newListaPools) {
	listaPools = newListaPools;
}
/**
 * Insert the method's description here.
 * Creation date: (25-05-2001 11:06:26 AM)
 * @param newListaQMgrs java.util.Properties
 */
public void setListaQMgrs(java.util.Properties newListaQMgrs) {
	listaQMgrs = newListaQMgrs;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:16:45 PM)
 * @param newMgrConsulta servicios.generales.MgrCons
 */
public void setMgrConsulta(servicios.basedatos.MgrCons newMgrConsulta) {
	mgrConsulta = newMgrConsulta;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:52:55 PM)
 * @param newMgrTemplates servicios.mensajeria.MsgTemplateReader
 */
public void setMgrTemplates(servicios.mensajeria.MsgTemplateReader newMgrTemplates) {
	mgrTemplates = newMgrTemplates;
}
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 03:15:01 PM)
 * @param newServletConfig jakarta.servlet.ServletConfig
 */
public void setServletConfig(jakarta.servlet.ServletConfig newServletConfig) {
	servletConfig = newServletConfig;
}
}
