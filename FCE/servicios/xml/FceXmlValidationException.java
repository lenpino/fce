package servicios.xml;

public class FceXmlValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] warnings = null;
	private String[] errors = null;
	private String[] fatals = null;
	
	public FceXmlValidationException(String[] warnings, String[] errors, String[] fatals) {
		super();
		this.warnings = warnings;
		this.errors = errors;
		this.fatals = fatals;
	}
	public String[] getErrorList(){
		return this.errors;
	}
	public String[] getWarningList(){
		return this.warnings;
	}
	public String[] getFatalList(){
		return this.fatals;
	}
}
