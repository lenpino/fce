package servicios.generales.xsl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class XslXml2PDF extends XslFceTransformer {
	
	private FopFactory fopFactory = FopFactory.newInstance();
	private TransformerFactory tFactory = TransformerFactory.newInstance();
	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Override
	public void retornaSalida(HttpServletResponse res) {		
	    try {

			//Prepare response
			res.setContentType("application/pdf");
			res.setContentLength(out.size());			
			//Send content to Browser
			res.getOutputStream().write(out.toByteArray());
			res.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void transform() {
	    try {
			//Setup FOP
			Fop fop = fopFactory.newFop(org.apache.xmlgraphics.util.MimeConstants.MIME_PDF, out);
			//Utiliza el XSL configurado como salida para este servicio
			DOMSource xsltSrc = new DOMSource(this.docXsl);
			Transformer transformer = tFactory.newTransformer(xsltSrc);
			//Se asegura que la salida sea a trevés de FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			//Setup input
			DOMSource src = new DOMSource(this.docXml);
			//Start the transformation and rendering process
			transformer.transform(src, res);
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ByteArrayOutputStream getOutputStream(){
		return this.out;
	}
	public void savePDF(String path){
		try {
			FileOutputStream file = new FileOutputStream(path);
			file.write(out.toByteArray());
			file.flush();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
