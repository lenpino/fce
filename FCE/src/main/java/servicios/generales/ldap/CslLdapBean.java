package servicios.generales.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


public class CslLdapBean extends LdapBean {
	private java.util.Properties conns = new java.util.Properties();
/**
 * CslLdapBean constructor comment.
 */
public CslLdapBean() {
	super();
}
	public synchronized Object executeAuthentication(String userid, String clave) throws servicios.generales.WSException {
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
					conns.put(userid, ctx);
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
			//			}
		}
		return lr;
	}
	public synchronized boolean isUserConnected(String userId) {
		return conns.containsKey(userId);
	}
	public synchronized void logOff(String userId) throws servicios.generales.WSException {
		try {
			InitialDirContext ctx = (InitialDirContext) conns.get(userId);
			if (ctx != null) {
				ctx.close();
				conns.remove(userId);
			}
			else {
		        result = new Integer(8);
			}

		}
		catch (NamingException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Error al desconectarse de LDAP: " + e.getExplanation());
			throw error;
		}

	}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
		try {
			CslLdapBean yo = new CslLdapBean();
			///////////////////////////////////////////////////////////
//			yo.init(args[0], args[1]);
			ListResult usuario = (ListResult) yo.executeAuthentication("usrprueba", "password");
			System.out.println("Usuario conectado");
			java.util.Properties atribs = new java.util.Properties();
			atribs.put("usua_activo", "0");
			yo.executeUpdate("usrprueba", atribs);
			ListResult user = (ListResult) yo.getUserData("usrprueba");
			/////////////////////////////////////////////////////////
			if (!yo.isUserConnected("usrprueba")) {
				java.util.Enumeration usuario2 = (java.util.Enumeration) yo.executeAuthentication("usrprueba", "password");
				System.out.println("Usuario conectado de nuevo");
			}
			else {
				System.out.println("El usuario ya esta conectado");
			}
			if (yo.correctCredentials("usrprueba", "password"))
				System.out.println("Credenciales aprobadas");
			else
				System.out.println("Credenciales incorrectas");
			yo.logOff("usrprueba");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

}
}
