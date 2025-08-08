package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (23-11-2000 12:46:46 PM)
 * @author: Leonardo Pino
 */
public class Logoff implements servicios.wsapi.ETCBeanInterface {
	protected jakarta.servlet.http.HttpServletRequest copyRequest;
/**
 * Logoff constructor comment.
 */
public Logoff() {
	super();
}
/**
 * execute method comment.
 */
//05/02/2001: Agrego el seteo del estado a 1 esto para evitar que la session
//			se considere viva y valida por los servicios. Esto debiera reparar el
//			comportamiento de la desconexion.

@Override
public void execute() throws servicios.generales.WSException{
	try {
		jakarta.servlet.http.HttpSession session = copyRequest.getSession();
		Integer sessionState = new Integer(1);
		session.setAttribute("estado", sessionState);
		session.invalidate();
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error en el logoff");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (23-11-2000 12:48:37 PM)
 * @return jakarta.servlet.http.HttpServletRequest
 */
public jakarta.servlet.http.HttpServletRequest getRequest() {
	return copyRequest;
}
/**
 * getResult method comment.
 */
@Override
public java.lang.Object getResult() {
	return null;
}
/**
 * init method comment.
 */
@Override
public void init() throws servicios.generales.WSException {}
/**
 * init method comment.
 */
@Override
public void init(java.lang.Object parametros) {}
/**
 * setContext method comment.
 */
@Override
public void setContext(jakarta.servlet.http.HttpServletRequest req) {
	setRequest(req);
}
/**
 * setParameters method comment.
 */
@Override
public void setParameters(java.lang.Object request) {}
/**
 * Insert the method's description here.
 * Creation date: (23-11-2000 12:48:37 PM)
 * @param newRequest jakarta.servlet.http.HttpServletRequest
 */
public void setRequest(jakarta.servlet.http.HttpServletRequest newRequest) {
	copyRequest = newRequest;
}
}
