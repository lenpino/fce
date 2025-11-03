package servicios.basedatos;

import java.util.Properties;

import jakarta.servlet.http.HttpSession;

import servicios.generales.WSException;


/**
 * @autor lpino
 * @Fecha 26-mar-03
 * 24/03/2003:	Se cambia el uso de ConsSQLParam por el de DBParserParams generalizandose para
*							el uso de parametros de salida en los SPs
* 26/03/2003:		Borrada el metodo de setConsParam utilizado en el mapeo de nombres de parametros
* 04/07/2003:		Se elimina la recursividad en la lectura del arbol
* 06/07/2003:		Se simplifica la el metodo getParameter para que mejore el rendimiento
*/
public class ConsultaSP {
	public String Consname;
	public String Consjsp;
	public String Consbean;
	public String Consstrproc;
	public ConsSPparams cparams[];
	private java.lang.String idPool;
	private String nInparams[];
	private String nOutparams[];
public ConsultaSP() {
	super();
}
public String getConsbean() {
	return this.Consbean;
}
public String getConsjsp() {
	return this.Consjsp;
}
public String getConsname() {
	return this.Consname;
}
public String getConsstrproc() {
	return this.Consstrproc;
}
//05/12/2000: Se agrego la condicion de, si el valor del
//			parametro es null o vacio, siendo de tipo "C", se coloca
//			"null" sin cremillas simples como parametro al store procedure.
//06/12/2000: Se cambia el string que retorna para servir al bean que usa
//			CallableStatement. No se incluye el codigo de retorno dado un error
//			Null ocurrido al tratar de registrar el primer parametro.
//31/01/2001: Para que los store Procedures funcionen en DB2 requieren los parentesis
//			en la lista de parametros asi que se agregaron. Resuelve el problema
//			en la Extranet de Proveedores y es compatible con Sybase (TarjetaSodimac)
//26/02/2003:	Solo retorna el string con el prototipo de la llamada al store procedure para que sea 
//						en la preparación del CallableStatement en donde se asignen los valores de
//						los parametros
//
public String getConsulta(DBParserParams losParametros) {
	String sql = "";
	StringBuffer variables = new StringBuffer();
	if ((losParametros.entrada == null || losParametros.entrada.size() == 0) && (losParametros.salida == null || losParametros.salida.size() == 0))
		sql = "{call " + this.getConsstrproc() + "}";
	else {
		//Por cada parametro de entrada corresponde un ?
		//Ojo que tambien hay que considerar los de salida si es necesario
		//pero no esta cubierto en esta primera etapa
		if(losParametros.entrada != null){ 
			for(int i=0;i<losParametros.entrada.size();i++)
				variables.append("?,");
		}
		if(losParametros.salida != null){
			for(int i=0;i<losParametros.salida.size();i++)
				variables.append("?,");
		}
		String listaVars = variables.toString();
		listaVars = listaVars.substring(0,listaVars.length() -1);
		sql =  "{call " + this.getConsstrproc() +"(" + listaVars +  ")}";
	}
	return sql;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 02:52:29 PM)
 * @return java.lang.String
 */
public java.lang.String getIdPool() {
	return idPool;
}
public java.lang.String getParameter(jakarta.servlet.http.HttpServletRequest request, HttpSession session, java.lang.String parameterName) throws WSException {
	if(request.getParameter(parameterName) != null){
		if (request.getAttribute(parameterName) != null)
			throw new WSException("Clase: ConsultaSP Error: Nombre de parametro " + parameterName + " duplicado Msg: Existe una variable con el mismo nombre en los parametros y en los atributos del REQUEST");
		else
			return request.getParameter(parameterName);
	}
	else if (request.getAttribute(parameterName) != null)
		return (String)request.getAttribute(parameterName);
	else if (session != null)
		return (String) session.getAttribute(parameterName);
	else
		return null;
}
public void setConsbean(String var) {
	this.Consbean = var;
}
public void setConsjsp(String var) {
	this.Consjsp = var;
}
public void setConsname(String var) {
	this.Consname = var;
}
public void setConsstrproc(String var) {
	this.Consstrproc = var;
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 02:52:29 PM)
 * @param newIdPool java.lang.String
 */
public void setIdPool(java.lang.String newIdPool) {
	idPool = newIdPool;
}
public void setParameters(jakarta.servlet.http.HttpServletRequest req, HttpSession session, DBParserParams losParametros) throws servicios.generales.WSException {
	try {
		//Si existen parametros de entrada
		if(losParametros.entrada != null){
			for(int i=0;i<losParametros.entrada.size();i++){
				String vaux =((ConsSQLparams)(losParametros.entrada.get(new Integer(i+1)))).nomparam;
				((ConsSQLparams)losParametros.entrada.get(new Integer(i+1))).valparam = (getParameter(req,session,vaux));
			}
		}
	}
	catch (Exception e) {
		if(e instanceof WSException)
			throw (WSException)e;
		else
			throw new WSException("Clase: ConsultaSP Error:Error al traspasar los valores de las variables desde el request Msg:" + e.getMessage());
	}
}
public void setParams(Properties lparams, DBParserParams losParametros) throws WSException {
	//Crea la lista de parametros de entrada
	// El offset permite a los parametros de salida poseer un indice que comience donde se acaban los parametros de entrada
	int offset = 1;
	try {
		if (nInparams != null) {
			for (int i = 0; i < this.nInparams.length; i++) {
				//Se coloca como llave el indice para asi poder registrar en forma correcta los parametros
				ConsSQLparams aux = (ConsSQLparams) lparams.get(nInparams[i]);
				if(aux == null)
					throw new WSException("No se encontro el parametro " + nInparams[i] + " Revisar los parametros en el XML"); 
				else{
					ConsSQLparams parm = new ConsSQLparams(aux.nomparam,aux.typeparam);
					losParametros.entrada.put(new Integer(i+1),parm);
				}
			}
			// Solo si hay parametros de entrada se suma el valor del ultimo indice
			offset = this.nInparams.length + 1;
		}
		if(nOutparams != null){
			//Crea la lista de parametros de salida
			for (int i = 0; i < nOutparams.length; i++) {
				//Se coloca como llave el indice para asi poder registrar en forma correcta los parametros
				ConsSQLparams aux = (ConsSQLparams) lparams.get(nOutparams[i]);
				if(aux == null)
					throw new WSException("No se encontro el parametro " + nOutparams[i] + " Revisar los parametros en el XML");
				else{ 
					ConsSQLparams parm = new ConsSQLparams(aux.nomparam,aux.typeparam);
					// Ojo que aca se suma el offset
					losParametros.salida.put(new Integer(i+offset), parm);
				}
			}
		}
	} 
	catch (Exception e) {
		throw new WSException("Clase: ConsultaSP Error: Error al cargar los objetos de parametros de entrada y de salida Msg: "+ e.getMessage());
	}
}
public void setNameParams(String inparams[], String outparams[]){
	this.nInparams = inparams;
	this.nOutparams = outparams;
}
}
