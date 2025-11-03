/*
 * Created on 15-jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package servicios.wsapi;


/**
 * @author lpino
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface FceSecurityInterface {
	public void setData(jakarta.servlet.http.HttpServletRequest data);
	public boolean isProgramSecure();
	public void writeLog();
	public int getErrorCode();
}
