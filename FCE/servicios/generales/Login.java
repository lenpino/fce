package servicios.generales;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * Insert the type's description here.
 * Creation date: (13-11-2000 05:13:15 PM)
 * @author: Leonardo Pino
 * 08-08-2003:	Agrega objeto ServletConfig a la sesion si el usuario se logea
 */
public class Login implements servicios.wsapi.ETCBeanInterface {
	protected javax.servlet.http.HttpServletRequest request;
	protected javax.servlet.http.HttpSession session;
	protected javax.servlet.ServletConfig servletConfig;
	public final static String FS = System.getProperty("file.separator");
/**
 * Login constructor comment.
 */
public Login() {
	super();
}
/**
 * execute method comment.
 */
@Override
public void execute() throws servicios.generales.WSException{
	try {
		Integer intentos = null;
		HashMap usersTries = null;
		Integer sessionState = null;
		setSession(getRequest().getSession());
		//Si no hay sesion crea una vieja, si la sesion es nueva la convierte a vieja
		if (getSession().isNew()) { 						
			//Si la SESSION es nueva
			intentos = new Integer(0);
			usersTries = new HashMap();
			//Son 0 intentos
			sessionState = new Integer(0);
			String mula = "";
			session.setAttribute("refNum", mula);
		}
		else { 												
			//Si la SESSION es vieja	
			//Rescato el numero de intentos que el usuario ya posee
			intentos = (Integer) session.getAttribute("tries"); 
			usersTries =(HashMap) session.getAttribute("usersTries");
			if (intentos == null)
				intentos = new Integer(0);
			if (usersTries==null)
				usersTries = new HashMap();
			if (intentos.intValue() > 0) {
				sessionState = new Integer(-1);
			}
			else {
				sessionState = new Integer(0);
			}
		}
		
		//Coloca valores para verificar el numero de intentos de login
		session.setAttribute("tries", intentos);
		session.setAttribute("usersTries", usersTries);
		session.setAttribute("estado", sessionState);
		
		//se copia la configuracion del servlet controlador para que sea usada por el resto del sistema 
		//si se necesita
		ServletContext contexto = this.servletConfig.getServletContext();
		String pathReal = contexto.getRealPath("") + FS;
		//Elimina esa dependencia dado que solo funciona para ambientes Windows
		session.setAttribute("configuracion",pathReal);
		//Se copia la lista de pools de conexiones a la(s) BD(s) si se requiere usar en
		//los objetos de negocio
		session.setAttribute("pools", getRequest().getAttribute("pools"));
		//Crea la lista de parametros propios de la aplicación y los coloca en la sesion
		if(this.servletConfig.getInitParameter("parametros") != null)
			session.setAttribute("params", getAppParams(contexto.getRealPath(this.servletConfig.getInitParameter("parametros"))));
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error en la configuracion de la sesion");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:26:32 PM)
 * @return javax.servlet.http.HttpServletRequest
 */
public javax.servlet.http.HttpServletRequest getRequest() {
	return request;
}
/**
 * getResult method comment.
 */
@Override
public java.lang.Object getResult() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (14-11-2000 03:04:58 PM)
 * @return javax.servlet.http.HttpSession
 */
public javax.servlet.http.HttpSession getSession() {
	return session;
}
/**
 * init method comment.
 */
@Override
public void init() throws servicios.generales.WSException{}
/**
 * init method comment.
 */
@Override
public void init(java.lang.Object parametros) {
	this.servletConfig = (javax.servlet.ServletConfig)parametros;
}
/**
 * setContext method comment.
 */
@Override
public void setContext(javax.servlet.http.HttpServletRequest req) {
	setRequest(req);
}
/**
 * setParameters method comment.
 */
@Override
public void setParameters(java.lang.Object request) {
	//No configuro ningun parametro
}
/**
 * Insert the method's description here.
 * Creation date: (13-11-2000 05:26:32 PM)
 * @param newRequest javax.servlet.http.HttpServletRequest
 */
public void setRequest(javax.servlet.http.HttpServletRequest newRequest) {
	request = newRequest;
}
/**
 * Insert the method's description here.
 * Creation date: (14-11-2000 03:04:58 PM)
 * @param newSession javax.servlet.http.HttpSession
 */
public void setSession(javax.servlet.http.HttpSession newSession) {
	session = newSession;
}
/**
 * * Insert the method's description here.
* Creation date: (14-11-2000 03:04:58 PM)
* @param newSession javax.servlet.http.HttpSession
*/
	private Properties getAppParams(String file){
		Properties params = new Properties();
		try {
			params.load(new FileInputStream(file));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		return params;
	}
}
