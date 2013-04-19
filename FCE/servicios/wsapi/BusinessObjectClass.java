package servicios.wsapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * @autor lpino
 * @Fecha Jul 28, 2003
*/
public abstract class BusinessObjectClass {
	protected java.lang.String sgteServicio;
	protected java.lang.String otherPage = "";
	protected HttpSession copySesion;
	protected HttpServletRequest copyRequest;
	protected HttpServletResponse copyResponse;
	protected static org.apache.log4j.Logger logs = org.apache.log4j.Logger.getLogger(BusinessObjectClass.class);
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
		otherPage = alias;
	}
}
