package servicios.control;

/**
 * Insert the type's description here.
 * Creation date: (13-09-2000 11:42:19 AM)
 * @author: Leonardo Pino
 * 05-01-2001:	Agrega variable de instancia pagina
 * 11-01-2001:	Agrega Variable de instancia datosPaginacion
 * 28-05-2001:	Agrega lo necesario para que los servicios tengan una secuencia ordenada segun su
 *							aparición en el XML y puedan con el token "sgte" pasar al siguiente servicio sin conocer
 *							su nombre
 * 11-07-2005:	Agrega el atributo con el nombre del objeto de control de errores
 * 15-07-2005:	Agrega el atributo con el nombre del objeto de seguridad  requerido
 * 20-10-2005:	Agrega el atributo para la invalidacion de la sesion
 * 18-12-2006:	Agrega atributo para dar soporte a Hibernate
 */
public class ProgRequest {
	protected java.lang.String nombre = "";
	protected boolean pagina;
	protected java.lang.String jspTocall = "";
	protected java.util.Properties listServices;
	protected java.lang.String initService;
	protected String objetoControl;
	protected String objetoSeguridad;
	protected boolean invalidaSesion = true;
	protected boolean hibernate = false;
	public servicios.control.PageData datosPaginacion = new servicios.control.PageData();
	private int orden = 0;
	private java.util.Properties listOrden;
/**
 * ProgRequest constructor comment.
 */
public ProgRequest() {
	super();
	listServices = new java.util.Properties();
	listOrden = new java.util.Properties();
}
/**
 * Insert the method's description here.
 * Creation date: (15-09-2000 11:29:19 AM)
 * @param key java.lang.String
 * @param value servicios.control.Service
 */
public void addService(String key, Service value) {
	getListServices().put(key,value);
	getListOrden().put(new Integer(orden),key);
	orden++;
}
/**
 * Insert the method's description here.
 * Creation date: (05-01-2001 05:04:26 PM)
 * @return boolean
 */
public boolean generaPaginas() {
	return this.pagina;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:32:15 PM)
 * @return java.lang.String
 */
public java.lang.String getInitService() {
	return initService;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:44:48 AM)
 * @return java.lang.String
 */
public java.lang.String getJspTocall() {
	return jspTocall;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:43:44 AM)
 * @return int
 */
public int getLargo() {
	return getListServices().size();
}
/**
 * Insert the method's description here.
 * Creation date: (28-05-2001 04:36:31 PM)
 * @return java.util.Properties
 */
public java.util.Properties getListOrden() {
	return listOrden;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:45:32 AM)
 * @return java.util.Properties
 */
public java.util.Properties getListServices() {
	return listServices;
}
/**
 * Insert the method's description here.
 * Creation date: (28-05-2001 05:58:22 PM)
 * @return java.lang.String
 */
public String getNextService(String lastkey) throws servicios.generales.WSException{
	String key = "sgte";
	try {
		int aux = 0;
		String aux2 = "";
		while (key.equalsIgnoreCase("sgte")) {
			aux2 = (String) (getListOrden().get(new Integer(aux)));
			//Si se trata de la ultima
			if (aux2.equalsIgnoreCase(lastkey))				
				//Asigno la llave al siguiente servicio del ultimo ejecutado
				key = (String) (getListOrden().get(new Integer(aux + 1)));
			aux++;
		}
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Clase: ProgRequest ERROR: Problema al recorrer la lista de llaves de los servicios, la llave era " + key);
	}
	return key;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:43:05 AM)
 * @return java.lang.String
 */
public java.lang.String getNombre() {
	return nombre;
}
/**
 * Insert the method's description here.
 * Creation date: (02-10-2000 12:32:15 PM)
 * @param newInitService java.lang.String
 */
public void setInitService(java.lang.String newInitService) {
	initService = newInitService;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:44:48 AM)
 * @param newJspTocall java.lang.String
 */
public void setJspTocall(java.lang.String newJspTocall) {
	jspTocall = newJspTocall;
}
/**
 * Insert the method's description here.
 * Creation date: (28-05-2001 04:36:31 PM)
 * @param newListOrden java.util.Properties
 */
public void setListOrden(java.util.Properties newListOrden) {
	listOrden = newListOrden;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:45:32 AM)
 * @param newListServices java.util.Properties
 */
public void setListServices(java.util.Properties newListServices) {
	listServices = newListServices;
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 11:43:05 AM)
 * @param newNombre java.lang.String
 */
public void setNombre(java.lang.String newNombre) {
	nombre = newNombre;
}
/**
 * Insert the method's description here.
 * Creation date: (05-01-2001 05:00:43 PM)
 * @param valorPag boolean
 */
public void setPagina(java.lang.Boolean valorPag) {
	this.pagina = valorPag.booleanValue();	
}
	/**
	 * @return
	 */
	public String getObjetoControl() {
		return objetoControl;
	}

	/**
	 * @param string
	 */
	public void setObjetoControl(String string) {
		objetoControl = string;
	}

	/**
	 * @return
	 */
	public String getObjetoSeguridad() {
		return objetoSeguridad;
	}

	/**
	 * @param string
	 */
	public void setObjetoSeguridad(String string) {
		objetoSeguridad = string;
	}
	public void setInvalidaSesion(boolean invalidaSesion) {
		this.invalidaSesion = invalidaSesion;
	}
	public boolean invalidaSesion(){
		return invalidaSesion;
	}
	/**
	 * @return Returns the hibernate.
	 */
	public boolean isHibernate() {
		return hibernate;
	}
	/**
	 * @param hibernate The hibernate to set.
	 */
	public void setHibernate(boolean hibernate) {
		this.hibernate = hibernate;
	}
}
