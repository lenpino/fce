package servicios.xml;

import java.util.ArrayList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FceXmlExceptionHandler implements ErrorHandler {
	private ArrayList warnings = new ArrayList();
	private ArrayList errors = new ArrayList();
	private ArrayList fatals = new ArrayList();
	private String muleta = "";
	private boolean lethal = false;
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		muleta  = new String(
			"Linea=" + exception.getLineNumber() + 
			"|Columna="+exception.getColumnNumber()+
			"|PublicId="+exception.getPublicId()+
			"|SystemId=" + exception.getSystemId() + 
			"|Mensaje=" + exception.getLocalizedMessage());
		warnings.add(muleta);
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		muleta = new String(
			"Linea=" + exception.getLineNumber() + 
			"|Columna="+exception.getColumnNumber()+
			"|PublicId="+exception.getPublicId()+
			"|SystemId=" + exception.getSystemId() + 
			"|Mensaje=" + exception.getLocalizedMessage());
		errors.add(muleta);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		muleta = new String(
			"Linea=" + exception.getLineNumber() + 
			"|Columna="+exception.getColumnNumber()+
			"|PublicId="+exception.getPublicId()+
			"|SystemId=" + exception.getSystemId() + 
			"|Mensaje=" + exception.getLocalizedMessage());
		fatals.add(muleta);
	}

	public String[] getErrors() {
		String[] listaErrores = new String[this.errors.size()];
		for(int i=0;i<this.errors.size();i++)
			listaErrores[i] = (String)this.errors.get(i);
		return listaErrores;
	}

	public String[] getFatals() {
		String[] listaLetales = new String[this.fatals.size()];
		for(int i=0;i<this.fatals.size();i++)
			listaLetales[i] = (String)this.fatals.get(i);
		return listaLetales;
	}

	public String[] getWarnings() {
		String[] listaAdvertencias = new String[this.warnings.size()];
		for(int i=0;i < this.warnings.size();i++){
			listaAdvertencias[i] = (String)this.warnings.get(i);
		}
		return listaAdvertencias;
	}

	public boolean isLethal() {
		if(this.fatals.size() > 0)
			lethal = true;
		return lethal;
	}
}
