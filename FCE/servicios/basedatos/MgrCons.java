package servicios.basedatos;

/**
 * Clase control del servicio hacia la Base de Datos
 * Creation date: (08-09-2000 04:23:27 PM)
 * @author: Leonardo Pino Werlinger
 * 23-04-2002:	Cambio en las clases para usar parsers de Apache en vez de los de IBM
 * 01-08-2003:	Nuevas lecturas en el arbol para el soporte de transacciones 
 */

import java.io.PrintWriter;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import servicios.generales.WSException;

/**
 * 29-01-2003:	Mejora el manejo de excepciones en el metodo load()
 * 24-03-2003:	Arroja error si no encuentra la consulta de SP en el XML
 * 26-03-2003:	No busca mas el mapeo de nombres de parametros para los SPs
 * 07-04-2003:	Se hereda desde la nueva clase XmlBean para centralizar el manejo de los XML
 **/
public class MgrCons extends servicios.generales.XmlBean {
	ConsultaSQL conssql = null;
	ConsultaTX constx = null;
	Properties listaParams = null;
	private Node findConsultaSP(String nconsulta) throws WSException {
		return find(nconsulta);
	}
	/**
	 * MgrCons constructor comment.
	 */
	public MgrCons() {
		super();
	}
	public synchronized ConsultaSP getConsultaSP(String TipoConsulta) throws Exception {
		ConsultaSP conssp = new ConsultaSP();
		//Crea la consulta a partir de los datos que estan en el XML
		setConsultaSP(findConsultaSP(TipoConsulta),conssp);
		//Extrae los nombres de los parametros de entrada y salida desde el XML y los deja en la consulta
		setConsultaSP(findSPParams(conssp.getConsstrproc()),conssp);
		return conssp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27-09-2000 01:26:05 PM)
	 * @return servicios.basedatos.Consulta
	 */
	public synchronized ConsultaSQL getConsultaSQL(String TipoConsulta) throws Exception, IndexOutOfBoundsException, NullPointerException {
		recorreArbol(findConsultaSQL(TipoConsulta));
		createParameters();
		conssql.setParams(listaParams);
		return conssql;
	}
	static public void main(String[] argv) {
			try {
				if (argv.length != 3) {
					System.err.println("Uso: java CtrlPres.MgrCons filename tipoconsulta flag");
					System.exit(1);
				}
				String flag = argv[2];
				MgrCons mgr = null;
				System.out.println("File = " + argv[0] + "; Consulta = " + argv[1]);
				mgr = new MgrCons();
				mgr.load(argv[0]);
				mgr.createParameters();
				if (flag.equalsIgnoreCase("SP")) {
					ConsultaSP cons = mgr.getConsultaSP(argv[1]);
					PrintWriter pwriter = new PrintWriter(System.out);
					pwriter.print("OK \n");
					pwriter.print(cons.getConsname() + "\n");
					pwriter.print(cons.getConsbean() + "\n");
					pwriter.print(cons.getConsstrproc() + "\n");
					pwriter.print("-------------------------------------------------------------------------------\n");
					pwriter.print("Parametros consulta \n\n");
//					Enumeration e = cons.getConsparam().entrada.keys();
//					while(e.hasMoreElements()){
//						pwriter.print("parametro = " + ((ConsParamsX)cons.getConsparam().entrada.get(e.nextElement())).nomparam + "\n");
//					}
					pwriter.print("-------------------------------------------------------------------------------\n");
					pwriter.flush();
					}
				}
			catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
	}
	private Node findConsultaSQL(String nconsulta) throws WSException {
		return find(nconsulta);
	}
	@Override
	protected void recorreArbol(Node root) throws WSException {
		try {
			int type = root.getNodeType();
			switch (type) {
				case Node.DOCUMENT_NODE :
					{
						break;
					}
				case Node.COMMENT_NODE :
					{
						break;
					}
				case Node.ELEMENT_NODE :
					{
						/***********************************************************************/
						if (root.getNodeName().equalsIgnoreCase("conssql")) {
							conssql = new ConsultaSQL();
							Element rama = (Element) root;
							conssql.setConsname(rama.getAttribute("consnamesql"));
							NodeList nList = root.getChildNodes();
							//considpoolsql
							conssql.setIdPool(nList.item(1).getFirstChild().getNodeValue());
							//consbeansql
							conssql.setConsbean(nList.item(3).getFirstChild().getNodeValue());
							//sql
							conssql.setQuery(nList.item(5).getFirstChild().getNodeValue());
							//paramsSQL
							NodeList nListPsql = nList.item(7).getChildNodes();
							int numHijos = nListPsql.getLength();
							String nomparams[] = new String[numHijos];
							for (int i = 1; i < numHijos; i++) {
								if (nListPsql.item(i).getNodeName().equalsIgnoreCase("nomparamSQL"))
									nomparams[i] = nListPsql.item(i).getFirstChild().getNodeValue();
								i++;
							}
							conssql.setParamsNames(nomparams);
							break;
						}
						/************************************************************************/
						else if (root.getNodeName().equalsIgnoreCase("paramsSQL")) {
						}
						/************************************************************************/
						else if (root.getNodeName().equalsIgnoreCase("params")) {
							listaParams = new Properties();
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 1; i < numHijos; i++) {
								//Aqui se crea un objeto ConsParamsX por cada parametro que exista en el XML
								ConsParamsX parametrosi = new ConsParamsX();
								try {
									if (nList.item(i).getNodeName().equalsIgnoreCase("param")) {
										parametrosi.nomparam = ((Element) nList.item(i)).getAttribute("name");
										NodeList nListD = nList.item(i).getChildNodes();
										parametrosi.typeparam = nListD.item(1).getFirstChild().getNodeValue();
										parametrosi.lenparam = Integer.parseInt(nListD.item(3).getFirstChild().getNodeValue());
										listaParams.put(parametrosi.nomparam, parametrosi);
									}
									i++;
								}
								catch (Exception e) {
									System.out.println("Nombre parametro anterior: " + nList.item(i - 2).getNodeName() + "\r Parametro #: " + i + "\r Mensaje: " + e.getMessage());
								}
							}
							break;
						}
						/***********************************************************************/
						else if (root.getNodeName().equalsIgnoreCase("constx")) {
							constx = new ConsultaTX();
							String nombreConsultas[] = null;
							String nombreParams[] = null;
							int k = 0;
							NodeList nList = root.getChildNodes();
							int numHijos = nList.getLength();
							for (int i = 1; i < numHijos; i++) {
								if (nList.item(i).getNodeName().equalsIgnoreCase("considpooltx"))
									constx.setIdPool(nList.item(i).getFirstChild().getNodeValue());
								else if (nList.item(i).getNodeName().equalsIgnoreCase("bean"))
									constx.setConsbean(nList.item(i).getFirstChild().getNodeValue());
								else if (nList.item(i).getNodeName().equalsIgnoreCase("sps")) {
									NodeList nListD = nList.item(i).getChildNodes();
									int numChilds = nListD.getLength();
									nombreConsultas = new String[(numChilds - 1) / 2];
									nombreParams = new String[(numChilds - 1) / 2];
									for (int j = 1; j < numChilds; j++) {
										//										if (nListD.item(j).getNodeName().equalsIgnoreCase("sp")) {
										nombreConsultas[k] = nListD.item(j).getFirstChild().getNodeValue();
										nombreParams[k] = ((Element) nListD.item(j)).getAttribute("sppar");
										k++;
										//										}
										j++;
									}
								}
								i++;
							}
							constx.consultas = nombreConsultas;
							constx.nParamsCons = nombreParams;
						}
						/***********************************************************************/
						else if (root.getNodeName().equalsIgnoreCase("strproc")) {
							String noutparams[] = null;
							String ninparams[] = null;
							NodeList nListInParams = ((Element) root).getElementsByTagName("instrparams");
							NodeList nListOutParams = ((Element) root).getElementsByTagName("outstrparams");
							int numInParams = nListInParams.getLength();
							int numOutParams = nListOutParams.getLength();
							if (numInParams > 0) {
								ninparams = new String[numInParams];
								for (int i = 0; i < numInParams; i++) {
									ninparams[i] = nListInParams.item(i).getFirstChild().getNodeValue();
								}
							}
							if (numOutParams > 0) {
								noutparams = new String[numOutParams];
								for (int i = 0; i < numOutParams; i++) {
									noutparams[i] = nListOutParams.item(i).getFirstChild().getNodeValue();
								}
							}
							//							NodeList nList = root.getChildNodes();
							//							int numHijos = nList.getLength();
							//							int k = 0;
							//							int l = 0;
							//							if (numHijos >= 3) {
							//								NodeList nListD = nList.item(1).getChildNodes();
							//								int numChilds = nListD.getLength();
							//								ninparams = new String[(numChilds - 1) / 2];
							//								for (int j = 1; j < numChilds; j++) {
							//									if (nListD.item(j).getNodeName().equalsIgnoreCase("instrparams")) {
							//										ninparams[k] = nListD.item(j).getFirstChild().getNodeValue();
							//										k++;
							//									}
							//									j++;
							//								}
							//								if (numHijos == 5) {
							//									NodeList nListOut = nList.item(3).getChildNodes();
							//									int numChildsOut = nListOut.getLength();
							//									noutparams = new String[(numChildsOut - 1) / 2];
							//									for (int j = 1; j < numChildsOut; j++) {
							//										if (nListOut.item(j).getNodeName().equalsIgnoreCase("outstrparams")) {
							//											noutparams[l] = nListOut.item(j).getFirstChild().getNodeValue();
							//											l++;
							//										}
							//										j++;
							//									}
							//								}
							//							}
							//Solo si existe una llamada previa a una transaccion se pueblan los nombres de los parametros para 
							//las consultas
							if (constx != null) {
								constx.nparamsIn.add(ninparams);
								constx.nparamsOut.add(noutparams);
							}
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
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new servicios.generales.WSException("Clase: MrgCons - Error al recorrer el arbol " + e.getMessage());
		}
	}
	public void createParameters() throws WSException {
		Element root = getDoc().getDocumentElement();
		NodeList nList = root.getElementsByTagName("params");
		recorreArbol(nList.item(0));
	}
	private Node findSPParams(String nconsulta) throws WSException {
		return find(nconsulta);
	}
	private Node findConsultaTX(String nconsulta) throws WSException {
		return find(nconsulta);
	}
	public synchronized ConsultaTX getConsultaTX(String TipoConsulta) throws Exception, IndexOutOfBoundsException, NullPointerException {
		recorreArbol(findConsultaTX(TipoConsulta));
		//Puebla el conjunto completo de parametros en el XML
		createParameters();
		//Extrae los nombres de los parametros de entrada y salida desde el XML y los deja en la consulta
		
		//Esto se realiza pos cada consulta que existe en la lista
		for(int i=0;i<constx.consultas.length;i++){
			recorreArbol(findSPParams(constx.consultas[i]));
			constx.setParams(listaParams, i);
		}
		return constx;
	}
	protected void setConsultaSP(Node root, ConsultaSP conssp) throws WSException {
		try {
			int type = root.getNodeType();
			switch (type) {
				case Node.DOCUMENT_NODE :
					{
						break;
					}
				case Node.COMMENT_NODE :
					{
						break;
					}
				case Node.ELEMENT_NODE :
					{
						/***********************************************************************/
						if (root.getNodeName().equalsIgnoreCase("conssp")) {
							Element rama = (Element) root;
							conssp.setConsname(rama.getAttribute("consname"));
							NodeList nList = root.getChildNodes();
							conssp.setIdPool(nList.item(1).getFirstChild().getNodeValue());
							conssp.setConsbean(nList.item(3).getFirstChild().getNodeValue());
							conssp.setConsstrproc(nList.item(5).getFirstChild().getNodeValue());
							break;
						}
						/***********************************************************************/
						/***********************************************************************/
						else if (root.getNodeName().equalsIgnoreCase("strproc")) {
							String noutparams[] = null;
							String ninparams[] = null;
							NodeList nListInParams = ((Element) root).getElementsByTagName("instrparams");
							NodeList nListOutParams = ((Element) root).getElementsByTagName("outstrparams");
							int numInParams = nListInParams.getLength();
							int numOutParams = nListOutParams.getLength();
							if (numInParams > 0) {
								ninparams = new String[numInParams];
								for (int i = 0; i < numInParams; i++) {
									ninparams[i] = nListInParams.item(i).getFirstChild().getNodeValue();
								}
							}
							if (numOutParams > 0) {
								noutparams = new String[numOutParams];
								for (int i = 0; i < numOutParams; i++) {
									noutparams[i] = nListOutParams.item(i).getFirstChild().getNodeValue();
								}
							}
							if (conssp != null)
								conssp.setNameParams(ninparams, noutparams);
							//Solo si existe una llamada previa a una transaccion se pueblan los nombres de los parametros para 
							//las consultas
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
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new servicios.generales.WSException("Clase: MrgCons - Error al recorrer el arbol " + e.getMessage());
		}
	}
}
