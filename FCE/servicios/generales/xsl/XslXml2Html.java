package servicios.generales.xsl;

import jakarta.servlet.http.HttpServletResponse;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XslXml2Html extends XslFceTransformer {

    public XslXml2Html() {
        super();
    }

    // @Override
    @Override
    public void transform() {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            // And setNamespaceAware, which is required when parsing xsl files
            // (No parseamos aquí, los Document ya vienen creados)
            // Seguridad JAXP
            tFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // Use the DOM Document to define a DOMSource object.
            DOMSource xslDomSource = new DOMSource(this.docXsl);
            // Set the systemId: note this is actually a URL, not a local filename
            // xslDomSource.setSystemId("birds.xsl");

            // Process the stylesheet DOMSource and generate a Transformer.
            Transformer transformer = tFactory.newTransformer(xslDomSource);

            // Use the DOM Document to define a DOMSource object.
            DOMSource xmlDomSource = new DOMSource(this.docXml);
            // Set the base URI for the DOMSource so any relative URIs it contains can be resolved.
            // xmlDomSource.setSystemId("birds.xml");

            // Create an empty DOMResult for the Result.
            DOMResult domResult = new DOMResult();

            // Perform the transformation, placing the output in the DOMResult.
            transformer.transform(xmlDomSource, domResult);
            this.resultado = domResult;
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            e.printStackTrace();
        }
    }

    // @Override
    @Override
    public void retornaSalida(HttpServletResponse res) {
        try {
            TransformerFactory tfactory = TransformerFactory.newInstance();
            tfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer serializer = tfactory.newTransformer();

            // El retorno es HTML.
            res.setContentType("text/html; charset=ISO-8859-1");

            // Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            try (var os = res.getOutputStream()) {
                serializer.transform(new DOMSource(((DOMResult) this.resultado).getNode()),
                        new StreamResult(os));
                os.flush();
            }

            // La salida va al stream de salida
            /*
            PrintWriter out = res.getWriter();
            XMLSerializer ser = new XMLSerializer(out, new OutputFormat("html", "ISO-8859-1", false));
            //Extrae la raiz del HTML
            Element html = ((DocumentImpl)(((DOMResult)(this.resultado)).getNode())).getDocumentElement();
            //Serializa la pagina
            ser.serialize(html);
            out.flush();
            out.close();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
