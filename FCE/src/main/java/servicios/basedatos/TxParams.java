package servicios.basedatos;
/**
 * @autor lpino
 * @Fecha Jul 31, 2003
*/
public class TxParams {
	private String consulta;
	private DBParserParams params;
	public int numeroPars;
	/**
	 * Constructor for TxParams.
	 */
	public TxParams() {
		super();
		params = new DBParserParams();
	}
	/**
	 * Returns the consulta.
	 * @return String
	 */
	public String getConsulta() {
		return consulta;
	}

	/**
	 * Returns the params.
	 * @return DBParserParams
	 */
	public DBParserParams getParams() {
		return params;
	}

	/**
	 * Sets the consulta.
	 * @param consulta The consulta to set
	 */
	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	/**
	 * Sets the params.
	 * @param params The params to set
	 */
	public void setParams(DBParserParams params) {
		this.params = params;
	}

}
