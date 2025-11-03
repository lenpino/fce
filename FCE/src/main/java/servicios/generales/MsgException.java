package servicios.generales;
/**
 * @autor lpino
 * @Fecha 07-may-03
*/
public class MsgException extends Exception {
	private int cod_error;
	/**
	 * Constructor for MsgException.
	 */
	public MsgException() {
		super();
	}
	/**
	 * Constructor for MsgException.
	 * @param s
	 */
	public MsgException(String s) {
		super(s);
	}
	/**
	 * Returns the cod_error.
	 * @return int
	 */
	public int getCod_error() {
		return cod_error;
	}

	/**
	 * Sets the cod_error.
	 * @param cod_error The cod_error to set
	 */
	public void setCod_error(int cod_error) {
		this.cod_error = cod_error;
	}

}
