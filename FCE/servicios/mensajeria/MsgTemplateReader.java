package servicios.mensajeria;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MsgTemplateReader {
	Document doc = null;
	protected MsgService servicioMensajes;
	protected TemplateMessage patronMensaje;
/**
 * MgrCons constructor comment.
 */
public MsgTemplateReader() {
	super();
	servicioMensajes = new servicios.mensajeria.MsgService();
	patronMensaje = new servicios.mensajeria.TemplateMessage();
}
/**
 * Insert the method's description here.
 * Creation date: (24-10-2000 11:38:27 AM)
 * @return org.w3c.dom.Node
 * @param msgid java.lang.String
 */
private Node findMsgId(String msgid) throws servicios.generales.WSException{
	Element root = getDoc().getDocumentElement();
	if (root.hasChildNodes()) {
		NodeList nList = root.getChildNodes();
		int numHijos = nList.getLength();
		for (int i = 0; i < numHijos; i++) {
			if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("message")) {
				Element rama = (Element) nList.item(i);
				if (rama.getAttribute("id").equalsIgnoreCase(msgid)){
					return rama;
				}
			}
		}
	}
	else {
		throw new servicios.generales.WSException("Error: El arbol de programas esta vacio");
	}
	return root;

}
/**
 * Insert the method's description here.
 * Creation date: (24-10-2000 11:38:27 AM)
 * @return org.w3c.dom.Node
 * @param msgid java.lang.String
 */
private Node findSrvMsgId(String srvmsgid) throws servicios.generales.WSException{
	Element root = getDoc().getDocumentElement();
	if (root.hasChildNodes()) {
		NodeList nList = root.getChildNodes();
		int numHijos = nList.getLength();
		for (int i = 0; i < numHijos; i++) {
			if (nList.item(i).getNodeType() != 3 && nList.item(i).getNodeName().equalsIgnoreCase("srvmsg")) {
				Element rama = (Element) nList.item(i);
				if (rama.getAttribute("id").equalsIgnoreCase(srvmsgid)){
					return rama;
				}
			}
		}
	}
	else {
		throw new servicios.generales.WSException("Error: El arbol de programas esta vacio");
	}
	return root;

}
public Document getDoc() {
	return this.doc;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 04:32:49 PM)
 * @return servicios.mensajeria.MsgService
 * @param srvmsgId java.lang.String
 */
public MsgService getMsgService(String srvmsgId) throws servicios.generales.WSException{
	recorreArbol(findSrvMsgId(srvmsgId));
	this.servicioMensajes.setPlantillaMsg(getTemplateMsg(this.servicioMensajes.getTemplateId()));
	return servicioMensajes;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 04:38:40 PM)
 * @return servicios.mensajeria.TemplateMessage
 * @param messageId java.lang.String
 */
public TemplateMessage getTemplateMsg(String messageId) throws servicios.generales.WSException{
	recorreArbol(findMsgId(messageId));
	return patronMensaje;
}
/**
 * Insert the method's description here.
 * Creation date: (15-09-2000 09:41:45 AM)
 */
public void init(String archivo) throws servicios.generales.WSException{
	load(archivo);
}
public void load(String ArchivoXml) throws servicios.generales.WSException{
	try {
		DOMParser parser = new DOMParser();
		parser.parse(ArchivoXml);
		Document doc1 = parser.getDocument();
		setDoc(doc1);
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al parsear archivo XML, msg: " + e.getMessage());
	} 
}
static public void main(String[] argv) throws Exception {
	try {
		if (argv.length != 1) {
			System.err.println("Uso: java servicios.control.ProgramReader filename");
			System.exit(1);
		}
		MsgTemplateReader yomismo = new MsgTemplateReader();
		yomismo.init(argv[0]);
		yomismo.getTemplateMsg("msgMail");
/*		System.out.println("*************************************************");
		yomismo.recorreArbol(yomismo.findSrvMsgId("EnvioMail")); */
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
 */
private void recorreArbol(Node root) throws servicios.generales.WSException {
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
					if (root.getNodeName().equalsIgnoreCase("field")) {
						Element rama = (Element) root;
						servicios.mensajeria.TemplateField tf = new servicios.mensajeria.TemplateField(patronMensaje);
						tf.setName(rama.getAttribute("id"));
						System.out.println("Atributo del field = " + rama.getAttribute("id"));
						NodeList nList = root.getChildNodes();
						int numHijos = nList.getLength();
						for (int i = 0; i < numHijos; i++) {
							if (nList.item(i).getNodeType() != 3) {
								if (nList.item(i).getNodeName().equalsIgnoreCase("len")) {
									Element ramita = (Element) nList.item(i);
									if (ramita.getAttribute("type").equalsIgnoreCase("fld"))
										tf.setSize(nList.item(i).getFirstChild().getNodeValue());
									else
										tf.setSize(Integer.parseInt(nList.item(i).getFirstChild().getNodeValue()));
									System.out.println("Atributo del len = " + ramita.getAttribute("type"));
									System.out.println("Valor del campo len = " + nList.item(i).getFirstChild().getNodeValue());
								}
							}
						}
						patronMensaje.addField(tf);
					}
					else
						if (root.getNodeName().equalsIgnoreCase("group")) {
							servicios.mensajeria.TemplateGroupField tgf;
							System.out.println("**********************************************");
							Element rama = (Element) root;
							try {
								tgf = (TemplateGroupField) patronMensaje.getField(rama.getAttribute("id"));
							}
							catch (NotFoundTemplateFieldException e) {
								tgf = new servicios.mensajeria.TemplateGroupField(patronMensaje);
								patronMensaje.addField(tgf);
							}
							tgf.setName(rama.getAttribute("id"));
							patronMensaje.addField(tgf);
							System.out.println("Atributo del group = " + rama.getAttribute("id"));
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 0; i < numHijos; i++) {
								if (nList.item(i).getNodeType() != 3) {
									if (nList.item(i).getNodeName().equalsIgnoreCase("repite")) {
										Element ramita = (Element) nList.item(i);
										System.out.println("Atributo del repite = " + ramita.getAttribute("type"));
										if (ramita.getAttribute("type").equalsIgnoreCase("fld"))
											tgf.setRepeat(nList.item(i).getFirstChild().getNodeValue());
										else
											tgf.setRepeat(Integer.parseInt(nList.item(i).getFirstChild().getNodeValue()));
										System.out.println("Valor del campo repite = " + nList.item(i).getFirstChild().getNodeValue());
									}
									if (nList.item(i).getNodeName().equalsIgnoreCase("field")) {
										System.out.println("Indice = " + i);
										Element ramasub = (Element) nList.item(i);
										servicios.mensajeria.TemplateField tf = new servicios.mensajeria.TemplateField(patronMensaje);
										tf.setName(ramasub.getAttribute("id"));
										System.out.println("Atributo del field = " + ramasub.getAttribute("id"));
										NodeList nListSub = nList.item(i).getChildNodes();
										int sons = nListSub.getLength();
										for (int j = 0; j < sons; j++) {
											if (nListSub.item(j).getNodeType() != 3) {
												if (nListSub.item(j).getNodeName().equalsIgnoreCase("len")) {
													Element ramita = (Element) nListSub.item(j);
													if (ramita.getAttribute("type").equalsIgnoreCase("fld"))
														tf.setSize(nListSub.item(j).getFirstChild().getNodeValue());
													else
														tf.setSize(Integer.parseInt(nListSub.item(j).getFirstChild().getNodeValue()));
													System.out.println("Atributo del len = " + ramita.getAttribute("type"));
													System.out.println("Valor del campo len = " + nListSub.item(j).getFirstChild().getNodeValue());
												}
											}
										}
										tgf.addField(tf);
										if (i == (numHijos - 2)) {
											root = root.getNextSibling();
										}
									}
									if (nList.item(i).getNodeName().equalsIgnoreCase("group")) {
										Node papa = root;
										servicios.mensajeria.TemplateGroupField tgfh = new servicios.mensajeria.TemplateGroupField(patronMensaje);
										Element ramon = (Element) nList.item(i);
										tgfh.setName(ramon.getAttribute("id"));
										patronMensaje.addFieldToGroup(tgf.getName(), tgfh);
										recorreArbol(nList.item(i));
										if (i == (numHijos - 2)) {
											root = papa.getNextSibling();
										}
									}
								}
							}
						}
						else
							if (root.getNodeName().equalsIgnoreCase("msgid")) {
								this.servicioMensajes.setTemplateId(root.getFirstChild().getNodeValue());
								System.out.println("Valor del campo len = " + root.getFirstChild().getNodeValue());
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
public void setDoc(Document doc2) {
	this.doc = doc2;
}
}
