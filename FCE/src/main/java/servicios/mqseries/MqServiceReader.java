package servicios.mqseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MqServiceReader extends servicios.generales.XmlBean{
	Document doc = null;
	protected servicios.mqseries.CommParms transParams;
/**
 * MgrCons constructor comment.
 */
public MqServiceReader() {
	super();
	transParams = new servicios.mqseries.CommParms();
}
/**
 * Insert the method's description here.
 * Creation date: (24-10-2000 11:38:27 AM)
 * @return org.w3c.dom.Node
 * @param msgid java.lang.String
 */
private Node findSrvMsgId(String srvmsgid) throws servicios.generales.WSException{
	return find(srvmsgid);
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 04:32:49 PM)
 * @return servicios.mensajeria.MsgService
 * @param srvmsgId java.lang.String
 */
public synchronized CommParms getMqParms(String srvmsgId) throws servicios.generales.WSException{
	recorreArbol(findSrvMsgId(srvmsgId));
	return transParams;
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
		MqServiceReader yomismo = new MqServiceReader();
		yomismo.init(argv[0]);
		servicios.mqseries.CommParms parametros = yomismo.getMqParms("msgMail");
		System.out.println("Cola  = " + parametros.getCola());
		System.out.println("Modo = " + parametros.getMode());
		System.out.println("Bean = " + parametros.getCommBean());
		System.out.println("Nombre = " + parametros.getConnQMgrName());
	}
		catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (13-09-2000 10:37:42 AM)
 * @param param org.w3c.dom.Element
 * Autor: Leonardo Pino
 * 25-05-2001:	Agregar la lectura del nombre de la conexion al queue Manager que corresponda
 */
@Override
protected void recorreArbol(Node root) throws servicios.generales.WSException {
	try {
		int type = root.getNodeType();
		switch (type) {
			case Node.DOCUMENT_NODE :
				{
					//					System.out.println("Tipo: Nodo Document");
					break;
				}
			case Node.ELEMENT_NODE :
				{
					if (root.getNodeName().equalsIgnoreCase("comm")) {
						Element rama = (Element) root;
						//						System.out.println("Tipo del Comm = " + rama.getAttribute("type"));
						//						System.out.println("Modo del Comm = " + rama.getAttribute("mode"));
						transParams.setMode(rama.getAttribute("mode"));
						NodeList nList = root.getChildNodes();
						int numHijos = nList.getLength();
						for (int i = 0; i < numHijos; i++) {
							if (nList.item(i).getNodeType() != 3) {
								if (nList.item(i).getNodeName().equalsIgnoreCase("queue")) {
									transParams.setCola(nList.item(i).getFirstChild().getNodeValue());
									//									System.out.println("Valor de la cola = " + nList.item(i).getFirstChild().getNodeValue());
								}
								if (nList.item(i).getNodeName().equalsIgnoreCase("option")) {
									Element ramita = (Element) nList.item(i);
									//Si es la primera vez que se encuentra este comando se agrega al properties
									if (!transParams.getCmdOptions().containsKey(ramita.getAttribute("type"))) {
										java.util.Vector optionList = new java.util.Vector();
										optionList.addElement(nList.item(i).getFirstChild().getNodeValue());
										transParams.getCmdOptions().put(ramita.getAttribute("type"), optionList);
									}
									//Si el comando ya estaba en la lista, solo se agrega la nueva opcion
									else {
										java.util.Properties Paux = transParams.getCmdOptions();
										java.util.Vector Vaux = (java.util.Vector) Paux.get(ramita.getAttribute("type"));
										Vaux.addElement(nList.item(i).getFirstChild().getNodeValue());
									}
									//									System.out.println("Atributo del option = " + ramita.getAttribute("type"));
									//									System.out.println("Valor del campo option = " + nList.item(i).getFirstChild().getNodeValue());
								}
								if (nList.item(i).getNodeName().equalsIgnoreCase("attrib")) {
									Element ramita = (Element) nList.item(i);
									//Si es la primera vez que se encuentra este atributo se agrega al properties
									if (!transParams.getAttribs().containsKey(ramita.getAttribute("mode"))) {
										java.util.Vector atribList = new java.util.Vector();
										atribList.addElement(nList.item(i).getFirstChild().getNodeValue());
										transParams.getAttribs().put(ramita.getAttribute("mode"), atribList);
									}
									//Si el atributo ya estaba en la lista, solo se agrega la nueva opcion
									else {
										java.util.Properties Paux2 = transParams.getAttribs();
										java.util.Vector Vaux2 = (java.util.Vector) Paux2.get(ramita.getAttribute("mode"));
										Vaux2.addElement(nList.item(i).getFirstChild().getNodeValue());
									}
									//									System.out.println("Atributo del attrib = " + ramita.getAttribute("mode"));
									//									System.out.println("Valor del campo attrib = " + nList.item(i).getFirstChild().getNodeValue());
								}
							}
						}
					}
					else
						if (root.getNodeName().equalsIgnoreCase("bean")) {
							this.transParams.setCommBean(root.getFirstChild().getNodeValue());
						}
					else
						if (root.getNodeName().equalsIgnoreCase("nombreConn")) {
							this.transParams.setConnQMgrName(root.getFirstChild().getNodeValue());
						}
					break;
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
}
