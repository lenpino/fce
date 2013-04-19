package servicios.mqseries;

/**
 * Inserte aquí la descripción del tipo.
 * Fecha de creación: (17-11-2000 01:46:20)
 * @author: Leonardo Pino
 */
public class ResultadoMsg extends servicios.generales.OutputServiceBean {
	public java.lang.String dataMsg = null;
/**
 * Comentario de constructor ResultadoMsg.
 */
public ResultadoMsg() {
	super();
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:48:36)
 * @return java.lang.String
 */
public java.lang.String getDataMsg() {
	return dataMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 01:48:36)
 * @param newDataMsg java.lang.String
 */
protected void setDataMsg(java.lang.String newDataMsg) {
	dataMsg = newDataMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 02:06:13)
 * @return java.lang.String
 */
@Override
public String toString() {
	return null;
}
}
