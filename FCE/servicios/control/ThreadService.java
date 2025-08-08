/*
 * Created on Aug 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package servicios.control;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import servicios.generales.WSException;
import servicios.generales.xsl.XslXml2Html;
import servicios.generales.xsl.XslXml2PDF;
import servicios.wsapi.FceObjectInterface;
import servicios.wsapi.FceSecurityInterface;

/**
 * @author lpino
 *	Fecha: Aug 9, 2004
 * 
 */
public class ThreadService {
	public ServiceMgr data;
	private String paginaRetorno = "";
	private HttpServletResponse res = null;
	private Object boResponse = null;
	public ThreadService() {
		super();
	}
	public ThreadService(ServiceMgr values) {
		data = values;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05-01-2001 06:05:43 PM)
	 * @param requets jakarta.servlet.http.HttpServletRequest
	 * @param program servicios.control.ProgRequest
	 * 28-05-2001:	Coloca lo necesario para que una key que sea "sgte" invoque al siguiente servicio
	 *							que se encuentra definido en el archivo XML
	 * 30-09-2003:	Coloca contextos en la salida de logs, asociados con el numero de referencia que existe en la sesion
	 * 30-09-2003:	Todos los mensajes de este kernel son de tipo DEBUG
	 * 10-03-2004:	Imprime el stack al arrojar un error en algun objeto de negocios
	 * 23-03-2005:	Elimina el codigo para identificar el thread
	 * 17-10-2005:	Incorpora el manejo del objeto Response y lo traspasa a los nuevos objetos de negocio
	 * 28-12-2005: Implementa la totalidad de la nueva interface FceObjectInterface
	 * 26-12-2012: La carga dinamica de clases se hace utilizando el classloader asociado al thread
	 */
	public void cicloServicios(jakarta.servlet.http.HttpServletRequest req, servicios.control.ProgRequest programa) throws Exception{
		//Inicializa el classloader asociado a este thread que es el que corresponde a la aplicacion que realiza el request
		Thread t = Thread.currentThread();
		ClassLoader cl = t.getContextClassLoader();
		try {
			String key;
			String lastkey = "";
			java.util.Properties servicios = programa.getListServices();
			servicios.control.Service servicio;
			key = programa.getInitService();
			while (key != null) {
				//Si se trata de continuar con el siguiente servicio
				if (key.equalsIgnoreCase("sgte"))
					//Recorre la lista de keys hasta la ultima que se ejecuto y retorna la que sigue en la definicion
					//hecha en la lista de servicios en el XML
					key = programa.getNextService(lastkey);
				data.logs.debug("0.5  El servicio a usarse es: " + key + " El servicio anterior fue: " + lastkey);
				data.logs.debug("Invocando servicio " + key);
				lastkey = key;
				servicio = (servicios.control.Service) servicios.get(key);
				if(servicio == null)
					throw new WSException("Clase: ThreadService - ERROR: Servicio no existe - MSG: Servicio " + key + " no existe en XML para el programa " + programa.getNombre());
				servicios.wsapi.CtrlServiceInterface service = null;
				service = (servicios.wsapi.CtrlServiceInterface) Class.forName("servicios." + servicio.getServicio()).newInstance();
				data.logs.debug("1. Clase " + servicio.getServicio() + " instaciada");
				service.init(data.getParams());
				data.logs.debug("2. Inicializacion de la clase " + servicio.getServicio() + "  completa");
				service.setParameters(req, servicio);
				data.logs.debug("3. Configuracion de parametros de la clase " + servicio.getServicio() + "  completa");
				service.execute();
				data.logs.debug("4. Ejecucion del servicio " + servicio.getServicio() + " del programa " + programa.getNombre() + ",  completa");
				if (servicio.getClaseNegocios().equalsIgnoreCase("sbo"))
					key = null;
				else {
					servicios.wsapi.BusinessObjectInterface businessObject = null;
					try {
						data.logs.debug("4.1.  Instanciando la clase " + servicio.getClaseNegocios() + " del servicio " + servicio.getServicio());
						businessObject = (servicios.wsapi.BusinessObjectInterface) cl.loadClass(servicio.getClaseNegocios()).newInstance();
						data.logs.debug("4.2. Instanciacion del objeto de negocios " + servicio.getClaseNegocios() + " del servicio " + servicio.getServicio());
						data.logs.debug("Usando clase de negocios: " + servicio.getClaseNegocios());
						//Los siguiente se ejecuta solo si el objeto de negocios extiende la clase BusinessObjectClass
						if(businessObject instanceof servicios.wsapi.BusinessObjectClass){
							((servicios.wsapi.BusinessObjectClass)businessObject).setThreadParams(req,res);
							((servicios.wsapi.BusinessObjectClass)businessObject).setJspAkaPage(programa.getJspTocall());
						}
						
						businessObject.getContext(req);							

						//Si se trata de la nueva interface ejecuta sus metodos
						if(businessObject instanceof FceObjectInterface){
							data.logs.debug("4.3.0 Objeto de negocios con nueva interface FCE " + servicio.getClaseNegocios());
							//Inicia la clase con variables que vengan en el request
							((servicios.wsapi.FceObjectInterface)businessObject).init(req,res);
							//Configura parametros dinamicos a partir del resultado del servicio
							((servicios.wsapi.FceObjectInterface)businessObject).setParameters(service.getResultado());
							//Ejecuta el BO
							((servicios.wsapi.FceObjectInterface)businessObject).execute();
							//Se retorna un resultado si es necesario
							Object salida = ((servicios.wsapi.FceObjectInterface)businessObject).getObjectResult();
							//Si se retorno el resultado este queda en el thread para su uso. Solo existe un resultado. Si otro BO reescribe
							//Solo el ultimo valor permanece
							if(salida != null){
								data.logs.debug("4.3.1. Objeto de resultado retornado por el BO");
								this.setBoResponse(salida);
							}
						}
						//Si es una clase con BusinessObjectInterface se ejecutan sus metodos
						else{
							businessObject.init();
							businessObject.getServiceResult(service.getResultado());							
						}
						key = businessObject.nextService();
						data.logs.debug("Llave retornada por el objeto de Negocios = " + key);
						if (key == null || (!businessObject.getJspAkaPage().equalsIgnoreCase("") && businessObject.getJspAkaPage() != null))
							//Si se devuelve una llave nula pero sin JSP distinto(vacio o nulo), no se cambia el JSP de salida
							if(!businessObject.getJspAkaPage().equalsIgnoreCase("")){
								data.logs.debug("Se configura el JSP " + businessObject.getJspAkaPage() + " para ser llamado. La clase de negocios es  " + servicio.getClaseNegocios() );
								paginaRetorno = businessObject.getJspAkaPage();
							}
					}
					catch (Exception e) {
						if (e instanceof servicios.generales.WSPgrmCallException) {
							String salida = programExec(req, ((servicios.generales.WSPgrmCallException) e).getProgram());
							data.logs.debug("Despues de una llamada recursiva se establece el JSP " + salida + " para llamarlo");
							paginaRetorno = salida;
							key = null;
						}
						else{
							if (e instanceof servicios.generales.WSException)
								throw e;
							else
								if (e instanceof java.lang.ClassNotFoundException){
									key = servicio.getClaseNegocios();
								}
								else{
									e.printStackTrace();
									throw e;
								}
						}
					}
					finally{
						businessObject = null;
					}
				}
				req.setAttribute(servicio.getBeanSalida(), service.getResultado());
				data.logs.debug("Se agrego el bean de salida llamado " + servicio.getBeanSalida());
			}
		}
		catch(Exception e){
			if(programa.getObjetoControl() == null)
				throw e;
			else{
				servicios.wsapi.FceObjectInterface businessObject = null;
				try {
					data.logs.debug("5.1.  Instanciando la clase " + programa.getObjetoControl() + " del programa " + programa.getNombre());
					businessObject = (servicios.wsapi.FceObjectInterface) cl.loadClass(programa.getObjetoControl()).newInstance();
					data.logs.debug("5.2. Instanciacion del objeto de negocios " + programa.getObjetoControl() + " del programa " + programa.getNombre());
					data.logs.debug("Usando clase de negocios: " + programa.getObjetoControl());
					businessObject.init();
					businessObject.getContext(req);
					//Usa la excepcion como variable para poder hacer algo interno con ella
					businessObject.setParameters(e);
					businessObject.execute();
					if ((!businessObject.getJspAkaPage().equalsIgnoreCase("") && businessObject.getJspAkaPage() != null))
						//Si se devuelve una llave nula pero sin JSP distinto(vacio o nulo), no se cambia el JSP de salida
						if(!businessObject.getJspAkaPage().equalsIgnoreCase("")){
							data.logs.debug("Se configura el JSP " + businessObject.getJspAkaPage() + " para ser llamado. La clase de negocios es de control de errores ");
							paginaRetorno = businessObject.getJspAkaPage();
						}
				}
				catch (Exception e1) {
					if (e instanceof servicios.generales.WSPgrmCallException) {
						String salida = programExec(req, ((servicios.generales.WSPgrmCallException) e1).getProgram());
						data.logs.debug("Despues de una llamada recursiva se establece el JSP " + salida + " para llamarlo");
						paginaRetorno = salida;
					}
					else{
						if (e instanceof servicios.generales.WSException)
							throw e1;
						else{
							e1.printStackTrace();
							throw e1;
						}
					}
				}
				finally{
					businessObject = null;
				}
			}
		}
		finally{
			t = null;
			cl = null;
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (13-11-2000 02:03:14 PM)
	 * @return java.lang.String
	 * 	Retorna el ALIAS de la pagina JSP de salida de este programa
	 * 05-01-2001:	Modifica la logica para soportar la paginaci�n
	 * 11-01-2001:	Agrega el bean de paginaci�n
	 * 11-02-2001:	Correccion de la asignaci�n de pagina actual
	 * 14-02-2001:	Logica para evitar usar el mismo paginador
	 * 07-03-2001:	Modifica la forma en que registra el log de los errores
	 * 23-04-2001:	Corrige la instanciaci�n de la clase ProgRequest para evitar errores de concurrencia
	 * 09-09-2003:	Permite que el nombre del programa venga como atributo del request
	 * 07-01-2004:	Simplifica logs
	 * 19-03-2004:	Elimina el uso del metodo getParameter y ahora solo usa el que provee el request
	 * 18-03-2004:	Errores desconocidos no se convierten a WSException, evitando perder el stack real del error
	 * 04-08-2004:	En la paginaci�n evita generar la sesion todo el tiempo y recicla la ya existente
	 * 23-03-2005:	Saca el codigo de registro de identificar el thread para este metodo
	 * 						Registra la sesion que se invalida
	 * 11-07-2005:	Cambia la forma de retornar el alias a la pagina siguiente. Ya no usa directamente
	 * 					el atributo del programa si no que una variable
	 * 15-07-2005:	Agrega revisi�n de seguridad via la nueva Interface de seguridad
	 * 10-12-2006:	Incorpora soporte para utilizar Hibernate directamente
	 * 18-12-2006:	Valida el uso de Hibernate
	 */
	protected String programExec(jakarta.servlet.http.HttpServletRequest req, String programName) throws servicios.generales.WSException {
		try {
			Object id_thread;
			//Si aun no hay sesion se marca de acuerdo a esto
			if(req.getSession(false) == null)
				id_thread = "SIN SESION";
			else
				//Si la sesion existe entonces se extrae algun valor desde la sesion
				//desde el atributo refNUm. Es responsabilidad del programador
				//setear este valor en algun aobjeto de negocios
				id_thread =req.getSession(false).getAttribute("refNum");
			if(id_thread == null)
				id_thread = req.getSession(false).getId();
			else if(id_thread.toString().equalsIgnoreCase(""))
				id_thread = req.getSession(false).getId();
			//Coloca el numero de referencia como cotexto
			data.logs.logPush(id_thread.toString());
			servicios.generales.BeanPaginacion paginator = null;
			jakarta.servlet.http.HttpSession session = null;
			servicios.control.ProgRequest programa = null;
			if (programName == null)
				programName = req.getParameter("reqName");
			if (programName == null)
				programName = (String)req.getAttribute("reqName");
			programa = data.getProgramLoader().getPrograma(programName);
			if(programa == null)
				throw new WSException("Clase: ThreadService Error: Programa " + programName + " no exite en el XML");
			//Si el programa no invalida sesion entonces marco la sesion como no eliminable
			if(!programa.invalidaSesion()){
				//Si la marca ya existe porque el programa anterior tampoco invalidaba, no hace nada y deja la marca
				if(req.getSession(false)!=null && req.getSession(false).getAttribute("flagInvalidacion") == null)
					req.getSession(false).setAttribute("flagInvalidacion",new Integer(0));
			}
			//Si el programa invalida comprueba si existe la marca para removerla
			else{
				if(req.getSession(false)!=null && req.getSession(false).getAttribute("flagInvalidacion") != null)
					req.getSession(false).removeAttribute("flagInvalidacion");
			}
			data.logs.debug("0. Extraccion del programa " + programName + " exitosa");
			//Extrae la pagina de retorno al iniciar el manejo del nuevo programa siempre y cuando la salida se trate de una pagina
			if(req.getParameter("fmtSalida") != null && (req.getParameter("fmtSalida").equalsIgnoreCase("sf") || req.getParameter("fmtSalida").equalsIgnoreCase("xls")))
				paginaRetorno = "directo";
			else
				paginaRetorno = programa.getJspTocall();
			//Revisa si posee objeto de seguridad
			if(programa.getObjetoSeguridad() != null){
				FceSecurityInterface securityObject = null;
				data.logs.debug("0.1.  Instanciando la clase " + programa.getObjetoSeguridad() + " del programa " + programa.getNombre());
				securityObject = (FceSecurityInterface) Class.forName(programa.getObjetoSeguridad()).newInstance();
				data.logs.debug("0.2. Instanciacion del objeto de negocios " + programa.getObjetoSeguridad()+ " del programa " + programa.getNombre());
				data.logs.debug("Usando clase de seguridad: " + programa.getObjetoSeguridad());
				securityObject.setData(req);
				//Si el programa no es seguro entonces cambia la pantalla de error y registra la informaci�n en un log
				if(!securityObject.isProgramSecure()){
					securityObject.writeLog();
					WSException e = new WSException("VIOLACION DE SEGURIDAD!");
					e.setErrorCode(securityObject.getErrorCode());
					throw e;
				}
			}
			data.logs.debug("Ejecutando programa "+ programName);
			if (!programa.generaPaginas()) {
				cicloServicios(req, programa);
			}
			//Si el programa pagina
			else {
				session = req.getSession();
				//Si aun no tiene objeto paginador lo crea y lo pone en la sesion
				if (session.getAttribute("paginador") == null) {
					cicloServicios(req,programa);
					paginator = new servicios.generales.BeanPaginacion();
					//setea lo valores iniciales necesarios para el paginador
					paginator.init(req, programa.datosPaginacion);
					//coloca el paginador en la session
					session.setAttribute("paginador", paginator);
				}
				//Si ya existe un objeto paginador en la sesion
				else {
					//rescata el objeto paginador desde la sesion
					paginator = (servicios.generales.BeanPaginacion) session.getAttribute("paginador");
					//Si el programa es otro crea otro paginador
					if (!paginator.getPrgName().equalsIgnoreCase(req.getParameter("reqName")) || !paginator.mismosParams(req)) {
						cicloServicios(req,programa);
						paginator = new servicios.generales.BeanPaginacion();
						//setea lo valores iniciales necesarios para el paginador
						paginator.init(req, programa.datosPaginacion);
						//coloca el paginador en la session reemplazando al anterior
						session.setAttribute("paginador", paginator);
					}
					//Si el programa es el mismo
					else {
						//Si en el request no viene la pagina a mostrar se usa la primera pagina
						if (req.getParameter("pagina") == null)
							paginator.setPaginaActual(0);
						else						
							//setea los parametros que trae el request
							paginator.setPaginaActual(Integer.parseInt(req.getParameter("pagina")));
					}
				}
				//recoloca el bean de salida en el request con los valores de la pagina que corresponde
				req.setAttribute(paginator.getBeanName(), paginator.getOutputBean());
			}
			data.logs.debug("5. Se retorna el JSP " + paginaRetorno + " a nombre del programa " + programa.getNombre());
			//Extrae el contexto 
			data.logs.logPop();
			return paginaRetorno;
		}
		catch (Exception e) {
			return data.errorManager(e,req,res);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11-09-2000 11:26:10 AM)
	 * Autor: Leonardo Pino
	 * @param req jakarta.servlet.RequestDispatcher
	 * 26-12-2005:	Verifica el resultado de la ejecuci�n y ejecuta el procesamiento de XSL si es necesario
	 * 04-01-2006: Completa el c�digo para parsear los XSL y convertirlos en HTML, ademas de agregar el control de errores
	 * 06-07-2006:	Verifica la existencia de la lista de XSL antes de revisar la lista. Esto evita errores con versiones viejas de FCE
	 * 09-01-2007:	Completa el manejo de la sesion de hibernate. Agrega mensajes de debug para verificar estado de la sesion
	 * 26-07-2012: Incorpora los formatos de salida HTML y JSON
	 */
	public String execute(jakarta.servlet.http.HttpServletRequest req) throws servicios.generales.WSException {
		String aux = programExec(req, null);
		String salida = "";
		if(req.getParameter("fmtSalida") !=  null && req.getAttribute("fmtSalida") == null)
			salida = req.getParameter("fmtSalida");
		else if(req.getAttribute("fmtSalida") != null)
			salida = (String)req.getAttribute("fmtSalida");
			
		//Si la salida es formateada por XSL (verifica primero que tenga lista de XSLs) y la pagina existe entonces se procesa la informaci�n XML
		//las fuentes del XML puede ser cualquier objeto dentro del request
		if(data.getListaXsl() != null && data.getListaXsl().containsKey(aux) && (salida.equalsIgnoreCase("xsl") || salida.equalsIgnoreCase("pdf"))){
			//Procesa el XSL y lo transforma en HTML
			data.logs.debug("Procesando archivo XML");
			if(this.data.getListaXsl().get(aux) == null){
				throw new WSException("Clase: ThreadService - Error: No existe XSL " + aux + "  en la lista de p�ginas del pagemapping.xml");
			}
			if(this.getBoResponse() != null){
				if(salida.equalsIgnoreCase("xsl")){
					XslXml2Html parser = new XslXml2Html();
					parser.setDocXsl((Document)(this.data.getListaXsl().get(aux)));
					parser.setDocXml((Document)getBoResponse());
					parser.transform();
					parser.retornaSalida(res);
				}else if(salida.equalsIgnoreCase("pdf")){
					XslXml2PDF parser = new XslXml2PDF();
					parser.setDocXsl((Document)(this.data.getListaXsl().get(aux)));
					parser.setDocXml((Document)getBoResponse());
					parser.transform();
					parser.retornaSalida(res);					
				}else{
					throw new WSException("Clase: ThreadService - Error: El parametro de fmtSalida tiene un valor desconocido");
				}
			}
			else{
				throw new WSException("Clase: ThreadService - Error: No existe XML para procesar el XSL");
			}
		} else if(salida.equalsIgnoreCase("html") ){
			try {
				res.setContentType("text/html; charset=UTF-8");
				res.setCharacterEncoding("UTF-8");
				res.getWriter().write((String)this.getBoResponse());
				res.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new WSException("Clase: ThreadService - Error: No se pudo escribir la salida HTML en el objeto Response");
			}
		} else if(salida.equalsIgnoreCase("json")){
	    	try {
				res.setContentType("application/json");
				res.setCharacterEncoding("UTF-8");
				res.getWriter().write((String)this.getBoResponse());
				res.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new WSException("Clase: ThreadService - Error: No se pudo escribir la salida JSON en el objeto Response");
			}
		}
		return aux;
	}
	public HttpServletResponse getRes() {
		return res;
	}
	public void setRes(HttpServletResponse res) {
		this.res = res;
	}
	public Object getBoResponse() {
		return boResponse;
	}
	public void setBoResponse(Object boResponse) {
		this.boResponse = boResponse;
	}
}
