package servicios.generales.ldap;

/**
 * Insert the type's description here.
 * Creation date: (26-03-2001 08:06:08 PM)
 * @author: Leonardo Pino
 * 25-07-2001:	Metodo execute retorna Integer si el usuario no fue encontrado en el servidor LDAP
 * Para borrar un usuario es necesario utilizar la siguiente linea:
 * ctx.destroySubcontext(name);
 * Donde name es por ejemplo: cn=usuarioPrueba  (todo!)
 */
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

import servicios.wsapi.ETCBeanInterface;

public class LdapBean implements ETCBeanInterface{
	public jakarta.servlet.http.HttpSession copySesion;
	public jakarta.servlet.http.HttpServletRequest copyRequest;
	public static int MAX_INTENTOS = 3;
	public java.util.Properties env = new java.util.Properties();
	protected LdapConfig configuracion;
	protected Object result = null;

	/**
	 * Ldap constructor comment.
	 */
	public LdapBean() {
		super();
	}
/**
 * Insert the method's description here.
 * Creation date: (05-Jul-2002 3:15:26 PM)
 * @param login java.lang.String
 * @param password java.lang.String
 */
public void autenticate(String login, String password) throws servicios.generales.WSException {

    env.put("example.search.base", this.getConfiguracion().getBaseNode());
    env.put("example.search.filter", "(&(objectclass=" + this.getConfiguracion().getClassObject() + ") (" + this.getConfiguracion().getLlave() + "=" + login + ") )");
    String base = env.getProperty("example.search.base");
    String filter = env.getProperty("example.search.filter");

    javax.naming.directory.SearchControls constraints = new javax.naming.directory.SearchControls();
    constraints.setSearchScope(javax.naming.directory.SearchControls.SUBTREE_SCOPE);

    try {
        DirContext ctx =bind(this.getConfiguracion().getLlave() + "=" + login,password);
        //Busca al usuario dentro del servicio LDAP
        ctx.search(base, filter, constraints);
        ctx.close();
    } catch (javax.naming.CommunicationException e) {
        servicios.generales.WSException error = new servicios.generales.WSException("Problema Comunicacion con servicio LDAP: " + e.getExplanation());
        throw error;
    } catch (javax.naming.AuthenticationException e) {
        result = new Integer(2);
    } catch (javax.naming.NamingException e) {
        System.out.println("Razon: " + e.getExplanation());
        result = new Integer(1);
    } catch (Exception e) {
        servicios.generales.WSException error = new servicios.generales.WSException("Error al autentificar el usuario: " + e.getMessage());
        throw error;
    }
}
	protected InitialDirContext bind(String user, String password) throws NamingException {
		env.put("java.naming.factory.initial", configuracion.getClaseLdap());
		env.put(Context.PROVIDER_URL, this.getConfiguracion().getDireccion());
		env.put(Context.SECURITY_PRINCIPAL, user + "," + this.getConfiguracion().getBaseNode());
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		DirContext root = new InitialDirContext(env);
		return (InitialDirContext) root;
	}
	private InitialDirContext bindAnonimo() throws NamingException {
		env.put("java.naming.factory.initial", configuracion.getClaseLdap());
		env.put(Context.PROVIDER_URL, this.getConfiguracion().getDireccion());
		env.put(Context.SECURITY_AUTHENTICATION, "none");

		DirContext root = new InitialDirContext(env);
		//		System.out.println("Usuario conectado");
		return (InitialDirContext) root;
	}
	public boolean correctCredentials(String userid, String pin) throws servicios.generales.WSException {
		javax.naming.NamingEnumeration resultado = null;

		StringBuffer aux = new StringBuffer();
		String lista = "";
		LdapAttrib atributos = null;

		env.put("java.naming.factory.initial", this.getConfiguracion().getClaseLdap());
		env.put("java.naming.provider.url", this.getConfiguracion().getDireccion());
		env.put(
			"java.naming.security.principal",
			this.getConfiguracion().getLlave() + "=" + userid + "," + this.getConfiguracion().getBaseNode());
		env.put("java.naming.security.credentials", pin);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put("example.search.base", this.getConfiguracion().getBaseNode());

		//Ciclo para armar la lista de atributos asociados a la busqueda
		for (int i = 0; i < this.getConfiguracion().getAttribs().size(); i++) {
			atributos = (LdapAttrib) this.getConfiguracion().getAttribs().elementAt(i);
			aux = aux.append("(" + atributos.getAttribName() + "=" + atributos.getAttribValue() + ")");
		}

		lista = aux.toString();
		env.put(
			"example.search.filter",
			"(&(objectclass=" + this.getConfiguracion().getClassObject() + ") (cn=" + userid + ") " + lista + ")");
		String base = env.getProperty("example.search.base");
		String filter = env.getProperty("example.search.filter");
		javax.naming.directory.SearchControls constraints = new javax.naming.directory.SearchControls();
		constraints.setSearchScope(javax.naming.directory.SearchControls.SUBTREE_SCOPE);
		try {
			DirContext ctx = new InitialDirContext(env);
			//Busca al usuario dentro del servicio LDAP
			resultado = ctx.search(base, filter, constraints);
			resultado.close();
			ctx.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	public void executeDelete(String login) throws servicios.generales.WSException {
		try {
//			java.util.Enumeration enum;
			String llave = null;
			InitialDirContext root = login(configuracion.getLoginAdmin(), configuracion.getClaveAdmin());

			// Add a new user object to the Engineering container
			DirContext ctx = (DirContext) root.lookup(configuracion.getBaseNode());

			// Borra el usuario
			ctx.destroySubcontext(this.configuracion.getLlave() + "=" + login);
			ctx.close();
			root.close();
		}
		catch (javax.naming.CommunicationException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Problema Comunicacion con servicio LDAP: " + e.getExplanation());
			throw error;
		}
		catch (javax.naming.NameAlreadyBoundException e) {
	        result = new Integer(4);
		}
		catch (javax.naming.directory.SchemaViolationException e) {
	        result = new Integer(3);
		}
		catch (NamingException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Error desconocido en servicio LDAP: " + e.getExplanation());
			throw error;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/01 9:32:30 am)
	 */
	public void executeInsert(String login, Properties user) throws servicios.generales.WSException {
		try {
			java.util.Enumeration enumeracion;
			String llave = null;
			InitialDirContext root = login(configuracion.getLoginAdmin(), configuracion.getClaveAdmin());

			// Add a new user object to the Engineering container
			DirContext ctx = (DirContext) root.lookup(configuracion.getBaseNode());
			Attributes attrs = new BasicAttributes();

			attrs.put(new BasicAttribute("objectclass", configuracion.getClassObject()));

			// Agrega los atributos uno por uno
			enumeracion = user.keys();
			while (enumeracion.hasMoreElements()) {
				llave = (String) enumeracion.nextElement();
				attrs.put(llave, user.getProperty(llave));
			}

			// Agrega el usuario
			ctx.createSubcontext(this.configuracion.getLlave() + "=" + login, attrs);
			ctx.close();
			root.close();
		}
		catch (javax.naming.CommunicationException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Problema Comunicacion con servicio LDAP: " + e.getExplanation());
			throw error;
		}
		catch (javax.naming.NameAlreadyBoundException e) {
	        result = new Integer(4);
		}
		catch (javax.naming.directory.SchemaViolationException e) {
	        result = new Integer(3);
		}
		catch (NamingException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Error desconocido en servicio LDAP: " + e.getExplanation());
			throw error;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/24/01 9:32:30 am)
	 */
	public void executeUpdate(String login, Properties user) throws servicios.generales.WSException {
		try {
			java.util.Enumeration enumeracion;
			String llave, llave_primaria = null;
			InitialDirContext root = login(configuracion.getLoginAdmin(), configuracion.getClaveAdmin());

			// Add a new user object to the Engineering container
			DirContext ctx = (DirContext) root.lookup(configuracion.getBaseNode());
			Attributes attrs = new BasicAttributes();

			attrs.put(new BasicAttribute("objectclass", this.getConfiguracion().getClassObject()));

			// Items a modificar son solo los que vienen en el Properties
			ModificationItem[] mods = new ModificationItem[user.size()];
			int i = 0;
			enumeracion = user.keys();
			while (enumeracion.hasMoreElements()) {
				llave = (String) enumeracion.nextElement();
				mods[i] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(llave, user.getProperty(llave)));
				i++;
			}

			/**
			 * Dentro de las constantes que el DirContext posee tambien estan:
			 * ADD_ATTRIBUTE:		Agrega un atributo al usuario (tarea de los administradores)
			 * REMOVE_ATTRIBUTE :	Borra un atributo (tarea de los administradores)
			 */

			// Modifica objeto        
			ctx.modifyAttributes(this.configuracion.getLlave() + "=" + login, mods);
			ctx.close();
			root.close();
		}
		catch (javax.naming.CommunicationException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Problema Comunicacion con servicio LDAP: " + e.getExplanation());
			throw error;
		}
		catch (javax.naming.NameAlreadyBoundException e) {
	        result = new Integer(4);
		}
		catch (javax.naming.directory.SchemaViolationException e) {
	        result = new Integer(3);
		}
		catch (NamingException e) {
			servicios.generales.WSException error = new servicios.generales.WSException("Error desconocido en servicio LDAP: " + e.getExplanation());
			throw error;
		}
		//		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26-03-2001 08:57:27 PM)
	 * @return servicios.generales.LdapConfig
	 */
	protected LdapConfig getConfiguracion() {
		return configuracion;
	}
	public static ListResult getListResult(javax.naming.NamingEnumeration namingEnum) throws servicios.generales.WSException {

		ListResult lr = new ListResult();

		try {

			while (namingEnum.hasMoreElements()) {
				javax.naming.directory.SearchResult sr = (javax.naming.directory.SearchResult) namingEnum.nextElement();
				javax.naming.directory.Attributes atts = sr.getAttributes();
				javax.naming.NamingEnumeration eatts = atts.getAll();

				ObjResult os = new ObjResult();
				while (eatts.hasMoreElements()) {
					javax.naming.directory.Attribute att = (javax.naming.directory.Attribute) eatts.next();
					String nameAtt = att.getID();
					javax.naming.NamingEnumeration vatt = att.getAll();

					ValueResult vs = new ValueResult();
					while (vatt.hasMoreElements()) {
						String valAtt = vatt.next().toString();
						vs.add(valAtt);
					}

					AttribResult ar = new AttribResult(nameAtt, vs);
					os.add(ar);
				}
				lr.add(os);
			}
			return lr;
		}
		catch (javax.naming.ReferralException re) {
			throw new servicios.generales.WSException("Error getListResult: " + re);
		}
		catch (javax.naming.NamingException ne) {
			throw new servicios.generales.WSException("Error getListResult: " + ne);
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Error getListResult: " + e);
		}
	}
	public Object getUserData(String login) throws servicios.generales.WSException {
		ListResult lr = null;
//		java.util.Enumeration enum;
		String llave, llave_primaria = null;
		env.put("example.search.base", this.getConfiguracion().getBaseNode());
		env.put("example.search.filter", "(&(objectclass=" + this.getConfiguracion().getClassObject() + ") (cn=" + login + ") )");

		String base = env.getProperty("example.search.base");
		String filter = env.getProperty("example.search.filter");
		javax.naming.directory.SearchControls constraints = new javax.naming.directory.SearchControls();
		constraints.setSearchScope(javax.naming.directory.SearchControls.SUBTREE_SCOPE);
		try {
			InitialDirContext root = bindAnonimo();

			javax.naming.NamingEnumeration retorno = root.search(base, filter, constraints);
			lr = getListResult(retorno);
			retorno.close();
			root.close();
			return lr;
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
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/04/01 11:22:13 am)
	 * @return javax.naming.directory.InitialDirContext
	 * @param ipaddr java.lang.String
	 * @param user java.lang.String
	 * @param password java.lang.String
	 */
	private InitialDirContext login(String user, String password) throws NamingException {
		env.put("java.naming.factory.initial", configuracion.getClaseLdap());
		env.put(Context.PROVIDER_URL, this.getConfiguracion().getDireccion());
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		InitialDirContext root = new InitialDirContext(env);
		return root;
	}
	public static void main(String[] args) {
		try {
			LdapBean yo = new LdapBean();
			///////////////////////////////////////////////////////////
			yo.autenticate("usrprueba", "password");
			System.out.println("Usuario conectado");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26-03-2001 08:57:27 PM)
	 * @param newConfiguracion servicios.generales.LdapConfig
	 */
	private void setConfiguracion(LdapConfig newConfiguracion) {
		configuracion = newConfiguracion;
	}
	/**
	 * setParameters method comment.
	 */
	@Override
	public void setParameters (java.lang.Object request) {
		setConfiguracion((LdapConfig) request);
	}
/**
 * init method comment.
 */
@Override
public void init(Object parametros) {}

/**
 * setContext method comment.
 */
@Override
public void setContext(jakarta.servlet.http.HttpServletRequest req) {
	this.copySesion = req.getSession(false);
	this.copyRequest = req;	
}

/**
 * init method comment.
 */
@Override
public void init() throws servicios.generales.WSException {}

/**
 * getResult method comment.
 */
@Override
public java.lang.Object getResult() {
	return result;
}

@Override
public void execute() throws servicios.generales.WSException {
	//Para la ejecución del bean se espera recibir la información requerida desde el REQUEST
	//USUARIO = usuario (String)
	//CLAVE = clave (String)
	//ATRIBUTOS = atribs (Properties)
	//OPERACION = codop (int)
	//				1 = Autentificar
	//				2 = Borrar usuario
	//				3 = Agregar usuario
	//				4 = Modifica atributos del usuario
	//				5 = Verifica credenciales
	try{
		int operacion = ((Integer)copyRequest.getAttribute("codop")).intValue();
		switch(operacion){
		case(1):
			autenticate((String)copyRequest.getAttribute("usuario"), (String)copyRequest.getAttribute("clave"));
		case(2):
			executeDelete((String)copyRequest.getAttribute("usuario"));
		case(3):
			executeInsert((String)copyRequest.getAttribute("usuario"), (Properties)copyRequest.getAttribute("atribs"));
		case(4):
			executeUpdate((String)copyRequest.getAttribute("usuario"), (Properties)copyRequest.getAttribute("atribs"));
		case(5):{
			Boolean bool = Boolean(correctCredentials((String)copyRequest.getAttribute("usuario"), (String)copyRequest.getAttribute("clave")));
			result = bool;
		}
		case(6):
			result = getUserData((String)copyRequest.getAttribute("usuario"));
		}
	}
	catch(Exception e){
	}
}

/**
 * Method Boolean.
 * @param b
 * @return Boolean
 */
private Boolean Boolean(boolean b) {
	return null;
}

}
