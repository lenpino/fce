package servicios.wsapi;

/**
 * Insert the type's description here.
 * Creation date: (08-09-2000 04:20:23 PM)
 * @author: Leonardo Pino
 */
public interface CtrlServiceInterface {
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 11:47:00 AM)
 */
void execute() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (22-09-2000 10:44:36 AM)
 * @return servicios.basedatos.ResultSet
 * 05-01-2001:	Cambia el tipo de retorno a Object para hacerlo mas generico
 */
Object getResultado();
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 11:47:58 AM)
 */
void init(Object datos) throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11-09-2000 11:43:29 AM)
 * @param request jakarta.servlet.http.HttpServletRequest
 * @param session jakarta.servlet.http.HttpSession
 */
void setParameters(jakarta.servlet.http.HttpServletRequest request, servicios.control.Service servicio) throws servicios.generales.WSException;
}
