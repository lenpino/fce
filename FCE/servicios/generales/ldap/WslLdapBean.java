package servicios.generales.ldap;

/**
 * Insert the type's description here.
 * Creation date: (05-Jul-2002 11:11:21 AM)
 * @author: Administrator
 */

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
public class WslLdapBean extends LdapBean {
	public jakarta.servlet.http.HttpSession copySesion;
/**
 * WslLdapBean constructor comment.
 */
public WslLdapBean() {
	super();
}
	public Object executeAuthentication(String userid, String clave, jakarta.servlet.http.HttpServletRequest request) throws servicios.generales.WSException {
		javax.naming.NamingEnumeration resultado = null;
		ListResult lr = null;
		if (userid != null && clave != null) {
			StringBuffer aux = new StringBuffer();
			String lista = "";
			LdapAttrib atributos = null;

			env.put("example.search.base", this.getConfiguracion().getBaseNode());

			//Ciclo para armar la lista de atributos asociados a la busqueda en ca so de existir
			for (int i = 0; i < this.getConfiguracion().getAttribs().size(); i++) {
				atributos = (LdapAttrib) this.getConfiguracion().getAttribs().elementAt(i);
				aux = aux.append("(" + atributos.getAttribName() + "=" + atributos.getAttribValue() + ")");
			}

			lista = aux.toString();
			//Crea la secuencia para la busqueda
			env.put(
				"example.search.filter",
				"(&(objectclass=" + this.getConfiguracion().getClassObject() + ") (cn=" + userid + ") " + lista + ")");
			String base = env.getProperty("example.search.base");
			String filter = env.getProperty("example.search.filter");
			javax.naming.directory.SearchControls constraints = new javax.naming.directory.SearchControls();
			constraints.setSearchScope(javax.naming.directory.SearchControls.SUBTREE_SCOPE);
			try {
				DirContext ctx = bind(this.getConfiguracion().getLlave() + "=" + userid, clave);
				//Busca al usuario dentro del servicio LDAP
				if (ctx != null) {
					//Coloca la conexion del usuario en la lista
					setContextSession(ctx, request);
					resultado = ctx.search(base, filter, constraints);
					lr = getListResult(resultado);
					resultado.close();
				}
			}
			catch (javax.naming.CommunicationException e) {
				servicios.generales.WSException error = new servicios.generales.WSException("Problema Comunicacion con servicio LDAP: " + e.getExplanation());
				throw error;
			}
			catch (javax.naming.AuthenticationException e) {
		        result = new Integer(2);
			}
			catch (javax.naming.NamingException e) {
		        result = new Integer(1);
			}
			catch (Exception e) {
				servicios.generales.WSException error = new servicios.generales.WSException("Error: " + e.getMessage());
				throw error;
			}
			//			}
		}
		return lr;
	}
	private String getUsr(String strResult) {
		String foundusr = "";

		if (strResult == null)
			return null;

		try {
			java.util.StringTokenizer st = new java.util.StringTokenizer(strResult, " : ");

			// Se asume que el ULTIMO token siempre trae el nombre del usuario
			while (st.hasMoreTokens()) {
				foundusr = st.nextToken();
			}
		}
		catch (Exception e) {
			return null;
		}

		return foundusr;
	}
	private boolean isConnected(String usr) {

		java.util.Properties env = new java.util.Properties();

		env.put("java.naming.factory.initial", configuracion.getClaseLdap());
		env.put("java.naming.provider.url", this.getConfiguracion().getDireccion());
		env.put("java.naming.security.principal", configuracion.getLoginAdmin());
		env.put("java.naming.security.credentials", configuracion.getClaveAdmin());
		env.put("example.search.base", "cn=connections,cn=monitor");
		env.put("example.search.filter", "(objectclass=*)");

		String base = env.getProperty("example.search.base");
		String filter = env.getProperty("example.search.filter");
		javax.naming.directory.SearchControls constraints = new javax.naming.directory.SearchControls();
		constraints.setSearchScope(javax.naming.directory.SearchControls.OBJECT_SCOPE);

		try {

			javax.naming.directory.DirContext ctx = new javax.naming.directory.InitialDirContext(env);
			javax.naming.NamingEnumeration results = ctx.search(base, filter, constraints);

			while (results.hasMoreElements()) {
				javax.naming.directory.SearchResult sr = (javax.naming.directory.SearchResult) results.nextElement();
				javax.naming.directory.Attributes atts = sr.getAttributes();
				javax.naming.NamingEnumeration eatts = atts.getAll();
				while (eatts.hasMoreElements()) {
					javax.naming.directory.Attribute att = (javax.naming.directory.Attribute) eatts.next();
					javax.naming.NamingEnumeration vatt = att.getAll();
					while (vatt.hasMoreElements()) {
						String nomUsr = getUsr(vatt.next().toString());
						if (usr.compareToIgnoreCase(nomUsr) == 0)
							return true;
					}
				}
			}

			ctx.close();
			return false;
		}
		catch (javax.naming.NamingException e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}
	public boolean isUserConnected(String userId) {
		return isConnected(getConfiguracion().getLlave() + "=" + userId + "," + this.getConfiguracion().getBaseNode());
	}
	
	public void logOff(jakarta.servlet.http.HttpServletRequest req) throws servicios.generales.WSException {
		ContextWrapper contexto = null;
		DirContext ctx = null;
		try {
			copySesion = req.getSession(false);
			if(copySesion == null){
	private void setContextSession(DirContext contx, jakarta.servlet.http.HttpServletRequest req) throws servicios.generales.WSException, Exception {
			}
			contexto = (ContextWrapper) copySesion.getAttribute("contexto");
			if (contexto != null) {
				ctx = contexto.getContexto();
			}
			else {
				servicios.generales.WSException error = new servicios.generales.WSException("Sesion no valida");
				throw error;
			}
			if (ctx != null) {
				ctx.close();
			}
			else {
		        result = new Integer(2);
			}
		}
		catch (NamingException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Error al desconectarse de LDAP: " + e.getExplanation());
			throw error;
		}
		catch(Exception e){
			if(e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException)e;
			else
				throw new servicios.generales.WSException("Error no reconocido al desconectar el usuario desde LDAP");
		}
	}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
		try {
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
}
	private void setContextSession(DirContext contx, javax.servlet.http.HttpServletRequest req) throws servicios.generales.WSException, Exception {
		//Obtengo la sesion asociada a este usuario
		copySesion = req.getSession();
		//Creo un wrapper de contexto
		ContextWrapper context = (ContextWrapper) copySesion.getAttribute("contexto");
		if (context == null) {
			ContextWrapper contexto = new ContextWrapper();
			//Coloco la conexion en el wrapper
			contexto.setContexto(contx);
			//Coloco el wrapper en la sesion del usuario
			copySesion.setAttribute("contexto", contexto);
		}
		else {
			try {
				context.getContexto().getEnvironment();
				servicios.generales.WSException error = new servicios.generales.WSException("Usuario ya tiene conexion");
				throw error;
			}
			catch (Exception e) {
				if (e instanceof servicios.generales.WSException)
					throw e;
				else {
					ContextWrapper contexto = new ContextWrapper();
					//Coloco la conexion en el wrapper
					contexto.setContexto(contx);
					//Coloco el wrapper en la sesion del usuario
					copySesion.setAttribute("contexto", contexto);
				}
			}
		}
	}
}
