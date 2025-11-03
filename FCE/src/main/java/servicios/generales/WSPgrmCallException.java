package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (15-11-2000 04:14:09 PM)
 * @author: Leonardo Pino
 */
public class WSPgrmCallException extends Exception {
	private int errorCode;
	protected java.lang.String program;
/**
 * WSPrgmCallException constructor comment.
 */
public WSPgrmCallException() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (15-11-2000 04:33:26 PM)
 * @param pgrm java.lang.String
 */
public WSPgrmCallException(String pgrm) {
	super();
	this.program = pgrm;
}
/**
 * Insert the method's description here.
 * Creation date: (15-11-2000 04:14:50 PM)
 * @return int
 */
public int getErrorCode() {
	return errorCode;
}
/**
 * Insert the method's description here.
 * Creation date: (15-11-2000 04:15:31 PM)
 * @return java.lang.String
 */
public java.lang.String getProgram() {
	return program;
}
/**
 * Insert the method's description here.
 * Creation date: (15-11-2000 04:14:50 PM)
 * @param newErrorCode int
 */
public void setErrorCode(int newErrorCode) {
	errorCode = newErrorCode;
}
/**
 * Insert the method's description here.
 * Creation date: (15-11-2000 04:15:31 PM)
 * @param newProgram java.lang.String
 */
public void setProgram(java.lang.String newProgram) {
	program = newProgram;
}
}
