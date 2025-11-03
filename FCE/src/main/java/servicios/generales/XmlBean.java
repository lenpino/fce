package servicios.generales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

//import org.apache.xerces.dom.CoreDocumentImpl;
//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import servicios.xml.FceXmlExceptionHandler;
import servicios.xml.FceXmlValidationException;


/**
 * @autor lpino
 * @Fecha 07-abr-03
*/
public abstract class XmlBean {
	private String[] errors = null;
	private String[] warnings = null;
	private String[] fatals = null;
	public void setXml(String xml, boolean valida) throws MsgException{
		try {
			// Abre el archivo
			InputSource inSource = new InputSource(new java.io.StringReader(xml));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbFactory.newDocumentBuilder();
			if (valida) {
			      String schemaLang = "http://www.w3.org/2001/XMLSchema";
			      // Tipo de driver de validacion:
			      SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			      // create schema by reading it from an XSD file:
			      Schema schema = factory.newSchema(new StreamSource("sample.xsd"));
			      Validator validator = schema.newValidator();
			      // at last perform validation:
			      validator.validate(new StreamSource("sample.xml"));
			}
			//Configura un manejador de errores para la validación  via schema
			parser.setErrorHandler(new SAXParserErrorHandler());
			
			Document doc1 =parser.parse(inSource);
			doc1.getDocumentElement().normalize();
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
	/**
	 * Metodo para validar y crear un objeto DOM a partir de un String  XML. 
	 * El resultado de la validación se coloca en las variables de instacia y puede ser solicitada por clases que utilicen este wrapper para conocer el resultado.
	 * En caso de existir errores fatales en la validación, estos se reportan a través de FceXmlValidationException
	 * @param xml 
	 * Un String con el XML entrante
	 * 
	 * @param valida 
	 * Indica si se valida o no el XML con un XSD
	 * 
	 * @throws Exception L
	 */
	public void createXml(String xml, boolean valida,String xsd) throws Exception{
		try {
			// Abre el archivo
			InputSource inSource = new InputSource(new java.io.StringReader(xml));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbFactory.newDocumentBuilder();
			
/*			InputSource inSource = new InputSource(new java.io.StringReader(xml));
			DOMParser parser = new DOMParser();
			if (valida) {
				//Configura la validación
				parser.setFeature("http://xml.org/sax/features/validation", true);
				//Configura la validación via schema
				parser.setFeature("http://apache.org/xml/features/validation/schema", true);
				//Configura el XSD a utilizar
				parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",xsd);
			}
*/			//Configura un manejador de errores para la validación  via schema
			FceXmlExceptionHandler elHandler = new FceXmlExceptionHandler();
			parser.setErrorHandler(elHandler);
//			parser.parse(inSource);
//			Document doc1 = parser.getDocument();
			Document doc1 =parser.parse(inSource);
			doc1.getDocumentElement().normalize();
			//Entrega el resultado de los errores y advertencias. En caso de haber errores fatales se arroja una exception ad-hoc
			if(elHandler.isLethal())
				throw new FceXmlValidationException(elHandler.getWarnings(), elHandler.getErrors(), elHandler.getFatals());
			else{
				setErrors(elHandler.getErrors());
				setWarnings(elHandler.getWarnings());
			}
			// Documento esta bien formado
			setDoc(doc1);
		}
		catch (org.xml.sax.SAXException e) {
			throw new MsgException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	protected void setAttribValue(String nombre_nodo, String nombre_atrib, String valor) {
		//Obtengo la raiz del arbol
		Element root = getDoc().getDocumentElement();
		//Dede la raiz extraigo el tag de la llave
		NodeList nList = root.getElementsByTagName(nombre_nodo);
		//Dado que existe solo una llave saco la primera con indice 0
		Element rama = (Element) nList.item(0);
		rama.setAttribute(nombre_atrib, valor);
	}
	public void saveXML(String nfile) {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
        	java.io.BufferedWriter out = new java.io.BufferedWriter(new FileWriter(nfile, false));
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            serializer.setOutputProperty( OutputKeys.METHOD, "xml");
            StreamResult streamResult = new StreamResult(new StringWriter());
            serializer.transform(new DOMSource(doc), streamResult);
            String xmlString = streamResult.getWriter().toString();
            out.write(xmlString);
            out.flush();
            out.close();
            
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
}
	public String printXML(String nombre_nodo) {
		StringBuffer buffer = new StringBuffer();		
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName(nombre_nodo);
		Element rama = (Element) nList.item(0);
		return display( rama ,buffer);
	}
	public String printXML() {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            serializer.setOutputProperty( OutputKeys.METHOD, "xml");
            StreamResult streamResult = new StreamResult(new StringWriter());
            serializer.transform(new DOMSource(doc), streamResult);
            String xmlString = streamResult.getWriter().toString();
            return xmlString;
            
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();            
            throw new RuntimeException(e);
        }
}
	public static String printXML(Node nodo) {
		StringBuffer buffer = new StringBuffer();		
		return display( nodo,buffer);
	}
	public void load(String ArchivoXml, boolean validacion) throws MsgException {
		try {
			// Abre el archivo
			InputStream is = new FileInputStream(ArchivoXml);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbFactory.newDocumentBuilder();
/*			DOMParser parser = new DOMParser();
			if (validacion) {
				//Configura la validación
				parser.setFeature("http://xml.org/sax/features/validation", true);
				//Configura la validación via schema
				parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			}
			//Configura un manejador de errores para la validación  via schema
*/			parser.setErrorHandler(new SAXParserErrorHandler());
//			parser.parse(new org.xml.sax.InputSource(is));
			
			Document doc1 = parser.parse(new org.xml.sax.InputSource(is));
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
	public String getSingleNodeValue(String nombre_nodo) {
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName(nombre_nodo);
		Element rama = (Element) nList.item(0);
		if(rama == null || rama.getFirstChild() == null)
			return "";
		else
			return rama.getFirstChild().getNodeValue();
	}
	public ArrayList<String> getNodeValues(String nombre_nodo) {
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName(nombre_nodo);
		ArrayList<String> lista = new ArrayList<String>();
		for(int i=0;i<nList.getLength();i++){
			if (nList.item(i).getFirstChild().getNodeType() == Node.TEXT_NODE)
				lista.add(nList.item(i).getFirstChild().getNodeValue());
		}
		return lista;
	}
	public String getAttributeValue(String nombre_nodo, String nombre_atrib) {
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName(nombre_nodo);
		Element rama = (Element) nList.item(0);
		return rama.getAttribute(nombre_atrib);
	}
	private static String display(Node start, StringBuffer buffer) {
		if (start.getNodeType() == Node.ELEMENT_NODE) {
			buffer.append("<" + start.getNodeName());
			NamedNodeMap startAttr = start.getAttributes();
			for (int i = 0; i < startAttr.getLength(); i++) {
				Node attr = startAttr.item(i);
				buffer.append(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
			}
			buffer.append(">");
		}
		else if (start.getNodeType() == Node.TEXT_NODE) {
			buffer.append(start.getNodeValue());
		}
		for (Node child = start.getFirstChild(); child != null; child = child.getNextSibling()) {
			display(child,buffer);
		}
		if (start.getNodeType() == Node.ELEMENT_NODE) {
			buffer.append("</" + start.getNodeName() + ">\r");
		}
		return buffer.toString();
	}
	protected void addStringValue(String nombre_nodo, String valor) {
		try {
			//Obtengo la raiz del arbol
			Element root = getDoc().getDocumentElement();
			//Dede la raiz extraigo el tag de la llave
			NodeList nList = root.getElementsByTagName(nombre_nodo);
			//Dado que existe solo una llave saco la primera con indice 0
			Element rama = (Element) nList.item(0);
			//Creo desde el docuemnto un nodo de texto y le asigno un valor
			Text text = doc.createTextNode(valor);
			//agrego el nodo como hijo de la rama de la llave
			rama.appendChild(text);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void addStringValRel(Element raiz, String nombre_nodo, String valor) {
		//Dede la raiz extraigo el tag de la llave
		NodeList nList = raiz.getElementsByTagName(nombre_nodo);
		//Dado que existe solo una llave saco la primera con indice 0
		Element rama = (Element) nList.item(0);
		//Creo desde el docuemnto un nodo de texto y le asigno un valor
		Text text = doc.createTextNode(valor);
		//agrego el nodo como hijo de la rama de la llave
		rama.appendChild(text);
	}
	protected Document doc = null;
	/**
	 * Constructor for XmlBean.
	 */
	public XmlBean() {
		super();
	}
	public static void main(String[] args) {
	}
	public void setDoc(Document doc2) {
		this.doc = doc2;
	}
	public void load(String ArchivoXml) throws WSException {
		try {
			// Open specified file
			File is = new File(ArchivoXml);
			// Start parsing
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc1 = parser.parse(is);
			doc1.normalizeDocument();
			// Document is well-formed
			setDoc(doc1);
		}
		catch (Exception e) {
			throw new WSException("Clase MgrCons: Error al cargar (parsear) XML : MSG " + e.getMessage());
		}
	}
	public Document getDoc() {
		return this.doc;
	}
	
	protected Node find(String nconsulta) throws WSException {
		if(nconsulta == null)
			throw new servicios.generales.WSException("Clase: XmlBean Error: No se entrego el nombre a buscar en arbol XML");
		else if(nconsulta.equalsIgnoreCase(""))
			throw new servicios.generales.WSException("Clase: XmlBean Error: El nombre a buscar viene vacio");
		Element result = (getDoc()).getElementById(nconsulta);
		if(result == null)
			throw new servicios.generales.WSException("Clase: XmlBean Error: No se encontro " + nconsulta + " en el XML");
		else
			return result;
	}	
	protected abstract void recorreArbol(Node root) throws WSException;
	protected Node find(String ntag, String natrib, String nconsulta) throws WSException {
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName(ntag);
		Node aux = nList.item(0);
		while(aux != null){
			if(((Element)aux).getAttribute(natrib).equalsIgnoreCase(nconsulta))
				return aux;
			aux = (aux.getNextSibling()).getNextSibling();
		}
		throw new servicios.generales.WSException("Clase: XmlBean Error: No se encontro " + nconsulta + " en el XML");
	}
	public static Document getDocXML(InputSource doc) throws MsgException {
		try {
//			DOMParser parser = new DOMParser();
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (doc != null) {
/*				//Configura la validación
				parser.setFeature("http://xml.org/sax/features/validation", true);
				//Configura la validación via schema
				parser.setFeature("http://apache.org/xml/features/validation/schema", true);
*/				parser.setErrorHandler(new SAXParserErrorHandler());
//				parser.parse(doc);
			}
			else
				throw new MsgException("Clase: XmlBeanMsg Error: Documento entregado es nulo");
			return parser.parse(doc);
		}
		catch (org.xml.sax.SAXParseException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Tipo de documento invalido Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		}
		catch (SAXException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Tipo de documento invalido Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		}
		catch (IOException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Problemas de IO Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Document getDocXML(InputSource doc, boolean valida) throws MsgException {
		try {
//			DOMParser parser = new DOMParser();
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (doc != null) {
/*				if(valida){
					//Configura la validación
					parser.setFeature("http://xml.org/sax/features/validation", true);
					//Configura la validación via schema
					parser.setFeature("http://apache.org/xml/features/validation/schema", true);
					parser.setErrorHandler(new SAXParserErrorHandler());
				}
*/				parser.parse(doc);
			}
			else
				throw new MsgException("Clase: XmlBeanMsg Error: Documento entregado es nulo");
			return parser.parse(doc);
		}
		catch (org.xml.sax.SAXParseException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Tipo de documento invalido Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		}
		catch (SAXException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Tipo de documento invalido Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		}
		catch (IOException e) {
			throw new MsgException("Clase: XmlBeanMsg Error: Problemas de IO Msg: " + e.getMessage() + " Mensaje XML: " + doc.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void setErrors(String[] errors) {
		this.errors = errors;
	}
	public void setFatals(String[] fatals) {
		this.fatals = fatals;
	}
	public void setWarnings(String[] warnings) {
		this.warnings = warnings;
	}
	public String[] getErrorMessages(){
		return this.errors;
	}
	public String[] getWarningMessages(){
		return this.warnings;
	}
	public String[] getFatalMessages(){
		return this.fatals;
	}
}
