package servicios.generales;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringJoiner;

/**
 * Versión modernizada de HttpConnBean (Java 11+).
 * - Usa HttpClient en vez de HttpURLConnection.
 * - Sin dependencias Xerces ni com.sun.net.ssl.
 * - Serializa XML con JAXP Transformer.
 * - Métodos conservan nombres y tipos de retorno donde es razonable.
 */
public class HttpConnBean {

    private URI uri;
    private Document doc;

    // Un solo HttpClient reutilizable (thread-safe)
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public static String preSOAP = ""
            + "<SOAP-ENV:Envelope "
            + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" "
            + "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<SOAP-ENV:Body>";

    public static String postSOAP = ""
            + "</SOAP-ENV:Body>"
            + "</SOAP-ENV:Envelope>";

    public HttpConnBean(String serverURI) {
        this.uri = URI.create(serverURI);
    }

    /**
     * Envía un XML y retorna el body como StringBuffer.
     * (Mantiene firma de retorno tipo StringBuffer para compatibilidad.)
     */
    public StringBuffer sendRequest(Document docXml, String encoding) {
        try {
            Charset cs = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
            String body = postXmlForText(docXml, cs);
            return body != null ? new StringBuffer(body) : null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envía el this.doc y retorna un Document parseado desde la respuesta.
     */
    public Document sendXML() {
        try {
            return postXmlForDocument(this.doc, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Carga un Document desde archivo (sin Xerces, solo JAXP).
     */
    public void load(String archivoXml) {
        try (InputStream is = new FileInputStream(archivoXml)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            // Si quieres validar contra XSD, agrega: dbf.setSchema(schema);
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(is);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * HTTPS ya es soportado automáticamente por HttpClient.
     * Mantengo el método por compatibilidad, delega a sendXML().
     */
    @Deprecated
    public void sendXMLHttps() {
        Document ignored = sendXML();
        if (ignored != null) {
            try {
                System.out.println(documentToString(ignored, StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Envía un XML y retorna el Document de respuesta. Lanza tu MsgException custom.
     */
    public Document sendXML(Document docXml, String encoding) throws MsgException {
        try {
            Charset cs = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
            Document out = postXmlForDocument(docXml, cs);
            System.out.println("\rXml enviado a: " + uri);
            return out;
        } catch (Throwable e) {
            throw new MsgException("Clase: HttpConnBean - Error al enviar el mensaje: " + e.getMessage());
        }
    }

    /**
     * POST application/x-www-form-urlencoded, leyendo respuesta.
     * (Sigue retornando StringBuffer para no romper llamadas existentes.)
     */
    public StringBuffer sendPost(Properties params) {
        try {
            String form = encodeForm(params, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new StringBuffer(resp.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Igual a sendPost, pero opcionalmente no lee respuesta.
     */
    public StringBuffer sendPost(Properties params, boolean salida) {
        try {
            String form = encodeForm(params, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();

            if (!salida) {
                http.send(req, HttpResponse.BodyHandlers.discarding());
                return null;
            } else {
                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                return new StringBuffer(resp.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve un InputStream de la respuesta (el caller debe cerrarlo).
     */
    public InputStream sendRequest(Properties params) {
        try {
            String form = encodeForm(params, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
            // OJO: el caller es responsable de cerrar este InputStream
            return resp.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Setter de URI (compatibilidad con tu API anterior). */
    public void setSURI(String sURI) {
        this.uri = URI.create(sURI);
    }

    /**
     * Envia un mensaje SOAP por POST. Corrige el método (antes era GET).
     */
    public static String sendSOAPMessage(String url, String soapIn) {
        try {
            HttpClient http = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            Charset cs = StandardCharsets.UTF_8;
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "text/xml; charset=" + cs.name())
                    .header("SOAPAction", "") // ajusta si tu WS requiere un valor específico
                    .POST(HttpRequest.BodyPublishers.ofString(soapIn, cs))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(cs));
            return resp.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ====================== Helpers internos ====================== */

    private String postXmlForText(Document docXml, Charset cs) throws Exception {
        String xml = documentToString(docXml, cs);
        HttpRequest req = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/xml; charset=" + cs.name())
                .header("Accept", "application/xml, text/xml, */*")
                .POST(HttpRequest.BodyPublishers.ofString(xml, cs))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(cs));
        return resp.body();
    }

    private Document postXmlForDocument(Document docXml, Charset cs) throws Exception {
        String xml = documentToString(docXml, cs);
        HttpRequest req = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/xml; charset=" + cs.name())
                .header("Accept", "application/xml, text/xml, */*")
                .POST(HttpRequest.BodyPublishers.ofString(xml, cs))
                .build();

        HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream is = resp.body()) {
            return parseXml(is);
        }
    }

    private static String documentToString(Document doc, Charset charset) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, charset.name());
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    private static Document parseXml(InputStream in) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(in);
    }

    private static String encodeForm(Properties params, Charset cs) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) return "";
        StringJoiner joiner = new StringJoiner("&");
        Enumeration<?> e = params.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String val = params.getProperty(key, "");
            joiner.add(URLEncoder.encode(key, cs.name()) + "=" + URLEncoder.encode(val, cs.name()));
        }
        return joiner.toString();
    }

    /* Demo simple (opcional): */
    public static void main(String[] args) {
        try {
            Properties parametros = new Properties();
            parametros.put("firstname", "Leo");
            parametros.put("lastname", "Pino");

            HttpConnBean a = new HttpConnBean(args[0]);
            System.out.println(a.sendPost(parametros, true));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
