package servicios.generales.ldap;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
public class ContextWrapper implements javax.servlet.http.HttpSessionBindingListener{
	private DirContext contexto = null;
	
	/**
	 * Gets the contexto
	 * @return Returns a InitialDirContext
	 */
	public DirContext getContexto() {
		return contexto;
	}
	/**
	 * Sets the contexto
	 * @param contexto The contexto to set
	 */
	public void setContexto(DirContext contexto) {
		this.contexto = contexto;
	}

	@Override
	protected void finalize() throws Throwable{
		this.contexto.close();
	}
	/**
	 * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}


	/**
	 * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		try{
				this.contexto.close();
		}
		catch(NamingException e){
			System.out.println("Error al cerrar la conexion LDAP " + e.getExplanation());
		}
	}


}

