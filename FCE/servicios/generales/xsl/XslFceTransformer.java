package servicios.generales.xsl;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

public abstract class XslFceTransformer {
	
	protected Document docXsl = null;
	protected Document docXml = null;
	protected Object resultado = null;

	public XslFceTransformer() {
		super();
	}

	public Document getDocXml() {
		return docXml;
	}

	public void setDocXml(Document docXml) {
		this.docXml = docXml;
	}

	public Document getDocXsl() {
		return docXsl;
	}

	public void setDocXsl(Document docXsl) {
		this.docXsl = docXsl;
	}
	
	abstract public void transform();
	abstract public void retornaSalida(HttpServletResponse res);
}
