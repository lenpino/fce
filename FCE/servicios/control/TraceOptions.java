package servicios.control;

/**
 * Insert the type's description here.
 * Creation date: (24-05-2001 05:30:22 PM)
 * @author: Administrator
 */
public class TraceOptions {
	private java.lang.String logPath = "";
	private boolean encendido = true;
/**
 * TraceOptions constructor comment.
 */
public TraceOptions() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 05:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getLogPath() {
	return logPath;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 05:31:47 PM)
 * @return boolean
 */
public boolean isEncendido() {
	return encendido;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 05:31:47 PM)
 * @param newEncendido boolean
 */
public void setEncendido(boolean newEncendido) {
	encendido = newEncendido;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 05:30:45 PM)
 * @param newLogPath java.lang.String
 */
public void setLogPath(java.lang.String newLogPath) {
	logPath = newLogPath;
}
}
