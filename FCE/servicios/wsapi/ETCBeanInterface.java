package servicios.wsapi;

/**
 * Insert the type's description here.
 * Creation date: (13-11-2000 04:06:22 PM)
 * @author: Leonardo Pino
 * 26/03/2001	Se agrega setContext para capturar el request y
 *						se cambia el parametro de setParameters para
 *						entregar el objeto que provenga desde el archivo XML
 */
public interface ETCBeanInterface {
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 04:07:25 PM)
 */
void execute() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (05-04-2001 11:01:29 AM)
 * @return java.lang.Object
 */
Object getResult();
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 04:09:41 PM)
 */
void init() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (02-03-2001 11:43:32 AM)
 * @param parametros java.lang.Object
 */
void init(Object parametros);
/**
 * Insert the method's description here.
 * Creation date: (26-03-2001 09:32:46 PM)
 * @param req jakarta.servlet.http.HttpServletRequest
 */
void setContext(jakarta.servlet.http.HttpServletRequest req);
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 04:11:31 PM)
 * @param request jakarta.servlet.http.HttpServletRequest
 * @param session jakarta.servlet.http.HttpSession
 */
void setParameters(Object request);
}
