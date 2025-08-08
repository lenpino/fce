package servicios.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import servicios.generales.WSException;

/**
 * @author lpino
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CtrlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 10:37:42 AM)
 * @param param org.w3c.dom.Element
 * 05-01-2001:	Agrega la logica para leer el dato de paginacion desde los atributos del programa
 * 12-01-2001:	Agrega la extraccion de datos para la estructura que contiene los datos
 *							iniciales de la paginación
 * 28-07-2003:	Modifica la llamada a las paginas JSP para no usar mapeo cuando viene un URL directo
 * 						esto permite mas flexibilidad en la salida.
 * 26-12-2005: Agrega lista de archivos XSL
 */
	protected Document doc = null;
	protected String defaultPage = null;
	protected String errorPage = null;
	protected Properties pageList = null;
	protected String tipoSesion = null;
	public final static String FS = System.getProperty("file.separator");
	protected HashMap xslList = null;
	private String ctx = null;
	
	private void setPages() throws WSException{
		try{
		Element root = this.doc.getDocumentElement();
			if (root.hasChildNodes()) {
				NodeList nList = root.getChildNodes();
				int numHijos = nList.getLength();
				for (int i = 0; i < numHijos; i++)
					if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("page-list"))
						recorreArbol(nList.item(i));
			}
		}
		catch(Exception e){
			if( e instanceof WSException)
				throw (WSException)e;
			else
				throw new WSException("Clase: CtrlServlet Error: Error al configurar lista de páginas Msg: " + e.getMessage());
		}
	}

	private void recorreArbol(Node root) throws servicios.generales.WSException {
		try {
			int type = root.getNodeType();
			switch (type) {
				case Node.DOCUMENT_NODE :
					{
						break;
					}
				case Node.ELEMENT_NODE :
					{
						if (root.getNodeName().equalsIgnoreCase("page-list")) {
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							this.pageList = new Properties();
							for (int i = 0; i < numHijos; i++) {
								if (nList.item(i).getNodeType() != 3) {
									if (nList.item(i).getNodeName().equalsIgnoreCase("page")){
										String npagina = null;
										String uri = null;
										NodeList ListaNodos = nList.item(i).getChildNodes();
										int hijos = ListaNodos.getLength();
										for (int j = 0; j < hijos; j++) {
											if (ListaNodos.item(j).getNodeType() != 3){
												if(ListaNodos.item(j).getNodeName().equalsIgnoreCase("page-name"))
													npagina = ListaNodos.item(j).getFirstChild().getNodeValue();
													else if(ListaNodos.item(j).getNodeName().equalsIgnoreCase("uri")){
														uri = ListaNodos.item(j).getFirstChild().getNodeValue();
													}
											}
										}
										//Si la extensión de la pagina corresponde a un archivo XLS
										if(uri.substring(uri.length()-4).equalsIgnoreCase(".xsl")){
											//Se elimina el primer separador y se usa el metodo replace
											uri = CtrlServlet.replace(uri.substring(1),"/",FS);
											//Carga la lista de archivos XLS con los objetos DOM
										      //Instantiate a DocumentBuilderFactory.
										      DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
										      // y coloca setNamespaceAware, lo que se requiere al parsear archivos xsl
										      dFactory.setNamespaceAware(true);														      
										      //Usa un DocumentBuilderFactory para crear un DocumentBuilder.
										      DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
										      URL archivo = new URL("file", "", ctx+uri);
										      //Usa un DocumentBuilder para parsear el archivo xsl.
										      Document xslDoc = dBuilder.parse(archivo.openStream());
										      xslList.put(npagina,xslDoc);
										}
										else
											this.pageList.put(npagina,uri);
									}
									else if (nList.item(i).getNodeName().equalsIgnoreCase("default-page")){
										NodeList ListaNodos = nList.item(i).getChildNodes();
										int hijos = ListaNodos.getLength();
										for (int j = 0; j < hijos; j++) {
											if (ListaNodos.item(j).getNodeType() != 3){
												if(ListaNodos.item(j).getNodeName().equalsIgnoreCase("uri"))
													this.defaultPage = ListaNodos.item(j).getFirstChild().getNodeValue();
											}
										}
									}
									else if (nList.item(i).getNodeName().equalsIgnoreCase("error-page")){
										NodeList ListaNodos = nList.item(i).getChildNodes();
										int hijos = ListaNodos.getLength();
										for (int j = 0; j < hijos; j++) {
											if (ListaNodos.item(j).getNodeType() != 3){
												if(ListaNodos.item(j).getNodeName().equalsIgnoreCase("uri"))
													this.errorPage = ListaNodos.item(j).getFirstChild().getNodeValue();
											}
										}
									}
								}
							}
						}
						break;
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
			}
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Clase: CtrlServlet - Error al recorrer el arbol " + e.getMessage());
		}
	}

	private void carga(String ArchivoXml) throws servicios.generales.WSException{
	try {
		InputStream is = new FileInputStream(ArchivoXml);
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource input = new InputSource(is);
		Document doc1 = parser.parse(input );
/*		DOMParser parser = new DOMParser();
		parser.parse(ArchivoXml);
		Document doc1 = parser.getDocument();*/
		this.doc = doc1;
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al parsear archivo XML, msg: " + e.getMessage());
	} 
}


	/**
	 * Constructor for CtrlServlet.
	 */
	public CtrlServlet() {
		super();
	}
	
	public void callPage(String npagina, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		//Si se especifica un URL directamente, no se usa el mapeo
		if(npagina.charAt(0) == '/')
			dispatcher = getServletContext().getRequestDispatcher(npagina);
			jakarta.servlet.http.HttpSession session = req.getSession(false);
	public void init() throws jakarta.servlet.ServletException {
			dispatcher = getServletContext().getRequestDispatcher(defaultPage);
		else
			dispatcher = getServletContext().getRequestDispatcher(pageList.getProperty(npagina));
		if(!res.isCommitted())
			dispatcher.forward(req, res);
	}
	
	/**
	 * 17-06-2003:	Mejora al registro de excepciones imprimiendo el stack en la salida estandar
	 * 18-05-2004:	Elimina la impresión del stack dado que el ServiceMgr ya lo hace y se duplica la
	 * 						cantidad de lineas impresas en logs, ademas de ser costoso
	 * 26-01-2006: Corrige error cuando no viene la variable fmtSalida
	 * */
	public void handleError(HttpServletRequest req, HttpServletResponse res, Object error) throws ServletException{
		try {
			// Se invalida la sesion para que no se hagan ejecucion de servicios sin estar dentro de la aplicacion
			javax.servlet.http.HttpSession session = req.getSession(false);
			//Se invalida SSI no existe el flag de invalidacion
			if(session!=null && session.getAttribute("flagInvalidacion") == null){
				System.out.println("Invalidando la sesion " + session.getId());
				//Extrae el contexto en caso de errores
				session.invalidate();
			}
			System.out.println("Clase: CtrlServlet - Error en JSP - Msg: " + ((Exception)error).getMessage());
			if(req.getParameter("fmtSalida")==null ||!req.getParameter("fmtSalida").equalsIgnoreCase("sf")){
				RequestDispatcher dispatcher;
				req.setAttribute("error", error);
				((Exception)error).printStackTrace();
				dispatcher = getServletContext().getRequestDispatcher(errorPage);
				dispatcher.forward(req, res);
			}
			else{
				/*
				StringBuffer msgError = new StringBuffer();
				msgError.append("<error>\n\t<codigo>-1</codigo>\n\t<descripcion>");
				msgError.append(((Exception)error).getMessage());
				msgError.append("</descripcion>\n</error>");
				res.setContentType("text/plain");
				java.io.PrintWriter out = res.getWriter();
				out.print(msgError.toString());
				out.flush();
				out.close();
	           	*/
				
				OutputStream os = res.getOutputStream();
            	ObjectOutputStream obj_out = new ObjectOutputStream(os);
            	obj_out.writeObject(error);
            	os.flush();
            	os.close();
            	
			}
		} 
		catch (Exception e) {
			//En caso de existir un error que no esta controlado en este punto se deja que el container maneje el error
			throw new ServletException(e.getMessage());
		} 
	}
	
	/**
	 * 05-06-2003:	Se agrega la busqueda de path relativos para asi simplificar la configuracion de los servicios
	 * 26-12-2005:	Inicia la lista de archivos XSL
	 * */
	@Override
	public void init() throws javax.servlet.ServletException {
		super.init();
		try{
			ServletConfig config = getServletConfig();
			ServletContext contexto = config.getServletContext();
			ctx = contexto.getRealPath("") + FS;
			this.xslList = new HashMap();
			this.carga(contexto.getRealPath(config.getInitParameter("pageList")));
			setPages();
		}
		catch(Exception e){
			System.out.println("Error al inicializar servlet " +e.getMessage());
		}
	}
	public void callPageURLRW(String npagina, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		//Si se especifica un URL directamente, no se usa el mapeo
		if(npagina.charAt(0) == '/')
			dispatcher = getServletContext().getRequestDispatcher(res.encodeURL(npagina));
		//Si no existe la página de destino se va a la página default
		else if(pageList.getProperty(npagina) == null)
			dispatcher = getServletContext().getRequestDispatcher(res.encodeURL(defaultPage));
		else
			dispatcher = getServletContext().getRequestDispatcher(res.encodeURL(pageList.getProperty(npagina)));
		if(!res.isCommitted())
			dispatcher.forward(req, res);
	}

	  public static String replace(String source, String pattern, String replace)
	    {
	        if (source!=null)
	        {
	        final int len = pattern.length();
	        StringBuffer sb = new StringBuffer();
	        int found = -1;
	        int start = 0;

	        while( (found = source.indexOf(pattern, start) ) != -1) {
	            sb.append(source.substring(start, found));
	            sb.append(replace);
	            start = found + len;
	        }

	        sb.append(source.substring(start));

	        return sb.toString();
	        }
	        else return "";
	    }
}
