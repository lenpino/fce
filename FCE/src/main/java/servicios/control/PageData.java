package servicios.control;

/**
 * Insert the type's description here.
 * Creation date: (11-01-2001 05:31:24 PM)
 * @author: Leonardo Pino
 */
public class PageData {
	protected java.lang.String beanPaginado = "";
	protected String numLineas = "";
	protected String numColumna = "";
	public java.lang.String[] imagenes = new java.lang.String[5];
	protected java.lang.String akaServlet = "";
	protected java.lang.String programa = "";
/**
 * PageData constructor comment.
 */
public PageData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:42:35 AM)
 * @return java.lang.String
 */
public java.lang.String getAkaServlet() {
	return akaServlet;
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:06 PM)
 * @return java.lang.String
 */
public java.lang.String getBeanPaginado() {
	return beanPaginado;
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:51 PM)
 * @return int
 */
public int getNumColumna() {
	return Integer.parseInt(numColumna);
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:27 PM)
 * @return int
 */
public int getNumLineas() {
	return Integer.parseInt(numLineas);
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:43:14 AM)
 * @return java.lang.String
 */
public java.lang.String getPrograma() {
	return programa;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:42:35 AM)
 * @param newAkaServlet java.lang.String
 */
public void setAkaServlet(java.lang.String newAkaServlet) {
	akaServlet = newAkaServlet;
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:06 PM)
 * @param newBeanPaginado java.lang.String
 */
public void setBeanPaginado(java.lang.String newBeanPaginado) {
	beanPaginado = newBeanPaginado;
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:51 PM)
 * @param newNumColumna int
 */
public void setNumColumna(String newNumColumna) {
	numColumna = newNumColumna;
}
/**
 * Insert the method's description here.
 * Creation date: (11-01-2001 06:07:27 PM)
 * @param newNumLineas int
 */
public void setNumLineas(String newNumLineas) {
	numLineas = newNumLineas;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:43:14 AM)
 * @param newPrograma java.lang.String
 */
public void setPrograma(java.lang.String newPrograma) {
	programa = newPrograma;
}
}
