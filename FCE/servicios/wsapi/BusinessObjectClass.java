package servicios.wsapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @autor lpino
 * @Fecha Jul 28, 2003
*/
public abstract class BusinessObjectClass {
	protected String sgteServicio;
	protected String otherPage = "";
	protected HttpSession copySesion;
	protected HttpServletRequest copyRequest;
	protected HttpServletResponse copyResponse;
	protected static final Logger logs = LogManager.getLogger(BusinessObjectClass.class);
	protected Object resultado;

	/**
	 * Constructor for BusinessObjectClass.
	 */
	public BusinessObjectClass() {
		super();
	}

	public String getJspAkaPage(){
		return otherPage;
	}

	public String nextService(){
		return sgteServicio;
	}

	public void setThreadParams(HttpServletRequest request, HttpServletResponse response){
		this.copyRequest = request;
		this.copyResponse = response;
	}

	public void setJspAkaPage(String alias){
		this.otherPage = alias;
	}
}
