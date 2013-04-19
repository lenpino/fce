package servicios.generales;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @autor lpino
 * @Fecha 15-may-03
*/
class SAXParserErrorHandler implements ErrorHandler {
    @Override
	public void warning(SAXParseException exception) throws SAXException {
         throw new SAXException("Advertencia encontrada: Linea: " + exception.getLineNumber() + " URI:  " + exception.getSystemId() + "  Mensaje: " + exception.getMessage());
    }
    @Override
	public void error(SAXParseException exception) throws SAXException {
         throw new SAXException("Error encontrado: Linea: " + exception.getLineNumber() + " URI:  " + exception.getSystemId() + "  Mensaje: " + exception.getMessage());
    }
    @Override
	public void fatalError(SAXParseException exception) throws SAXException {
         throw new SAXException("Error fatal encontrado: Linea: " + exception.getLineNumber() + " URI:  " + exception.getSystemId() + "  Mensaje: " + exception.getMessage());
    }
}