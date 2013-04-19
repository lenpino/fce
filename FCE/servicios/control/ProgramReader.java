package servicios.control;

/**
 * Insert the type's description here.
 * Creation date: (13-09-2000 11:42:19 AM)
 * @author: Leonardo Pino
 * 11-01-2001		Agregar codigo en recorreArbol para leer la informacion de la
 * 						paginacion desde el XML del programa
 * 11-04-2001		Modificar recorreArbol para cambiar lectura de indp por la
 *							del modo de operacion del servicio
 * 23-05-2001:	Agrega los metodos para crear la lista de pools de conecciones
 * 24-05-2001:	Agrega los metodos para configurar el trace
 * 25-05-2001:	Agrega los metodos para extraer una lista de conecciones a distintos QMgrs
 * 06-08-2001:	Corrige error al leer el programa con el nuevo DTD
 * 10-09-2001:	Corrige la asignación del nombre del ID de la conexion de Base de Datos
 * 23-04-2002:	Metodo getPoolVersion es eliminado, y se cambian las clases de parsing de XML
 */
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import servicios.basedatos.PoolWas30;
import servicios.generales.WSException;

public class ProgramReader extends servicios.generales.XmlBean {
	protected ProgRequest programa;
	protected java.util.Properties xmlFiles;
	protected java.util.Properties todosErrores;
	private org.w3c.dom.Node nodoFlag = null;
	protected HashMap todosProgramas = new HashMap();
	/**
	 * MgrCons constructor comment.
	 */
	public ProgramReader() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (13-09-2000 10:31:24 AM)
	 */
	protected void buildProgram(Node root) throws servicios.generales.WSException {
		setAtributosPrg(root);
		setServiciosPrg(root);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (15-09-2000 10:34:45 AM)
	 * @return org.w3c.dom.Node
	 * @param programa java.lang.String
	 */
/*	private Node findProgram(String programa) throws servicios.generales.WSException {
		Node raiz = find(programa);
		this.programa = new ProgRequest();
		this.programa.setNombre(programa);
		return raiz;
	}
*/	/**
	 * Insert the method's description here.
	 * Creation date: (22-09-2000 11:54:26 AM)
	 * @return org.w3c.dom.Node
	 */
	private Node findTodosErrores() {
		Element root = getDoc().getDocumentElement();
		if (root.hasChildNodes()) {
			NodeList nList = root.getChildNodes();
			int numHijos = nList.getLength();
			for (int i = 0; i < numHijos; i++)
				if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("errores"))
					return nList.item(i);
		}
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-09-2000 11:54:26 AM)
	 * @return org.w3c.dom.Node
	 */
	private Node findXmlFiles() {
		Element root = getDoc().getDocumentElement();
		if (root.hasChildNodes()) {
			NodeList nList = root.getChildNodes();
			int numHijos = nList.getLength();
			for (int i = 0; i < numHijos; i++)
				if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("xmls"))
					return nList.item(i);
		}
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25-05-2001 10:26:44 AM)
	 * @return servicios.mqseries.ConectorMQ
	 * @param raiz org.w3c.dom.Node
	 */
	private servicios.mqseries.ConectorMQ getConectorMQ(Node raiz) throws servicios.generales.WSException {
		servicios.mqseries.ConectorMQ cnxMQ = new servicios.mqseries.ConectorMQ();
		try {
			servicios.mqseries.EntornoMq ambienteMQ = new servicios.mqseries.EntornoMq();
			while (raiz != null) {
				if (raiz.getNodeType() != 3) {
					if (raiz.getNodeName().equalsIgnoreCase("nombreQM")) {
						// Acepta campo sin valor para poder conectarse al QM default
						try {
							cnxMQ.setQManagerName(raiz.getFirstChild().getNodeValue());
						}
						catch (Exception e) {
							cnxMQ.setQManagerName("");
						}
					}
					if (raiz.getNodeName().equalsIgnoreCase("hostMQ"))
						ambienteMQ.setHost(raiz.getFirstChild().getNodeValue());
					if (raiz.getNodeName().equalsIgnoreCase("channelMQ"))
						ambienteMQ.setChannel(raiz.getFirstChild().getNodeValue());
					if (raiz.getNodeName().equalsIgnoreCase("portMQ"))
						ambienteMQ.setPort(raiz.getFirstChild().getNodeValue());
					if (raiz.getNodeName().equalsIgnoreCase("usuario"))
						ambienteMQ.setUserID(raiz.getFirstChild().getNodeValue());
					if (raiz.getNodeName().equalsIgnoreCase("clave"))
						ambienteMQ.setPassword(raiz.getFirstChild().getNodeValue());
				}
				raiz = raiz.getNextSibling();
			}
			cnxMQ.setEnvMq(ambienteMQ);
			cnxMQ.init();
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: ProgramReader Error: Problema al crear un pool de conecciones Msg: " + e.getMessage());
		}
		return cnxMQ;
	}
	@Override
	public Document getDoc() {
		return this.doc;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-05-2001 03:03:56 PM)
	 * @return java.lang.Object
	 * @param nodo org.w3c.dom.Node
	 * 22-04-2002:	Removido el codigo para dar soporte a WAS 2.03
	 * 23-04-2002:	Cambia la clase de JNDI para WAS 4.0
	 * 06-01-2003:	El contexto inicial se hace parametrico para mejorar la portabilidad
	 * 						el nuevo elemento del XML se llama contextclassb
	 * 13-03-2003:	Si no existe contexto inicial como parametro instancia
	 * 						el contexto sin parametros, para dar soporte a Tomcat
	 */
	private Object getPoolObject(Node nodo) throws servicios.generales.WSException {
		try {
			String poolName = "";
			String context = "";
			PoolWas30 pool30 = new PoolWas30();
			while (nodo != null) {
				if (nodo.getNodeType() != 3) {
					if (nodo.getNodeName().equalsIgnoreCase("user"))
						pool30.setUsuario(nodo.getFirstChild().getNodeValue());
					else if (nodo.getNodeName().equalsIgnoreCase("password"))
						pool30.setClave(nodo.getFirstChild().getNodeValue());
					else if (nodo.getNodeName().equalsIgnoreCase("poolname"))
						poolName = nodo.getFirstChild().getNodeValue();
					else if (nodo.getNodeName().equalsIgnoreCase("contextclass"))
						context = nodo.getFirstChild().getNodeValue();
				}
				nodo = nodo.getNextSibling();
			}
			Context ctx = null;
			Context envCtx = null;
			DataSource ds = null;
			if (context.equalsIgnoreCase("")) {
				ctx = new InitialContext();
				envCtx = (Context) ctx.lookup("java:comp/env");
				ds = (DataSource) envCtx.lookup(poolName);
			}
			else {
				java.util.Hashtable parms = new java.util.Hashtable();
				parms.put(Context.INITIAL_CONTEXT_FACTORY, context);
				ctx = new InitialContext(parms);
				ds = (DataSource) ctx.lookup(poolName);
			}
			pool30.setDs(ds);
			return pool30;
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: ProgramReader Error: Problema al crear un pool de conecciones Msg: " + e.getMessage());
		}
	}
	/**
	 * Metodo que retorna la lista de pools de conexion a la base de datos
	 * tanto de la version 2 como de la 3.
	 * Creation date: (22-05-2001 03:04:34 PM)
	 * @return java.util.Properties
	 * autor: Leonardo Pino
	 */
	public java.util.Properties getPools() throws servicios.generales.WSException {
		java.util.Properties listaPools = new java.util.Properties();
		try {
			String llave = "";
			Element root = getDoc().getDocumentElement();
			Node padre = gotoNode(root, "DBconns");
			if(padre != null){
				//Recorro la lista de hijos de este nodo
				NodeList nList = padre.getChildNodes();
				int numHijos = nList.getLength();
				for (int i = 0; i < numHijos; i++) {
					if (nList.item(i).getNodeType() != 3) {
						if (nList.item(i).getNodeName().equalsIgnoreCase("DBconn")) {
							//Extraigo el atributo del nombre para colocarlo en la lista
							Element rama = (Element) nList.item(i);
							llave = rama.getAttribute("DBconnid");
							//Coloco el pool y su nombre en la lista.
							listaPools.put(llave, getPoolObject(nList.item(i).getFirstChild()));
						}
					}
				}
			}
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: ProgramReader Error: Problema al crear la lista de pools de conecciones Msg: " + e.getMessage());
		}
		//Resetea el flag para que puedan existir otras busquedas usando gotoNode
		this.nodoFlag = null;
		return listaPools;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 10:55:38 AM)
	 * @return servicios.control.ProgRequest
	 */
	public synchronized ProgRequest getPrograma(String program) throws servicios.generales.WSException {
		if (program == null){
			servicios.generales.WSException e = new servicios.generales.WSException("Clase: ProgramReader Metodo: getPrograma Error: El programa es nulo");
			e.setErrorCode(-1);
			throw e;
		}
		return (ProgRequest)todosProgramas.get(program);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25-05-2001 10:23:30 AM)
	 * @return java.util.Properties
	 */
	public java.util.Properties getQMgrs() throws servicios.generales.WSException {
		java.util.Properties listaQMgrs = new java.util.Properties();
		try {
			String llave = "";
			Element root = getDoc().getDocumentElement();
			Node padre = gotoNode(root, "MQconns");
			//Recorro la lista de hijos de este nodo
			NodeList nList = padre.getChildNodes();
			int numHijos = nList.getLength();
			for (int i = 0; i < numHijos; i++) {
				if (nList.item(i).getNodeType() != 3) {
					if (nList.item(i).getNodeName().equalsIgnoreCase("MQconn")) {
						//Extraigo el atributo del nombre para colocarlo en la lista
						Element rama = (Element) nList.item(i);
						llave = rama.getAttribute("MQconnid");
						//Coloco el conector MQ y su nombre en la lista.
						listaQMgrs.put(llave, getConectorMQ(nList.item(i).getFirstChild()));
					}
				}
			}
		}
		catch (Exception e) {
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: ProgramReader Error: Problema al crear la lista de pools de conecciones Msg: " + e.getMessage());
		}
		//Resetea el flag para que puedan existir otras busquedas usando gotoNode
		this.nodoFlag = null;
		return listaQMgrs;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18-10-2000 02:28:36 PM)
	 * @return java.util.Properties
	 */
	public synchronized java.util.Properties getTodosErrores() throws servicios.generales.WSException {
		Element root = (Element) findTodosErrores();
		//	Element root = (Element) getDoc().getDocumentElement();
		recorreArbol(root);
		return todosErrores;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-09-2000 09:35:57 AM)
	 * @return java.util.Properties
	 */
	public synchronized java.util.Properties getXmlFiles() throws servicios.generales.WSException {
		Element root = (Element) findXmlFiles();
		recorreArbol(root);
		return xmlFiles;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-05-2001 03:02:39 PM)
	 * @return org.w3c.dom.Node
	 * @param param java.lang.String
	 */
	private Node gotoNode(Node root, String param) throws servicios.generales.WSException {
		try {
			//Verifico si continuo con la recursividad o si retorno al nivel de stack anterior
			if (nodoFlag != null)
				return nodoFlag;
			int type = root.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				//Configuro una estructura de datos con los XMLs que correspondan
				if (root.getNodeName().equalsIgnoreCase(param)) {
					//Marco el nodo para que no continue la recursividad
					nodoFlag = root;
					return nodoFlag;
				}
			}
			if (root.hasChildNodes()) {
				NodeList nList = root.getChildNodes();
				int childrenCount = nList.getLength();
				for (int i = 0; i < childrenCount; i++)
					gotoNode(nList.item(i), param);
				if (nodoFlag != null)
					return nodoFlag;
			}
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Clase: ProgramReader.gotoNode - Error al recorrer el arbol " + e.getMessage());
		}
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (15-09-2000 09:41:45 AM)
	 */
	public void init(String archivo) throws servicios.generales.WSException {
		load(archivo);
		//Todos los xml files asociados a la aplicacion
		xmlFiles = new java.util.Properties();
		//Todos los errores de la aplicacion
		todosErrores = new java.util.Properties();
		//Inicio la estructura con todos los programas
		Element root = getDoc().getDocumentElement();
		setAllPrograms(root);
	}
	static public void main(String[] argv) {
		try {
			if (argv.length != 1) {
				System.err.println("Uso: java servicios.control.ProgramReader filename");
				System.exit(1);
			}
			ProgramReader yomismo = new ProgramReader();
			yomismo.init(argv[0]);
			/*		java.util.Properties prop = yomismo.getQMgrs();
					servicios.mqseries.ConectorMQ cnxMQ = (servicios.mqseries.ConectorMQ)prop.get("mqlocal");
			*/
			ProgRequest program = yomismo.getPrograma("prglsttrabajadores");
			System.out.println(program.getNombre());
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (13-09-2000 10:37:42 AM)
	 * @param param org.w3c.dom.Element
	 * 05-01-2001:	Agrega la logica para leer el dato de paginacion desde los atributos del programa
	 * 12-01-2001:	Agrega la estraccion de datos para la estructura que contiene los datos
	 *				iniciales de la paginación
	 * 03-03-2004:	Habilitacion de la lectura de comentarios en los XML de programas
	 * 07-05-2004:	Deja de usar este metodo la busqueda de programas. Ahora usa
	 * 						- setAtributosPrg
	 * 						- setServiciosPrg
	 * 						Estos metodos dejan de ser recursivos
	 */
	@Override
	protected void recorreArbol(Node root) throws servicios.generales.WSException {
		try {
			int type = root.getNodeType();
			switch (type) {
				case Node.DOCUMENT_NODE :
					{
						break;
					}
				case Node.ELEMENT_NODE :
					{
							if (root.getNodeName().equalsIgnoreCase("xmls")) {
							String llave = "";
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 0; i < numHijos; i++) {
								if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("xml")) {
									Element rama = (Element) nList.item(i);
									llave = rama.getAttribute("type");
									NodeList ListaNodos = nList.item(i).getChildNodes();
									int hijos = ListaNodos.getLength();
									for (int j = 0; j < hijos; j++) {
										if (ListaNodos.item(j).getNodeType() != 3 && ListaNodos.item(j).getNodeName().equalsIgnoreCase("path")) {
											xmlFiles.put(llave, ListaNodos.item(j).getFirstChild().getNodeValue());
										}
									}
								}
							}
							//Configuro una estructura de datos con los XMLs que correspondan
							break;
						}
						else if (root.getNodeName().equalsIgnoreCase("error")) {
							String llave = "";
							Element rama = (Element) root;
							llave = rama.getAttribute("idError");
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 0; i < numHijos; i++) {
								if (nList.item(i).getNodeName().equalsIgnoreCase("AliasJspError"))
									todosErrores.put(llave, nList.item(i).getFirstChild().getNodeValue());
							}
							break;
							//Configuro una estructura de datos con los XMLs que correspondan
						}
					}
				case Node.ENTITY_REFERENCE_NODE :
					{
						//					System.out.println("Tipo: Nodo Entity Reference");
						break;
					}
				case Node.CDATA_SECTION_NODE :
					{
						//					System.out.println("Tipo: Nodo Seccion");
						break;
					}
				case Node.COMMENT_NODE :
					{
						break;
					}
			}
			if (root.hasChildNodes()) {
				NodeList nList = root.getChildNodes();
				int childrenCount = nList.getLength();
				for (int i = 0; i < childrenCount; i++)
					recorreArbol(nList.item(i));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new servicios.generales.WSException("Clase: ProgramReader.recorreArbol - Error al recorrer el arbol " + e.getMessage());
		}
	}
	@Override
	public void setDoc(Document doc2) {
		this.doc = doc2;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14-09-2000 10:55:38 AM)
	 * @param newPrograma servicios.control.ProgRequest
	 */
	public void setPrograma(ProgRequest newPrograma) {
		programa = newPrograma;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18-10-2000 02:28:36 PM)
	 * @param newTodosErrores java.util.Properties
	 */
	public void setTodosErrores(java.util.Properties newTodosErrores) {
		todosErrores = newTodosErrores;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22-09-2000 09:35:57 AM)
	 * @param newXmlFiles java.util.Properties
	 */
	public void setXmlFiles(java.util.Properties newXmlFiles) {
		xmlFiles = newXmlFiles;
	}
	protected void setAtributosPrg(Node rootPrg) {
		NodeList nListAtribs = ((Element) rootPrg).getElementsByTagName("atributos");
		Node aux = nListAtribs.item(0);
		NodeList nList = aux.getChildNodes();
		int numHijos = nList.getLength();
		for (int i = 0; i < numHijos; i++) {
			if (nList.item(i).getNodeType() != 3) {
				if (nList.item(i).getNodeName().equalsIgnoreCase("servicioIni"))
					programa.setInitService(nList.item(i).getFirstChild().getNodeValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("AliasJspSalida"))
					programa.setJspTocall(nList.item(i).getFirstChild().getNodeValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("pagina"))
					programa.setPagina(Boolean.valueOf(nList.item(i).getFirstChild().getNodeValue()));
				else if (nList.item(i).getNodeName().equalsIgnoreCase("paginacionDta")) {
					NodeList nListPag = nList.item(i).getChildNodes();
					int numHijosPag = nListPag.getLength();
					for (int j = 0; j < numHijosPag; j++) {
						if (nListPag.item(j).getNodeType() != 3) {
							if (nListPag.item(j).getNodeName().equalsIgnoreCase("beanPaginado"))
								programa.datosPaginacion.setBeanPaginado(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("numLineas"))
								programa.datosPaginacion.setNumLineas(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("numCol"))
								programa.datosPaginacion.setNumColumna(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("aliasServlet"))
								programa.datosPaginacion.setAkaServlet(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("program"))
								programa.datosPaginacion.setPrograma(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("imagenes")) {
								NodeList nListImg = nListPag.item(j).getChildNodes();
								int numHijosImg = nList.getLength();
								for (int k = 0; k < numHijosImg; k++) {
									if (nListImg.item(k).getNodeType() != 3) {
										if (nListImg.item(k).getNodeName().equalsIgnoreCase("pathURL"))
											programa.datosPaginacion.imagenes[0] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("prevAct"))
											programa.datosPaginacion.imagenes[1] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("prevInact"))
											programa.datosPaginacion.imagenes[2] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("sgteAct"))
											programa.datosPaginacion.imagenes[3] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("sgteInact"))
											programa.datosPaginacion.imagenes[4] = nListImg.item(k).getFirstChild().getNodeValue();
									}
								}
							}

						}
					}
				}
			}
		}
	}
	protected void setServiciosPrg(Node rootPrg) throws WSException{
		NodeList nListServ = ((Element) rootPrg).getElementsByTagName("servicio");
		//Obtengo la lista de servicios asociados a este programa
		Node aux = nListServ.item(0);
		while(aux != null){
			servicios.control.Service servicio = new servicios.control.Service();
			NodeList nList = aux.getChildNodes();
			int numHijos = nList.getLength();
			String llave = "";
			for (int i = 0; i < numHijos; i++) {
				if (nList.item(i).getNodeType() != 3) {
					if (nList.item(i).getNodeName().equalsIgnoreCase("idservicio")) {
						Element rama = (Element) nList.item(i);
						llave = rama.getAttribute("key");
						servicio.setServicio(nList.item(i).getFirstChild().getNodeValue());
					}
					if (nList.item(i).getNodeName().equalsIgnoreCase("session"))
						servicio.setSession(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("modo"))
						servicio.setModoOpera(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("beanSalida"))
						servicio.setBeanSalida(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("timeout"))
						servicio.setTimeOut(Integer.parseInt(nList.item(i).getFirstChild().getNodeValue()));
					if (nList.item(i).getNodeName().equalsIgnoreCase("consname"))
						servicio.setNombreConsul(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("tipoparser"))
						servicio.setTipoParser(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("boclass"))
						servicio.setClaseNegocios(nList.item(i).getFirstChild().getNodeValue());
				}
			}
			programa.addService(llave, servicio);
			aux = (aux.getNextSibling()).getNextSibling();
		}
		//Se copia el programa a la lista de programas
		todosProgramas.put(programa.getNombre(), programa);
	}
	
	protected void setAllPrograms(Node root) throws WSException{
		NodeList nListServ = ((Element) root).getElementsByTagName("programa");
		//Obtengo la lista de objetos
		Node aux = nListServ.item(0);
		while(aux != null){
			this.programa = new ProgRequest();
			this.programa.setNombre(aux.getAttributes().item(0).getNodeValue());			
			buildProgram(aux);
			aux = (aux.getNextSibling()).getNextSibling();
		}		
	}

}
