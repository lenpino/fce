package servicios.basedatos;

import java.util.Properties;

/**
 * @author lpino
 * 19-08-2004:	El constructor inicializa la memoria de las variables de instancia
 *
 */
public class DBParserParams {
	
	public java.util.Properties entrada = null;
	public java.util.Properties salida = null;
	
	/**
	 * Constructor for DBParserParams.
	 */
	public DBParserParams() {
		super();
		salida = new Properties();
		entrada = new Properties();
	}
	/**
	 * Returns the entrada.
	 * @return java.util.Properties
	 */
	public java.util.Properties getEntrada() {
		return entrada;
	}

	/**
	 * Returns the salida.
	 * @return java.util.Properties
	 */
	public java.util.Properties getSalida() {
		return salida;
	}

}
