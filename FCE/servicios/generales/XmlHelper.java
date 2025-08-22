/*
 * Created on Apr 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package servicios.generales;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Helper class for XML manipulation with modern Java features
 * @author lpino
 * Fecha: Apr 7, 2005
 * Modernizado: usando APIs estándar de Java y características modernas
 */
public class XmlHelper extends XmlBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String[] nombreCols;
    
    // Constantes para nombres de elementos XML
    private static final String ELEMENT_BEAN = "bean";
    private static final String ELEMENT_COL = "col";
    private static final String ELEMENT_DATA = "data";
    private static final String ELEMENT_DATO = "dato";
    private static final String ATTR_NOMBRE = "nombre";
    private static final String ATTR_TIPO = "tipo";
    
    public XmlHelper() {
        super();
        initializeDocument();
    }
    
    /**
     * Inicializa el documento XML usando APIs estándar de Java
     */
    private void initializeDocument() {
        try {
            var dbf = DocumentBuilderFactory.newInstance();
            // Configuración segura para prevenir XXE attacks
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.newDocument();
        } catch (ParserConfigurationException e) {
            throw new XmlHelperException("Error al inicializar DocumentBuilder", e);
        }
    }
    
    @Override
    protected void recorreArbol(Node root) throws WSException {
        // Implementación pendiente según necesidades específicas
    }
    
    /**
     * Crea el elemento raíz bean
     */
    public void creaRaizBean() {
        var raiz = getDoc().createElement(ELEMENT_BEAN);
        raiz.setAttribute(ATTR_NOMBRE, "");
        getDoc().appendChild(raiz);
    }
    
    /**
     * Crea una columna con nombre y tipo especificados
     * @param nombre Nombre de la columna
     * @param tipo Tipo de datos de la columna
     * @return Elemento columna creado
     */
    public Element creaColumna(String nombre, String tipo) {
        var columna = getDoc().createElement(ELEMENT_COL);
        columna.setAttribute(ATTR_NOMBRE, nombre);
        columna.setAttribute(ATTR_TIPO, tipo);
        getDoc().getDocumentElement().appendChild(columna);
        return columna;
    }
    
    /**
     * Inserta datos en una columna
     * @param columna Elemento columna donde insertar los datos
     * @param datos Array de datos a insertar
     */
    public void insertaDatosColumna(Element columna, Object[] datos) {
        Arrays.stream(datos)
            .forEach(dato -> {
                var elementoDato = getDoc().createElement(ELEMENT_DATO);
                var textoValor = dato != null ? dato.toString() : "";
                var nodoTexto = getDoc().createTextNode(textoValor);
                elementoDato.appendChild(nodoTexto);
                columna.appendChild(elementoDato);
            });
    }
    
    /**
     * @return Array con nombres de columnas
     */
    public String[] getNombreCols() {
        return nombreCols != null ? nombreCols.clone() : new String[0];
    }
    
    /**
     * Establece los nombres de las columnas
     * @param nombres Array con nombres de columnas
     */
    public void setNombreCols(String[] nombres) {
        this.nombreCols = nombres != null ? nombres.clone() : null;
    }
    
    /**
     * Crea un registro con los datos proporcionados
     * @param datos Array de datos para el registro
     */
    public void creaRegistro(Object[] datos) {
        if (nombreCols == null || datos == null) {
            return;
        }
        
        try {
            var fila = getDoc().createElement(ELEMENT_DATA);
            // Empezar desde índice 1 como en el código original
            for (int i = 1; i < nombreCols.length && i < datos.length; i++) {
                var valor = datos[i] != null ? datos[i].toString() : "";
                fila.setAttribute(nombreCols[i], valor);
            }
            getDoc().getDocumentElement().appendChild(fila);
        } catch (DOMException e) {
            throw new XmlHelperException("Error al crear registro", e);
        }
    }
    
    /**
     * Retorna los datos de una columna específica
     * @param indice Índice de la columna
     * @return Array con los datos de la columna
     */
    public Object[] retornaColumna(int indice) {
        var root = getDoc().getDocumentElement();
        var nList = root.getElementsByTagName(ELEMENT_COL);
        
        if (indice >= nList.getLength()) {
            return new Object[0];
        }
        
        var columnElement = (Element) nList.item(indice);
        var childNodes = columnElement.getChildNodes();
        
        // Filtrar solo nodos que no sean de texto y extraer valores
        var datos = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var nodo = childNodes.item(i);
            if (nodo.getNodeType() != Node.TEXT_NODE) {
                var valor = Optional.ofNullable(nodo.getFirstChild())
                    .map(Node::getNodeValue)
                    .orElse(null);
                datos.add(valor);
            }
        }
        
        return datos.toArray();
    }
    
    /**
     * Retorna los nombres de las columnas
     * @return Array con nombres de columnas
     */
    public String[] retornaNomColumnas() {
        var root = getDoc().getDocumentElement();
        var nList = root.getElementsByTagName(ELEMENT_COL);
        var nombres = new String[nList.getLength() + 1];
        
        for (int i = 0; i < nList.getLength(); i++) {
            var element = (Element) nList.item(i);
            nombres[i + 1] = element.getAttribute(ATTR_NOMBRE);
        }
        
        return nombres;
    }
    
    /**
     * Obtiene los tipos de datos de las columnas
     * @return Array con tipos de datos
     */
    public String[] getTiposDatos() {
        var root = getDoc().getDocumentElement();
        var nList = root.getElementsByTagName(ELEMENT_COL);
        var tipos = new String[nList.getLength() + 1];
        
        for (int i = 0; i < nList.getLength(); i++) {
            var element = (Element) nList.item(i);
            tipos[i + 1] = element.getAttribute(ATTR_TIPO);
        }
        
        return tipos;
    }
    
    /**
     * Establece el valor de un nodo
     * @param nodeName Nombre del nodo
     * @param nodeValue Valor del nodo
     */
    public void setNodeValue(String nodeName, String nodeValue) {
        addStringValue(nodeName, nodeValue);
    }
    
    @Override
    public void setAttribValue(String nodeName, String atribName, String atribValue) {
        super.setAttribValue(nodeName, atribName, atribValue);
    }
    
    /**
     * Establece el valor de un atributo en el elemento raíz
     * @param atribName Nombre del atributo
     * @param atribValue Valor del atributo
     */
    public void setRootAttribValue(String atribName, String atribValue) {
        var root = getDoc().getDocumentElement();
        if (root != null) {
            root.setAttribute(atribName, atribValue);
        }
    }
    
    /**
     * Establece el XML desde una cadena de texto usando APIs estándar
     * @param xml Cadena XML
     * @param valida Si debe validar el XML
     * @throws MsgException Si hay error en el parsing
     */
    public void setXmlFromString(String xml, boolean valida) throws MsgException {
        try {
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            if (valida) {
                dbf.setValidating(true);
                // Configuración de validación moderna
                dbf.setFeature("http://xml.org/sax/features/validation", true);
                dbf.setFeature("http://apache.org/xml/features/validation/schema", true);
            }
            
            // Configuración de seguridad
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            var builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new ModernErrorHandler());
            
            try (var inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
                var inputSource = new InputSource(inputStream);
                inputSource.setEncoding(StandardCharsets.UTF_8.name());
                var document = builder.parse(inputSource);
                setDoc(document);
            }
            
        } catch (SAXException e) {
            throw new MsgException("Error de validación XML: " + e.getMessage());
        } catch (Exception e) {
            throw new MsgException("Error al procesar XML: " + e.getMessage());
        }
    }
    
    /**
     * Establece el valor del elemento raíz
     * @param value Valor a establecer
     */
    public void setRootValue(String value) {
        var root = getDoc().getDocumentElement();
        if (root != null) {
            var text = doc.createTextNode(value);
            root.appendChild(text);
        }
    }
    
    /**
     * Método para reemplazar valores de atributos (implementación pendiente)
     * @param valor Valor a procesar
     * @return Valor procesado
     */
    public static String replaceAttribValue(String valor) {
        // Implementación específica según necesidades
        return valor != null ? valor.trim() : "";
    }
    
    // Clases internas para manejo de errores
    
    /**
     * ErrorHandler moderno para SAX parsing
     */
    private static class ModernErrorHandler implements ErrorHandler {
        
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            System.err.println("Warning: " + exception.getMessage());
        }
        
        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw new SAXException("Error de parsing XML: " + exception.getMessage(), exception);
        }
        
        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw new SAXException("Error fatal de parsing XML: " + exception.getMessage(), exception);
        }
    }
    
    /**
     * Excepción específica para XmlHelper
     */
    public static class XmlHelperException extends RuntimeException {
        
        private static final long serialVersionUID = 1L;
        
        public XmlHelperException(String message) {
            super(message);
        }
        
        public XmlHelperException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}