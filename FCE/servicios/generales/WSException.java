package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (11-09-2000 12:10:27 PM)
 * @author: Leonardo Pino
 */
public class WSException extends Exception {
	private int errorCode;
/**
 * WSException constructor comment.
 */
public WSException() {
	super();
}
/**
 * WSException constructor comment.
 * @param s java.lang.String
 */
public WSException(String s) {
	super(s);
}
/**
 * Insert the method's description here.
 * Creation date: (18-10-2000 10:39:31 AM)
 * @return int
 */
public int getErrorCode() {
	return errorCode;
}
/**
 * Insert the method's description here.
 * Creation date: (18-10-2000 10:39:31 AM)
 * @param newErrorCode int
 */
public void setErrorCode(int newErrorCode) {
	errorCode = newErrorCode;
}
}
