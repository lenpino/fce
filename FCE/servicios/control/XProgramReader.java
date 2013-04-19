package servicios.control;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import servicios.generales.WSException;

/**
 * @author lpino
 *
 */
public class XProgramReader extends ProgramReader {	
	private HashMap todosServicios = new HashMap();
	private HashMap todosObjetos = new HashMap();
	@Override
	public void init(String archivo) throws servicios.generales.WSException {
		load(archivo);
		//Todos los xml files asociados a la aplicacion
		xmlFiles = new java.util.Properties();
		//Todos los errores de la aplicacion
		todosErrores = new java.util.Properties();
		Element root = getDoc().getDocumentElement();
		//Inicio la estructura con todos los servicios
		setAllServicios(root);
		//Inicio la estructura con todos los objetos
		setAllObjects(root);
		//Inicio la estructura con todos los programas
		setAllPrograms(root);
	}
	static public void main(String[] argv) {
		try {
			if (argv.length != 1) {
				System.err.println("Uso: java servicios.control.ProgramReader filename");
				System.exit(1);
			}
			XProgramReader yomismo = new XProgramReader();
			yomismo.init(argv[0]);
/*			ProgRequest program = yomismo.getPrograma("prglsttrabajadores");
			System.out.println(program.getNombre());
*/		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void setAllServicios(Node rootPrg) {
		NodeList nListServ = ((Element) rootPrg).getElementsByTagName("servicio");
		//Obtengo la lista de servicios asociados a este programa
		Node aux = nListServ.item(0);
		while(aux != null){
			servicios.control.XService servicio = new servicios.control.XService();
			NodeList nList = aux.getChildNodes();
			int numHijos = nList.getLength();
			String llave = "";
			for (int i = 0; i < numHijos; i++) {
				if (nList.item(i).getNodeType() != 3) {
					if (nList.item(i).getNodeName().equalsIgnoreCase("idservicio")) {
						Element rama = (Element) nList.item(i);
						llave = rama.getAttributes().item(0).getNodeValue();
						servicio.setServicio(rama.getAttributes().item(1).getNodeValue());
					}
					if (nList.item(i).getNodeName().equalsIgnoreCase("session"))
						servicio.setSession(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("modo"))
						servicio.setModoOpera(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("beanSalida"))
						servicio.setBeanSalida(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("timeout"))
						servicio.setTimeOut(Integer.parseInt(nList.item(i).getFirstChild().getNodeValue()));
					if (nList.item(i).getNodeName().equalsIgnoreCase("consname"))
						servicio.setNombreConsul(nList.item(i).getFirstChild().getNodeValue());
					if (nList.item(i).getNodeName().equalsIgnoreCase("tipoparser"))
						servicio.setTipoParser(nList.item(i).getFirstChild().getNodeValue());
				}
			}
			todosServicios.put(llave, servicio);
			aux = (aux.getNextSibling()).getNextSibling();
		}
	}
	
	private void setAllObjects(Node root){
		NodeList nListServ = ((Element) root).getElementsByTagName("objeto");
		//Obtengo la lista de objetos
		Node aux = nListServ.item(0);
		while(aux != null){
			todosObjetos.put(aux.getAttributes().item(0).getNodeValue(), aux.getFirstChild().getNodeValue());
			aux = (aux.getNextSibling()).getNextSibling();
		}		
	}
	
	@Override
	protected void setServiciosPrg(Node rootPrg) throws WSException{
		NodeList nListServ = ((Element) rootPrg).getElementsByTagName("servicioPrg");
		Node aux = nListServ.item(0);
		String xua = null;
		while(aux != null){
			XService servicioPrg = new XService();
			XService servicio = (XService)todosServicios.get(aux.getAttributes().item(0).getNodeValue());
			if(servicio==null)
				throw new WSException("Clase: XProgramReader - Error: No existe la definicion, en el XML, para el servicio " + aux.getAttributes().item(0).getNodeValue());
			//Copiando los valores
			servicioPrg.setBeanSalida(servicio.getBeanSalida());
			servicioPrg.setSession(servicio.getSession());
			servicioPrg.setModoOpera(servicio.getModoOpera());
			servicioPrg.setNombreConsul(servicio.getNombreConsul());
			servicioPrg.setTipoParser(servicio.getTipoParser());
			servicioPrg.setServicioCtrl(servicio.getServicio());
			
			if(aux.getAttributes().item(1) == null)
				xua = null;
			else
				xua = aux.getAttributes().item(1).getNodeValue();
			Node sgteNodo = (aux.getNextSibling()).getNextSibling();
			//Si no tiene atributo de sgteSrv y es el ultimo servicio entonces se coloca SBO
			if(xua == null && sgteNodo == null)
				servicioPrg.setClaseNegocios("sbo");
			//Si no tiene atributo de sgteSrv y no es el ultimo entonces el siguiente servicio es el siguiente en la lista
			else if(xua == null)
				servicioPrg.setClaseNegocios(sgteNodo.getAttributes().item(0).getNodeValue());
			//Si tiene sgteSrv y esta en la lista de objetos de negocios se coloca el nombre de la clase
			else if(todosObjetos.get(xua) != null)
				servicioPrg.setClaseNegocios((String)todosObjetos.get(xua));
			else
			//Si tiene sgteSrv y no es un objeto entonces se asigna el valor que indica el atributo
				servicioPrg.setClaseNegocios(xua);
			//Se agrega el servicio al programa
			programa.addService(aux.getAttributes().item(0).getNodeValue(), servicioPrg);
			aux = sgteNodo;
		}
		//Se copia el programa a la lista de programas
		todosProgramas.put(programa.getNombre(), programa);
	}

	@Override
	protected void setAtributosPrg(Node rootPrg) {
		NodeList nListAtribs = ((Element) rootPrg).getElementsByTagName("atributos");
		Node aux = nListAtribs.item(0);
		NodeList nList = aux.getChildNodes();
		int numHijos = nList.getLength();
		for (int i = 0; i < numHijos; i++) {
			if (nList.item(i).getNodeType() != 3) {
				if (nList.item(i).getNodeName().equalsIgnoreCase("servicioIni"))
					programa.setInitService(nList.item(i).getFirstChild().getNodeValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("AliasJspSalida"))
					programa.setJspTocall(nList.item(i).getFirstChild().getNodeValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("pagina"))
					programa.setPagina(Boolean.valueOf(nList.item(i).getFirstChild().getNodeValue()));
				else if (nList.item(i).getNodeName().equalsIgnoreCase("hibernate"))
					programa.setHibernate((Boolean.valueOf(nList.item(i).getFirstChild().getNodeValue())).booleanValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("objCtrlExcepciones")){
					//Extrae de la lista de objetos el encargado del manejo de errores para este programa
					programa.setObjetoControl((String)todosObjetos.get(nList.item(i).getFirstChild().getNodeValue()));
				}
				else if (nList.item(i).getNodeName().equalsIgnoreCase("invalidaSesion"))
					programa.setInvalidaSesion((Boolean.valueOf(nList.item(i).getFirstChild().getNodeValue())).booleanValue());
				else if (nList.item(i).getNodeName().equalsIgnoreCase("paginacionDta")) {
					NodeList nListPag = nList.item(i).getChildNodes();
					int numHijosPag = nListPag.getLength();
					for (int j = 0; j < numHijosPag; j++) {
						if (nListPag.item(j).getNodeType() != 3) {
							if (nListPag.item(j).getNodeName().equalsIgnoreCase("beanPaginado"))
								programa.datosPaginacion.setBeanPaginado(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("numLineas"))
								programa.datosPaginacion.setNumLineas(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("numCol"))
								programa.datosPaginacion.setNumColumna(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("aliasServlet"))
								programa.datosPaginacion.setAkaServlet(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("program"))
								programa.datosPaginacion.setPrograma(nListPag.item(j).getFirstChild().getNodeValue());
							else if (nListPag.item(j).getNodeName().equalsIgnoreCase("imagenes")) {
								NodeList nListImg = nListPag.item(j).getChildNodes();
								int numHijosImg = nList.getLength();
								for (int k = 0; k < numHijosImg; k++) {
									if (nListImg.item(k).getNodeType() != 3) {
										if (nListImg.item(k).getNodeName().equalsIgnoreCase("pathURL"))
											programa.datosPaginacion.imagenes[0] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("prevAct"))
											programa.datosPaginacion.imagenes[1] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("prevInact"))
											programa.datosPaginacion.imagenes[2] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("sgteAct"))
											programa.datosPaginacion.imagenes[3] = nListImg.item(k).getFirstChild().getNodeValue();
										else if (nListImg.item(k).getNodeName().equalsIgnoreCase("sgteInact"))
											programa.datosPaginacion.imagenes[4] = nListImg.item(k).getFirstChild().getNodeValue();
									}
								}
							}

						}
					}
				}
			}
		}
	}

}
