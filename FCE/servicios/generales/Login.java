package servicios.generales;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Inserta aquí la descripción del tipo.
 * Fecha de creación: (13-11-2000 05:13:15 PM)
 * @author: Leonardo Pino
 * 08-08-2003:	Agrega objeto ServletConfig a la sesión si el usuario se loguea
 */
public class Login implements servicios.wsapi.ETCBeanInterface {
	protected jakarta.servlet.http.HttpServletRequest request;
	protected jakarta.servlet.http.HttpSession session;
	protected jakarta.servlet.ServletConfig servletConfig;
	public static final String FS = System.getProperty("file.separator");

/**
 * Comentario del constructor Login.
 */
public Login() {
	super();
}

/**
 * Comentario del método execute.
 */
@Override
public void execute() throws servicios.generales.WSException{
	try {
		Integer intentos;
		Map<String, Integer> intentosUsuarios;
		Integer estadoSesion;
		setSession(getRequest().getSession());
		//Si no hay sesión crea una nueva, si la sesión es nueva la convierte a antigua
		if (getSession().isNew()) { 						
			//Si la SESIÓN es nueva
			intentos = 0;
			intentosUsuarios = new HashMap<>();
			//Son 0 intentos
			estadoSesion = 0;
			String referencia = "";
			session.setAttribute("refNum", referencia);
		}
		else { 												
			//Si la SESIÓN es antigua	
			//Rescato el número de intentos que el usuario ya posee
			intentos = (Integer) session.getAttribute("tries"); 
			intentosUsuarios = (Map<String, Integer>) session.getAttribute("usersTries");
			if (intentos == null)
				intentos = 0;
			if (intentosUsuarios == null)
				intentosUsuarios = new HashMap<>();
			if (intentos > 0) {
				estadoSesion = -1;
			}
			else {
				estadoSesion = 0;
			}
		}
		
		//Coloca valores para verificar el número de intentos de login
		session.setAttribute("tries", intentos);
		session.setAttribute("usersTries", intentosUsuarios);
		session.setAttribute("estado", estadoSesion);
		
		//se copia la configuración del servlet controlador para que sea usada por el resto del sistema 
		//si se necesita
		ServletContext contexto = this.servletConfig.getServletContext();
		String rutaReal = contexto.getRealPath("") + FS;
		//Elimina esa dependencia dado que solo funciona para ambientes Windows
		session.setAttribute("configuracion", rutaReal);
		//Se copia la lista de pools de conexiones a la(s) BD(s) si se requiere usar en
		//los objetos de negocio
		session.setAttribute("pools", getRequest().getAttribute("pools"));
		//Crea la lista de parámetros propios de la aplicación y los coloca en la sesión
		if(this.servletConfig.getInitParameter("parametros") != null)
			session.setAttribute("params", getParametrosAplicacion(contexto.getRealPath(this.servletConfig.getInitParameter("parametros"))));
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error en la configuración de la sesión");
	}
}

/**
 * Inserta aquí la descripción del método.
 * Fecha de creación: (13-11-2000 05:26:32 PM)
 * @return javax.servlet.http.HttpServletRequest
 */
public jakarta.servlet.http.HttpServletRequest getRequest() {
	return request;
}

/**
 * Comentario del método getResult.
 */
@Override
public Object getResult() {
	return null;
}

/**
 * Inserta aquí la descripción del método.
 * Fecha de creación: (14-11-2000 03:04:58 PM)
 * @return javax.servlet.http.HttpSession
 */
public jakarta.servlet.http.HttpSession getSession() {
	return session;
}

/**
 * Comentario del método init.
 */
@Override
public void init() throws servicios.generales.WSException{}

/**
 * Comentario del método init.
 */
@Override
public void init(Object parametros) {
	this.servletConfig = (jakarta.servlet.ServletConfig)parametros;
}

/**
 * Comentario del método setContext.
 */
@Override
public void setContext(HttpServletRequest req) {
	setRequest(req);
}

/**
 * Comentario del método setParameters.
 */
@Override
public void setParameters(Object request) {
	//No configuro ningún parámetro
}

/**
 * Inserta aquí la descripción del método.
 * Fecha de creación: (13-11-2000 05:26:32 PM)
 * @param newRequest javax.servlet.http.HttpServletRequest
 */
public void setRequest(jakarta.servlet.http.HttpServletRequest newRequest) {
	request = newRequest;
}

/**
 * Inserta aquí la descripción del método.
 * Fecha de creación: (14-11-2000 03:04:58 PM)
 * @param newSession javax.servlet.http.HttpSession
 */
public void setSession(jakarta.servlet.http.HttpSession newSession) {
	session = newSession;
}

/**
 * Inserta aquí la descripción del método.
 * Fecha de creación: (14-11-2000 03:04:58 PM)
 * @param archivo ruta del archivo de propiedades
 */
private Properties getParametrosAplicacion(String archivo){
	var parametros = new Properties();
	try (var inputStream = new FileInputStream(archivo)) {
		parametros.load(inputStream);
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	catch (IOException e) {
		e.printStackTrace();
	} 
	return parametros;
}
}