/*
 * Created on Apr 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package servicios.generales;

import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * @author lpino
 *	Fecha: Apr 7, 2005
 * 
 */
public class XmlHelper extends XmlBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] nombreCols;
	
	public XmlHelper() {
		super();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.doc = db.newDocument();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void recorreArbol(Node root) throws WSException {
	}
	
	public void creaRaizBean(){
		Element raiz = getDoc().createElement("bean");
		raiz.setAttribute("nombre","");
		getDoc().appendChild(raiz);
	}
	
	public Element creaColumna(String nombre, String tipo){
		Element columna = getDoc().createElement("col");
		columna.setAttribute("nombre",nombre);
		columna.setAttribute("tipo", tipo);
		getDoc().getDocumentElement().appendChild(columna);
		return columna;
	}
	
	public void insertaDatosColumna(Element columna, Object[] datos){
		for(int i=0;i < datos.length; i++){
			Element dato = getDoc().createElement("dato");
			//Creo desde el docuemnto un nodo de texto y le asigno un valor
			Text text = getDoc().createTextNode(datos[i] != null?datos[i].toString():"");
			dato.appendChild(text);
			columna.appendChild(dato);
		}
	}
	
	/**
	 * @return
	 */
	public String[] getNombreCols() {
		return nombreCols;
	}

	/**
	 * @param strings
	 */
	public void setNombreCols(String[] strings) {
		nombreCols = strings;
	}

	public void creaRegistro(Object[] datos) {
		try {
			Element fila = getDoc().createElement("data");
			for (int i = 1; i < nombreCols.length; i++) {
				fila.setAttribute(nombreCols[i], datos[i] == null?"":datos[i].toString());
			}
			getDoc().getDocumentElement().appendChild(fila);
		} catch (DOMException e) {
			e.printStackTrace();
		}
	}
	
	public Object[] retornaColumna(int indice){
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName("col");
		System.out.println("Nodos= " + nList.getLength());
		NodeList nList2 = ((Element)nList.item(indice)).getChildNodes();
		Object[] columna = new Object[(nList2.getLength() -1)/2];
		int cont = 0;
		for(int i=0;i < nList2.getLength();i++){
			if(nList2.item(i).getNodeType() != Node.TEXT_NODE){
				columna[cont] = nList2.item(i).getFirstChild() == null?null:nList2.item(i).getFirstChild().getNodeValue();
				cont++;
			}
		}
		for(int i=0;i < columna.length;i++)
			System.out.print(columna[i] + ";");			
		return columna;
	}
	
	public String[] retornaNomColumnas(){
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName("col");
		System.out.println("Nodos= " + nList.getLength());
		String[] nombres = new String[nList.getLength()+1];
		for(int i=0;i<nList.getLength();i++){
			nombres[i+1] = ((Element)nList.item(i)).getAttribute("nombre");
			System.out.println(nombres[i+1]);
		}
		return nombres;
	}

	public String[] getTiposDatos(){
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName("col");
		System.out.println("Nodos= " + nList.getLength());
		String[] nombres = new String[nList.getLength()+1];
		for(int i=0;i<nList.getLength();i++){
			nombres[i+1] = ((Element)nList.item(i)).getAttribute("tipo");
			System.out.println(nombres[i+1]);
		}
		return nombres;
	}
	public void setNodeValue(String nodeName, String nodeValue){
		addStringValue(nodeName, nodeValue);
	}
	@Override
	public void setAttribValue(String nodeName, String atribName, String atribValue) {
		super.setAttribValue(nodeName, atribName, atribValue);
	}
	public void setRootAttribValue(String atribName, String atribValue){
		//Obtengo la raiz del arbol
		Element root = getDoc().getDocumentElement();
		//Coloco el valor en el atributo que corresponda
		root.setAttribute(atribName, atribValue);
	}
	public void setXmlFromString(String xml, boolean valida) throws MsgException{
		try {
			// Abre el archivo
			InputSource inSource = new InputSource(new java.io.ByteArrayInputStream(xml.getBytes()));
			DOMParser parser = new DOMParser();
			if (valida) {
				//Configura la validación
				parser.setFeature("http://xml.org/sax/features/validation", true);
				//Configura la validación via schema
				parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			}
			//Configura un manejador de errores para la validación  via schema
			parser.setErrorHandler(new SAXParserErrorHandler());
			parser.parse(inSource);
			Document doc1 = parser.getDocument();
			// Documento esta bien formado
			setDoc(doc1);
		}
		catch (org.xml.sax.SAXException e) {
			throw new MsgException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void setRootValue(String value){
		//Obtengo la raiz del arbol
		Element root = getDoc().getDocumentElement();
		//Creo desde el docuemnto un nodo de texto y le asigno un valor
		Text text = doc.createTextNode(value);
		//Coloco el valor en el atributo que corresponda
		root.appendChild(text);
	}
	public static String replaceATtribValue(String valor){
		return null;
	}
}
