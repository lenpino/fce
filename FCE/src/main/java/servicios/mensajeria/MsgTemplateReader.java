package servicios.mensajeria;

//(sin package declarado porque el original no lo incluía)

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MsgTemplateReader {

 private Document doc = null;
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
 private Node findMsgId(String msgid) throws servicios.generales.WSException {
     Element root = getDoc().getDocumentElement();
     if (root.hasChildNodes()) {
         NodeList nList = root.getChildNodes();
         int numHijos = nList.getLength();
         for (int i = 0; i < numHijos; i++) {
             Node child = nList.item(i);
             if (child.getNodeType() == Node.ELEMENT_NODE
                     && "message".equalsIgnoreCase(child.getNodeName())) {
                 Element rama = (Element) child;
                 if (msgid.equalsIgnoreCase(rama.getAttribute("id"))) {
                     return rama;
                 }
             }
         }
     } else {
         throw new servicios.generales.WSException("Error: El arbol de programas esta vacio");
     }
     return root;
 }

 /**
  * Insert the method's description here.
  * Creation date: (24-10-2000 11:38:27 AM)
  * @return org.w3c.dom.Node
  * @param srvmsgid java.lang.String
  */
 private Node findSrvMsgId(String srvmsgid) throws servicios.generales.WSException {
     Element root = getDoc().getDocumentElement();
     if (root.hasChildNodes()) {
         NodeList nList = root.getChildNodes();
         int numHijos = nList.getLength();
         for (int i = 0; i < numHijos; i++) {
             Node child = nList.item(i);
             if (child.getNodeType() == Node.ELEMENT_NODE
                     && "srvmsg".equalsIgnoreCase(child.getNodeName())) {
                 Element rama = (Element) child;
                 if (srvmsgid.equalsIgnoreCase(rama.getAttribute("id"))) {
                     return rama;
                 }
             }
         }
     } else {
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
 public MsgService getMsgService(String srvmsgId) throws servicios.generales.WSException {
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
 public TemplateMessage getTemplateMsg(String messageId) throws servicios.generales.WSException {
     recorreArbol(findMsgId(messageId));
     return patronMensaje;
 }

 /**
  * Insert the method's description here.
  * Creation date: (15-09-2000 09:41:45 AM)
  */
 public void init(String archivo) throws servicios.generales.WSException {
     load(archivo);
 }

 public void load(String archivoXml) throws servicios.generales.WSException {
     try (InputStream in = Files.newInputStream(Path.of(archivoXml))) {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         try {
             dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
         } catch (Exception ignored) {
             // algunos proveedores pueden no soportarlo
         }
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc1 = db.parse(in);
         setDoc(doc1);
     } catch (Exception e) {
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
     } catch (Exception e) {
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
             case Node.DOCUMENT_NODE: {
                 //					System.out.println("Tipo: Nodo Document");
                 break;
             }
             case Node.ELEMENT_NODE: {
                 if ("field".equalsIgnoreCase(root.getNodeName())) {
                     Element rama = (Element) root;
                     servicios.mensajeria.TemplateField tf = new servicios.mensajeria.TemplateField(patronMensaje);
                     tf.setName(rama.getAttribute("id"));
                     System.out.println("Atributo del field = " + rama.getAttribute("id"));
                     NodeList nList = root.getChildNodes();
                     int numHijos = nList.getLength();
                     for (int i = 0; i < numHijos; i++) {
                         Node n = nList.item(i);
                         if (n.getNodeType() == Node.ELEMENT_NODE && "len".equalsIgnoreCase(n.getNodeName())) {
                             Element ramita = (Element) n;
                             String val = safeText(n);
                             if ("fld".equalsIgnoreCase(ramita.getAttribute("type"))) {
                                 tf.setSize(val);
                             } else {
                                 tf.setSize(Integer.parseInt(val));
                             }
                             System.out.println("Atributo del len = " + ramita.getAttribute("type"));
                             System.out.println("Valor del campo len = " + val);
                         }
                     }
                     patronMensaje.addField(tf);
                 } else if ("group".equalsIgnoreCase(root.getNodeName())) {
                     servicios.mensajeria.TemplateGroupField tgf;
                     System.out.println("**********************************************");
                     Element rama = (Element) root;
                     try {
                         tgf = (TemplateGroupField) patronMensaje.getField(rama.getAttribute("id"));
                     } catch (NotFoundTemplateFieldException e) {
                         tgf = new servicios.mensajeria.TemplateGroupField(patronMensaje);
                         patronMensaje.addField(tgf);
                     }
                     tgf.setName(rama.getAttribute("id"));
                     patronMensaje.addField(tgf);
                     System.out.println("Atributo del group = " + rama.getAttribute("id"));

                     NodeList nList = root.getChildNodes();
                     int numHijos = nList.getLength();
                     for (int i = 0; i < numHijos; i++) {
                         Node n = nList.item(i);
                         if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                         if ("repite".equalsIgnoreCase(n.getNodeName())) {
                             Element ramita = (Element) n;
                             String val = safeText(n);
                             System.out.println("Atributo del repite = " + ramita.getAttribute("type"));
                             if ("fld".equalsIgnoreCase(ramita.getAttribute("type"))) {
                                 tgf.setRepeat(val);
                             } else {
                                 tgf.setRepeat(Integer.parseInt(val));
                             }
                             System.out.println("Valor del campo repite = " + val);
                         }
                         if ("field".equalsIgnoreCase(n.getNodeName())) {
                             System.out.println("Indice = " + i);
                             Element ramasub = (Element) n;
                             servicios.mensajeria.TemplateField tf = new servicios.mensajeria.TemplateField(patronMensaje);
                             tf.setName(ramasub.getAttribute("id"));
                             System.out.println("Atributo del field = " + ramasub.getAttribute("id"));

                             NodeList nListSub = n.getChildNodes();
                             int sons = nListSub.getLength();
                             for (int j = 0; j < sons; j++) {
                                 Node ns = nListSub.item(j);
                                 if (ns.getNodeType() == Node.ELEMENT_NODE && "len".equalsIgnoreCase(ns.getNodeName())) {
                                     Element ramita = (Element) ns;
                                     String val = safeText(ns);
                                     if ("fld".equalsIgnoreCase(ramita.getAttribute("type"))) {
                                         tf.setSize(val);
                                     } else {
                                         tf.setSize(Integer.parseInt(val));
                                     }
                                     System.out.println("Atributo del len = " + ramita.getAttribute("type"));
                                     System.out.println("Valor del campo len = " + val);
                                 }
                             }
                             tgf.addField(tf);
                             if (i == (numHijos - 2)) {
                                 root = root.getNextSibling();
                             }
                         }
                         if ("group".equalsIgnoreCase(n.getNodeName())) {
                             Node papa = root;
                             servicios.mensajeria.TemplateGroupField tgfh = new servicios.mensajeria.TemplateGroupField(patronMensaje);
                             Element ramon = (Element) n;
                             tgfh.setName(ramon.getAttribute("id"));
                             patronMensaje.addFieldToGroup(tgf.getName(), tgfh);
                             recorreArbol(n);
                             if (i == (numHijos - 2)) {
                                 root = papa.getNextSibling();
                             }
                         }
                     }
                 } else if ("msgid".equalsIgnoreCase(root.getNodeName())) {
                     this.servicioMensajes.setTemplateId(safeText(root));
                     System.out.println("Valor del campo len = " + safeText(root));
                 }
                 break;
             }
             case Node.ENTITY_REFERENCE_NODE: {
                 //					System.out.println("Tipo: Nodo Entity Reference");
                 break;
             }
             case Node.CDATA_SECTION_NODE: {
                 //					System.out.println("Tipo: Nodo Seccion");
                 break;
             }
             default:
                 break;
         }

         if (root.hasChildNodes()) {
             NodeList nList = root.getChildNodes();
             int childrenCount = nList.getLength();
             for (int i = 0; i < childrenCount; i++) {
                 recorreArbol(nList.item(i));
             }
         }
     } catch (Exception e) {
         throw new servicios.generales.WSException("Error al recorrer el arbol " + e.getMessage());
     }
 }

 public void setDoc(Document doc2) {
     this.doc = doc2;
 }

 /* ===================== Helpers ===================== */

 private static String safeText(Node n) {
     // Evita NPE; getTextContent() ya concatena hijos de texto/CDATAs
     String s = n != null ? n.getTextContent() : null;
     return s != null ? s.trim() : "";
 }
}
