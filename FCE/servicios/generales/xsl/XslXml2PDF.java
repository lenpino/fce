package servicios.generales.xsl;

import jakarta.servlet.http.HttpServletResponse;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;

public class XslXml2PDF extends XslFceTransformer {

    // FOP y XSLT “modernos”
    private final FopFactory fopFactory;
    private final TransformerFactory tFactory;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public XslXml2PDF() {
        // Base URI por defecto (ajústalo si usas rutas relativas a recursos)
        this(URI.create(new java.io.File(".").toURI().toString()));
    }

    public XslXml2PDF(URI baseUri) {
        try {
            this.fopFactory = FopFactory.newInstance(baseUri);
        } catch (Exception e) {
            // Fallback muy defensivo si la URI falla por alguna razón
            throw new IllegalStateException("No se pudo inicializar FopFactory", e);
        }
        this.tFactory = TransformerFactory.newInstance();
        try {
            // endurece el procesamiento XSLT
            this.tFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (Exception ignore) {
            // algunos proveedores podrían no soportarlo; continuar sin romper
        }
    }

    @Override
    public void retornaSalida(HttpServletResponse res) {
        try {
            // Prepare response
            res.setContentType("application/pdf");
            res.setContentLength(out.size());
            // Puedes forzar descarga con attachment; inline muestra en el navegador
            // res.setHeader("Content-Disposition", "inline; filename=\"documento.pdf\"");

            // Send content to Browser
            var os = res.getOutputStream();
            os.write(out.toByteArray());
            os.flush(); // el contenedor suele cerrar el stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform() {
        try {
            // Limpia el buffer por si reusas la instancia
            out.reset();

            // Setup FOP
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Utiliza el XSL configurado como salida para este servicio
            DOMSource xsltSrc = new DOMSource(this.docXsl);
            Transformer transformer = tFactory.newTransformer(xsltSrc);

            // Se asegura que la salida sea a través de FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Setup input (tu XML de entrada)
            DOMSource src = new DOMSource(this.docXml);

            // Start the transformation and rendering process
            transformer.transform(src, res);

        } catch (FOPException e) {
            e.printStackTrace();
        } catch (Exception e) { // incluye TransformerException
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream getOutputStream() {
        return this.out;
    }

    public void savePDF(String path) {
        try {
            Files.write(Path.of(path), out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
