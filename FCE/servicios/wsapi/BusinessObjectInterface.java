package servicios.wsapi;

/**
 * Insert the type's description here.
 * Creation date: (02-10-2000 12:44:13 PM)
 * @author: Leonardo Pino
 */
public interface BusinessObjectInterface {
/**
 * Insert the method's description here.
 * Creation date: (30-10-2000 05:35:45 PM)
 * @param request javax.servlet.http.HttpServletRequest
 * @param session javax.servlet.http.HttpSession
 */
void getContext(javax.servlet.http.HttpServletRequest request);
/**
 * Insert the method's description here.
 * Creation date: (20-10-2000 10:03:28 AM)
 * @return java.lang.String
 */
String getJspAkaPage();
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 01:03:18 PM)
 */
void getServiceResult();
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 01:35:42 PM)
 * @param resultado servicios.basedatos.ResultSetService
 * 05-01-2001:	Cambia el parametro por Object para hacerlo mas generico
 */
void getServiceResult(Object resultado) throws servicios.generales.WSException, servicios.generales.WSPgrmCallException;
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:45:03 PM)
 */
void init();
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:57:30 PM)
 * @return java.lang.String
 */
String nextService();
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:45:29 PM)
 */
void setParameter();
}
