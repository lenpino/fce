package servicios.generales;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import servicios.generales.ldap.LdapAttrib;
import servicios.generales.ldap.LdapConfig;

public class EtcReader extends XmlBean{
	protected LdapConfig ldapData;
/**
 * MgrCons constructor comment.
 */
public EtcReader() {
	super();
}
	public LdapConfig getLdapData(String idLdap) throws servicios.generales.WSException {
		//Cada vez que invocamos este metodo se crea un nuevo set de datos
		try{
			ldapData = new LdapConfig();
			recorreArbol(findLdap(idLdap));
			return ldapData;
		}
		catch(Exception e){
			throw (servicios.generales.WSException)e;
		}
	}

	private Node findLdap(String idLdap) throws servicios.generales.WSException {
		return find(idLdap);
	}

/**
 * Insert the method's description here.
 * Creation date: (26-03-2001 10:30:25 PM)
 * @return java.lang.Object
 * @param dato java.lang.String
 */
public Object getObject(String dato) throws servicios.generales.WSException {
	try {
		return null;
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Clase EtcReader - Error al retornar el tipo de datos del XML - Mensaje " + e.getMessage());
	}
}

public Object getObject(String tipoParser, String consulta) throws servicios.generales.WSException {
	//Consulta representa para el caso de LDAP el nombre de la configuración especifica que se requiere p.e. TOL
	try {
		if (tipoParser.equalsIgnoreCase("Ldap")) {
			return getLdapData(consulta);
		}
		else
			return null;
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Clase EtcReader - Error al retornar el tipo de datos del XML - Mensaje " + e.getMessage());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (15-09-2000 09:41:45 AM)
 */
public void init(String archivo) throws servicios.generales.WSException{
	load(archivo);
}
static public void main(String[] argv) {
	try {
		if (argv.length != 1) {
			System.err.println("Uso: java servicios.control.ProgramReader filename");
			System.exit(1);
		}
		EtcReader yomismo = new EtcReader();
		yomismo.init(argv[0]);
		LdapConfig configuracion = yomismo.getLdapData("tol");
		System.out.println("direccion = " + configuracion.getDireccion());
	}
		catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 10:37:42 AM)
 * @param param org.w3c.dom.Element
 */
@Override
protected void recorreArbol(Node root) throws servicios.generales.WSException {
	try {
		int type = root.getNodeType();
		switch (type) {
			case Node.DOCUMENT_NODE :
				{
					break;
				}
			case Node.ELEMENT_NODE :
				{
						if (root.getNodeName().equalsIgnoreCase("LDAP")) {
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 0; i < numHijos; i++) {
								if (nList.item(i).getNodeType() != 3) {
									if (nList.item(i).getNodeName().equalsIgnoreCase("direccion"))
										ldapData.setDireccion(nList.item(i).getFirstChild().getNodeValue());
									else
										if (nList.item(i).getNodeName().equalsIgnoreCase("claseImplementa"))
											ldapData.setClaseLdap(nList.item(i).getFirstChild().getNodeValue());
										else
											if (nList.item(i).getNodeName().equalsIgnoreCase("nombreNodoBase"))
												ldapData.setBaseNode(nList.item(i).getFirstChild().getNodeValue());
											else
												if (nList.item(i).getNodeName().equalsIgnoreCase("adminUser"))
													ldapData.setLoginAdmin(nList.item(i).getFirstChild().getNodeValue());
												else
													if (nList.item(i).getNodeName().equalsIgnoreCase("adminPwd"))
														ldapData.setClaveAdmin(nList.item(i).getFirstChild().getNodeValue());
													else
														if (nList.item(i).getNodeName().equalsIgnoreCase("clase"))
															ldapData.setClassObject(nList.item(i).getFirstChild().getNodeValue());
														else
															if (nList.item(i).getNodeName().equalsIgnoreCase("mainKey"))
																ldapData.setLlave(nList.item(i).getFirstChild().getNodeValue());
								}
							}
						}
						else
							if (root.getNodeName().equalsIgnoreCase("atributos")) {
								NodeList nList = root.getChildNodes();
								int numHijos = nList.getLength();
								for (int i = 0; i < numHijos; i++) {
									LdapAttrib atributo = new LdapAttrib();
									if (nList.item(i).getNodeType() != 3)
										if (nList.item(i).getNodeName().equalsIgnoreCase("atributo")) {
											NodeList subNlist = nList.item(i).getChildNodes();
											int subNumHijos = subNlist.getLength();
											for (int j = 0; j < subNumHijos; j++)
												if (subNlist.item(j).getNodeType() != 3)
													if (subNlist.item(j).getNodeName().equalsIgnoreCase("nombre"))
														atributo.setAttribName(subNlist.item(j).getFirstChild().getNodeValue());
													else
														if (subNlist.item(j).getNodeName().equalsIgnoreCase("valor"))
															atributo.setAttribValue(subNlist.item(j).getFirstChild().getNodeValue());
											ldapData.getAttribs().addElement(atributo);
										}
								}
							}
			}
			case Node.ENTITY_REFERENCE_NODE :
				{
					//					System.out.println("Tipo: Nodo Entity Reference");
					break;
				}
			case Node.CDATA_SECTION_NODE :
				{
					//					System.out.println("Tipo: Nodo Seccion");
					break;
				}
		}
		if (root.hasChildNodes()) {
			NodeList nList = root.getChildNodes();
			int childrenCount = nList.getLength();
			for (int i = 0; i < childrenCount; i++)
				recorreArbol(nList.item(i));
		}
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al recorrer el arbol " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (19-03-2001 05:02:01 PM)
 * @param newLdapData servicios.generales.LdapConfig
 */
public void setLdapData(LdapConfig newLdapData) {
	ldapData = newLdapData;
}
}
