package servicios.generales.xsl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

public class XslXml2Html extends XslFceTransformer {

	public XslXml2Html() {
		super();
	}

//	@Override
	@Override
	public void transform() {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();			
			//Instantiate a DocumentBuilderFactory.
			  DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			  // And setNamespaceAware, which is required when parsing xsl files
			  dFactory.setNamespaceAware(true);			  
			  // Use the DOM Document to define a DOMSource object.
			  DOMSource xslDomSource = new DOMSource(this.docXsl);
			  // Set the systemId: note this is actually a URL, not a local filename
//			  xslDomSource.setSystemId("birds.xsl");
			  // Process the stylesheet DOMSource and generate a Transformer.
			  Transformer transformer = tFactory.newTransformer(xslDomSource);
			  // Use the DOM Document to define a DOMSource object.
			  DOMSource xmlDomSource = new DOMSource(this.docXml);			  
			  // Set the base URI for the DOMSource so any relative URIs it contains can
			  // be resolved.
//			  xmlDomSource.setSystemId("birds.xml");			  
			  // Create an empty DOMResult for the Result.
			  DOMResult domResult = new DOMResult(); 
			  // Perform the transformation, placing the output in the DOMResult.
			  transformer.transform(xmlDomSource, domResult);
			  this.resultado = domResult;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}  catch (TransformerException e) {
			e.printStackTrace();
		}
	}

//	@Override
	@Override
	public void retornaSalida(HttpServletResponse res) {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
	    try {
			// El retorno es HTML.
			res.setContentType("text/html; charset=ISO-8859-1");
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"

            serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            serializer.setOutputProperty( OutputKeys.METHOD, "html");
            StreamResult streamResult = new StreamResult(res.getOutputStream());            
            serializer.transform(new DOMSource(((DOMResult)(this.resultado)).getNode()), streamResult);
            streamResult.getOutputStream().flush();
            streamResult.getOutputStream().close();
			// La salida va al stream de salida
/*			PrintWriter out = res.getWriter();
			XMLSerializer ser = new XMLSerializer(out, new OutputFormat("html", "ISO-8859-1", false));
			//Extrae la raiz del HTML
			Element html = ((DocumentImpl)(((DOMResult)(this.resultado)).getNode())).getDocumentElement();
			//Serializa la pagina
			ser.serialize(html);
			out.flush();
			out.close();
*/		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
