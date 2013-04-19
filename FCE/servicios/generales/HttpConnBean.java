package servicios.generales;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.net.ssl.HttpsURLConnection;

/**
 * @author Leonardo
 *
 */
public class HttpConnBean {
	private String sURI;
	private Document doc;
	
	public static String preSOAP = ""+
	"<SOAP-ENV:Envelope "+
	"xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" "+
	"xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
	"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
	"<SOAP-ENV:Body>";
	public static String postSOAP =""+
	"</SOAP-ENV:Body>"+
	"</SOAP-ENV:Envelope>"; 

	public HttpConnBean(String serverURI) {
		sURI = serverURI;
	}
	public StringBuffer sendRequest(Document docXml, String encoding) {
		try {
			//Crea el URL para establecer la comunicación
			URL url = new URL(sURI);
			// Crea la conección a partir de una conexion abierta por el URL
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//Configura la conexión para entrada y salida
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//Objeto encargado de llevar la información a traves del canal de comunicación
			OutputStream out = conn.getOutputStream();
			//Se crea un serializador con el objeto de traspaso de información y el formato de salida
			XMLSerializer ser = new XMLSerializer(out, new OutputFormat("xml", "UTF-8", false));
			//Se envia la información (XML) a traves del canal
			ser.serialize(docXml);
			//Se cierra el objeto que traspaso la información
			out.close();
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//Se crea un parser 
			//			DOMParser parser = new DOMParser();
			//Se lee desde la entrada con la conexión abierta
			//			parser.parse(new InputSource(conn.getInputStream()));
			//Se crea un documento para retornarlo
			//			docOut = parser.getDocument();
			//			InputSource in = new InputSource(conn.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer html = new StringBuffer();
			String linea = "";
			while ((linea = in.readLine()) != null)
				html.append(linea);
			in.close();
			return html;
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) {
		try {
			// Envia el XML al Servlet y recibe elmismo XML de respuesta
			//Creo un conjunto de parametros para ser posteados con la URL
			Properties parametros = new Properties();
						parametros.put("firstname","Leo");
			    		parametros.put("lastname","Pino");
			//    		parametros.put("Path","c:/Previred/Files/BcoChile.txt");
			//    		parametros.put("Periodo","052003");
			//    		parametros.put("UrlOrigen","");
			//    		parametros.put("Email","lpino@previred.com");
			//    		parametros.put("Id_Convenio","1234567");
			//			parametros.put("Accion","ValidaCarga");
			//			parametros.put("Identificador_Indexa","1234567890");
			//			parametros.put("Convenio","Bcochile");
			//			parametros.put("TipoCarga","2");
			HttpConnBean a = new HttpConnBean(args[0]);
			//			a.load(args[1]);
			System.out.println(a.sendPost(parametros,true));
			//			Document docOut = a.sendRequest();
			// Imprime el mismo XML por la consola 
			//			XMLSerializer ser = new XMLSerializer(System.out, null);
			//			ser.serialize(docOut);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public Document sendXML() {
		try {
			//Crea el URL para establecer la comunicación
			URL url = new URL(sURI);
			// Crea la conección a partir de una conexion abierta por el URL
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//Configura la conexión para salida
			conn.setDoOutput(true);
			//Objeto encargado de llevar la información a traves del canal de comunicación
			OutputStream out = conn.getOutputStream();
			//Se crea un serializador con el objeto de traspaso de información y el formato de salida
			XMLSerializer ser = new XMLSerializer(out, new OutputFormat("text", "ISO-8859-1", false));
			//Se envia la información (XML) a traves del canal
			ser.serialize(this.doc);
			System.out.println("Xml enviado a: " + sURI);
			//Se cierra el objeto que traspaso la información
			out.close();
			//Recibe el xml y genera un mensaje de respuesta
			return XmlBean.getDocXML(new InputSource(conn.getInputStream()),false);
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	public void load(String ArchivoXml) {
		try {
			// Open specified file
			InputStream is = new FileInputStream(ArchivoXml);
			// Start parsing
			DOMParser parser = new DOMParser();
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			parser.parse(new org.xml.sax.InputSource(is));
			this.doc = parser.getDocument(); // @XML4J
			// Document is well-formed
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public void sendXMLHttps() {
		try {
			HttpsURLConnection conn = null;
			System.getProperties().put("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
			java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			//Crea el URL para establecer la comunicación
			URL url = new URL(sURI);
			// Crea la conección a partir de una conexion abierta por el URL
			conn = (HttpsURLConnection) url.openConnection();
			//Configura la conexión para salida
			conn.setDoOutput(true);
			//Objeto encargado de llevar la información a traves del canal de comunicación
			OutputStream out = conn.getOutputStream();
			//Se crea un serializador con el objeto de traspaso de información y el formato de salida
			XMLSerializer ser = new XMLSerializer(out, new OutputFormat("xml", "UTF-8", false));
			//Se envia la información (XML) a traves del canal
			ser.serialize(this.doc);
			System.out.println("Xml enviado a: " + sURI);
			//Se cierra el objeto que traspaso la información
			out.close();
			String linea = "";
			java.io.BufferedReader arch = new java.io.BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((linea = arch.readLine()) != null)
				System.out.println(linea);
			//				XMLSerializer ser2 = new XMLSerializer(System.out, null);
			//				ser2.serialize(this.doc);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public Document sendXML(Document docXml, String encoding) throws MsgException {
		try {
			//Crea el URL para establecer la comunicación
			URL url = new URL(sURI);
			// Crea la conección a partir de una conexion abierta por el URL
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//Configura la conexión para salida
			conn.setDoOutput(true);
			//Objeto encargado de llevar la información a traves del canal de comunicación
			OutputStream out = conn.getOutputStream();
			//Se crea un serializador con el objeto de traspaso de información y el formato de salida
			XMLSerializer ser = new XMLSerializer(out, new OutputFormat("text", encoding, false));
			//Se envia la información (XML) a traves del canal
			ser.serialize(docXml);
			System.out.println("\rXml enviado a: " + sURI);
			//Se cierra el objeto que traspaso la información
			out.close();
			//Recibe el xml y genera un mensaje de respuesta
			return XmlBean.getDocXML(new InputSource(conn.getInputStream()),false);
		}
		catch (Throwable e) {
			throw new MsgException("Clase: XmlBridge - Error al enviar el mensaje al cliente Msg: " + e.getMessage());
		}
	}
	public StringBuffer sendPost(Properties params) {
		URL url;
		DataOutputStream printout;
		String content = null;
		try {
			url = new URL(sURI);
			// URL connection channel.
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// Dejo saber al run-time que no quiero recibir.
			urlConn.setDoInput(true);
			// Dejo saber al run-time que quiero enviar.
			urlConn.setDoOutput(true);
			// Desabilito el cache.
			urlConn.setUseCaches(false);
			// Especifico el  tipo del contenido.
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// Send POST output.
			printout = new DataOutputStream(urlConn.getOutputStream());
			//Extraigo los parametros a enviar
			if (!params.isEmpty()) {
				Enumeration e = params.keys();
				StringBuffer buffer = new StringBuffer();
				while (e.hasMoreElements()) {
					String aux = (String) e.nextElement();
					buffer.append(aux + "=" + params.getProperty(aux) + "&");
				}
				content = (buffer.deleteCharAt(buffer.length() - 1)).toString();
			}
			else
				content = "";
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			// Get response data.
			System.out.println("Termine :-)");
			java.io.BufferedReader arch = new java.io.BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			StringBuffer html = new StringBuffer();
			String linea = "";
			while ((linea = arch.readLine()) != null)
				html.append(linea);
			arch.close();
			return html;
		}
		catch (MalformedURLException e) {
			return null;
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}
/**
 * 
 * @param sURI
 */	public void setSURI(String sURI) {
		this.sURI = sURI;
	}
	public StringBuffer sendPost(Properties params, boolean salida) {
		URL url;
		DataOutputStream printout;
		String content = null;
		try {
			url = new URL(sURI);
			// URL connection channel.
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// Dejo saber al run-time que no quiero recibir.
			urlConn.setDoInput(salida);
			// Dejo saber al run-time que quiero enviar.
			urlConn.setDoOutput(true);
			// Desabilito el cache.
			urlConn.setUseCaches(false);
			// Especifico el  tipo del contenido.
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// Send POST output.
			printout = new DataOutputStream(urlConn.getOutputStream());
			//Extraigo los parametros a enviar
			if (!params.isEmpty()) {
				Enumeration e = params.keys();
				StringBuffer buffer = new StringBuffer();
				while (e.hasMoreElements()) {
					String aux = (String) e.nextElement();
					buffer.append(aux + "=" + params.getProperty(aux) + "&");
				}
				content = (buffer.deleteCharAt(buffer.length() - 1)).toString();
			}
			else
				content = "";
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			if (salida) {
				// Get response data.
				System.out.println("Termine :-)");
				java.io.BufferedReader arch = new java.io.BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				StringBuffer html = new StringBuffer();
				String linea = "";
				while ((linea = arch.readLine()) != null)
					html.append(linea);
				arch.close();
				urlConn.disconnect();
				return html;
			}
			else{
				urlConn.disconnect();
				return null;
			}
		}
		catch (MalformedURLException e) {
			return null;
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}
	public InputStream sendRequest(Properties params) {
		URL url;
		DataOutputStream printout;
		String content = null;
		try {
			url = new URL(sURI);
			// URL connection channel.
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// Dejo saber al run-time que no quiero recibir.
			urlConn.setDoInput(true);
			// Dejo saber al run-time que quiero enviar.
			urlConn.setDoOutput(true);
			// Desabilito el cache.
			urlConn.setUseCaches(false);
			// Especifico el  tipo del contenido.
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// Send POST output.
			printout = new DataOutputStream(urlConn.getOutputStream());
			//Extraigo los parametros a enviar
			if (!params.isEmpty()) {
				Enumeration e = params.keys();
				StringBuffer buffer = new StringBuffer();
				while (e.hasMoreElements()) {
					String aux = (String) e.nextElement();
					buffer.append(aux + "=" + params.getProperty(aux) + "&");
				}
				content = (buffer.deleteCharAt(buffer.length() - 1)).toString();
			}
			else
				content = "";
			printout.writeBytes(content);
			printout.flush();
			printout.close();
			// Get response data.
			System.out.println("Termine :-)");
			InputStream salida = urlConn.getInputStream();
//			urlConn.disconnect();
			return salida;
		}
		catch (MalformedURLException e) {
			return null;
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
/**
 * @author	Daniel Sepulveda
 * Envia un mensaje XML agregando la formalidad necesaria para SOAP
 * @param url: Dirección de destino (usualmente un WebService)
 * @param soapIn: Contenido XML del mensaje SOAP
 * @return Una cadena con la respuesta del WebService
 */	public static String sendSOAPMessage(String url,String soapIn){
		URL                 aURL;
		HttpURLConnection   aC;
		OutputStreamWriter  oS;
		String              line;
//		StringBuffer        soap;
//		byte[]              mensaje;
		String              xmlInput = "";
        
		// se lee el xml de entrada al Webservice
		/*
		try {
			InputStream bIn = in;
			int aux = bIn.read();
			while ( aux > 0 ) {
				xmlInput += (char)aux;
				aux = bIn.read();
			}           
			bIn.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
			System.exit( 0 );
		} */                       
     	xmlInput = soapIn;
		try {                       
			aURL = new URL(url);
			aC = (HttpURLConnection) aURL.openConnection();
			aC.setDoOutput( true );
			aC.setDoInput( true );
			aC.setRequestMethod( "GET" );
			aC.setUseCaches( false );
			aC.setAllowUserInteraction( false ); 
			aC.setRequestProperty( "Content-Length", Integer.toString( xmlInput.length() )
);  
			aC.setRequestProperty( "Content-Type", "text/xml; charset=utf-8" );
			aC.setRequestProperty( "SOAPAction", "" );                
                        
			oS = new OutputStreamWriter( aC.getOutputStream() );
			oS.write( xmlInput );
			oS.flush();
			oS.close();
            
			InputStream iS = aC.getInputStream();
			BufferedReader bR = new BufferedReader( new InputStreamReader( iS ) );
			String salida = "";
            
			line = bR.readLine();
			while( line != null ) {
				System.out.println( line );
				salida +=line;
				line = bR.readLine();
			}
			bR.close();
			return salida;
		}
		catch( Exception e ) {
			e.printStackTrace();
			System.exit( 0 );
		}
		return null;
	}
}