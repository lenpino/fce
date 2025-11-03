package servicios.control;

/**
 * Insert the type's description here.
 * Creation date: (13-09-2000 11:23:39 AM)
 * @author: Leonardo Pino
 * 11-04-2001	Se cambia el atributo de independencia que no se ocupaba por el de
 *						modo de operacion que permite informar si el servicio rescata datos
 *						si modifica datos o ninguno de los anteriores.
 */
public class Service {
	protected String servicio;
	protected boolean generaBean;
	protected java.lang.String beanSalida = "";
	protected java.lang.String xmlFile = "";
	protected int timeOut;
	protected java.lang.String nombreConsul = "";
	protected java.lang.String tipoParser = "";
	protected java.lang.String jsp;
	protected java.lang.String claseNegocios="sgte";
	protected java.lang.String session;
	protected java.lang.String modoOpera = "";
/**
 * Service constructor comment.
 */
public Service() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:25:56 AM)
 * @return java.lang.String
 */
public java.lang.String getBeanSalida() {
	return beanSalida;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:36:29 PM)
 * @return java.lang.String
 */
public java.lang.String getClaseNegocios() {
	return claseNegocios;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:35:42 PM)
 * @return java.lang.String
 */
public java.lang.String getJsp() {
	return jsp;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 09:54:17 AM)
 * @return java.lang.String
 */
public java.lang.String getModoOpera() {
	return modoOpera;
}
/**
 * Insert the method's description here.
 * Creation date: (27-09-2000 04:36:23 PM)
 * @return java.lang.String
 */
public java.lang.String getNombreConsul() {
	return nombreConsul;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:24:06 AM)
 * @return int
 */
public String getServicio() {
	return servicio;
}
/**
 * Insert the method's description here.
 * Creation date: (01-12-2000 10:56:32 AM)
 * @return java.lang.String
 */
public java.lang.String getSession() {
	return session;
}
/**
 * Insert the method's description here.
 * Creation date: (21-09-2000 02:35:04 PM)
 * @return int
 */
public int getTimeOut() {
	return timeOut;
}
/**
 * Insert the method's description here.
 * Creation date: (27-09-2000 05:19:06 PM)
 * @return java.lang.String
 */
public java.lang.String getTipoParser() {
	return tipoParser;
}
/**
 * Insert the method's description here.
 * Creation date: (21-09-2000 02:34:40 PM)
 * @return java.lang.String
 */
public java.lang.String getXmlFile() {
	return xmlFile;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:24:48 AM)
 * @return boolean
 */
public boolean isGeneraBean() {
	return generaBean;
}
/**
 * Insert the method's description here.
 * Creation date: (01-12-2000 10:52:01 AM)
 * @return boolean
 */
public boolean needSession() throws servicios.generales.WSException{
	if(session.equalsIgnoreCase("no"))
		return false;
	else if(session.equalsIgnoreCase("si"))
		return true;
	else
		throw new servicios.generales.WSException("Error al requerir la seguridad");
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:25:56 AM)
 * @param newBeanSalida java.lang.String
 */
public void setBeanSalida(java.lang.String newBeanSalida) {
	beanSalida = newBeanSalida;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:36:29 PM)
 * @param newClaseNegocios java.lang.String
 */
public void setClaseNegocios(java.lang.String newClaseNegocios) {
	claseNegocios = newClaseNegocios;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:24:48 AM)
 * @param newGeneraBean boolean
 */
public void setGeneraBean(boolean newGeneraBean) {
	generaBean = newGeneraBean;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:35:42 PM)
 * @param newJsp java.lang.String
 */
public void setJsp(java.lang.String newJsp) {
	jsp = newJsp;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 09:54:17 AM)
 * @param newModoOpera java.lang.String
 */
public void setModoOpera(java.lang.String newModoOpera) {
	modoOpera = newModoOpera;
}
/**
 * Insert the method's description here.
 * Creation date: (27-09-2000 04:36:23 PM)
 * @param newNombreConsul java.lang.String
 */
public void setNombreConsul(java.lang.String newNombreConsul) {
	nombreConsul = newNombreConsul;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:24:06 AM)
 * @param newServicio int
 */
public void setServicio(String newServicio) {
	servicio = newServicio;
}
/**
 * Insert the method's description here.
 * Creation date: (01-12-2000 10:56:32 AM)
 * @param newSession java.lang.String
 */
public void setSession(java.lang.String newSession) {
	session = newSession;
}
/**
 * Insert the method's description here.
 * Creation date: (21-09-2000 02:35:04 PM)
 * @param newTimeOut int
 */
public void setTimeOut(int newTimeOut) {
	timeOut = newTimeOut;
}
/**
 * Insert the method's description here.
 * Creation date: (27-09-2000 05:19:06 PM)
 * @param newTipoParser java.lang.String
 */
public void setTipoParser(java.lang.String newTipoParser) {
	tipoParser = newTipoParser;
}
/**
 * Insert the method's description here.
 * Creation date: (21-09-2000 02:34:40 PM)
 * @param newXmlFile java.lang.String
 */
public void setXmlFile(java.lang.String newXmlFile) {
	xmlFile = newXmlFile;
}
}
