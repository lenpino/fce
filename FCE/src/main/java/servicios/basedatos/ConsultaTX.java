package servicios.basedatos;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import jakarta.servlet.http.HttpSession;

import servicios.generales.WSException;

/**
 * @autor lpino
 * @Fecha 14-jul-03
*/
public class ConsultaTX {
	/**
	 * Constructor for ConsultaTX.
	 */
	public ConsultaTX() {
		super();
	}
	protected Properties inParams;
	protected Properties outParams;
	private String nInparams[];
	private String nOutparams[];
	public String consultas[];
	public String nParamsCons[];
	private String Consbean;
	private String idPool;
	public ArrayList nparamsIn = new ArrayList();
	public ArrayList nparamsOut = new ArrayList();
	private ArrayList reqValores = new ArrayList();
	private ArrayList todo = new ArrayList();
	public void setParams(Properties lparams, int indice) throws WSException {
		//Creo un TxParams por cada consulta
		TxParams consulta = new TxParams();
		//Crea la lista de parametros de entrada
		// El offset permite a los parametros de salida poseer un indice que comience donde se acaban los parametros de entrada
		int offset = 1;
		try {
			nInparams = (String[])nparamsIn.get(indice);
			if (nInparams != null) {
				inParams = new Properties();
				for (int i = 0; i < this.nInparams.length; i++) {
					//Se coloca como llave el indice para asi poder registrar en forma correcta los parametros
					ConsSQLparams aux = (ConsSQLparams) lparams.get(nInparams[i]);
					if (aux == null)
						throw new WSException("No se encontro el parametro " + nInparams[i] + " Revisar los parametros en el XML");
					else{
						ConsParamsX parm = new ConsParamsX(aux.nomparam,aux.typeparam);
						inParams.put(new Integer(i + 1), parm);
					}
				}
				// Solo si hay parametros de entrada se suma el valor del ultimo indice
				offset = this.nInparams.length + 1;
			}
			nOutparams = (String[])nparamsOut.get(indice);
			if (nOutparams != null) {
				outParams = new Properties();
				//Crea la lista de parametros de salida
				for (int i = 0; i < nOutparams.length; i++) {
					//Se coloca como llave el indice para asi poder registrar en forma correcta los parametros
					// Ojo que aca se suma el offset
					outParams.put(new Integer(i + offset), lparams.get(nOutparams[i]));
				}
			}
			//Coloco los parametros de entrada y salida (sin valores todavia) de esta consulta en su respectiva clase
			consulta.getParams().entrada = inParams;
			consulta.getParams().salida = outParams;
			//Agrego la consulta a la lista contenida en esta transaccion
			todo.add(consulta);
		}
		catch (Exception e) {
			throw new WSException("Clase: ConsultaTX Error: Error al cargar los objetos de parametros de entrada y de salida Msg: " + e.getMessage());
		}
	}
	/**
	 * getConsulta:
	 * A partir de la lista de consultas obtenidas desde el XML, se arman las llamadas a los SP con el numero correcto de parametros
	 * ParIN: Indice del nombre de consulta que se requiere armar
	 * ParOUT: Llamada lista para precompilarla a traves del CallableStatement
	 * */
	private String getConsulta(int indice) {
		String sql = "";
		StringBuffer variables = new StringBuffer();
		inParams = ((TxParams)todo.get(indice)).getParams().entrada;
		outParams = ((TxParams)todo.get(indice)).getParams().salida;
		if ((inParams == null || inParams.size() == 0) && (outParams == null || outParams.size() == 0))
			sql = "{call " + consultas[indice] + "}";
		else {
			//Por cada parametro de entrada corresponde un ?
			//Ojo que tambien hay que considerar los de salida si es necesario
			//pero no esta cubierto en esta primera etapa
			if (inParams != null) {
				for (int i = 0; i < inParams.size(); i++)
					variables.append("?,");
			}
			if (outParams != null) {
				for (int i = 0; i < outParams.size(); i++)
					variables.append("?,");
			}
			String listaVars = variables.toString();
			listaVars = listaVars.substring(0, listaVars.length() - 1);
			sql = "{call " + consultas[indice] + "(" + listaVars + ")}";
		}
		return sql;
	}
	public void setIdPool(java.lang.String newIdPool) {
		idPool = newIdPool;
	}
	public java.lang.String getIdPool() {
		return idPool;
	}
	public ArrayList getConsparam() throws WSException {
		try {
			setValues();
			return todo;
		}
		catch (Exception e) {
			throw new WSException("Clase: ConsultaTX Error: Error al convertir los parametros Msg: " + e.getMessage());
		}
	}
	/**
	 * Returns the consbean.
	 * @return String
	 */
	public String getConsbean() {
		return Consbean;
	}
	/**
	 * Sets the consbean.
	 * @param consbean The consbean to set
	 */
	public void setConsbean(String consbean) {
		Consbean = consbean;
	}
	public void setParameters(jakarta.servlet.http.HttpServletRequest req, HttpSession session) throws servicios.generales.WSException {
		Hashtable params;
		try {
			//Si existen parametros de entrada
			if (nParamsCons.length >0) {
				for (int i = 0; i < nParamsCons.length; i++) {
					//Rescata el hashtable con todos los parametros para las consulta(s)
					params =  (Hashtable)getParameter(req, session, nParamsCons[i]);
					if(params!= null)
						//Agrega la lista de parametros al conjunto total
						this.reqValores.add(params);
					else
						throw new WSException("Clase: ConsultaTX Error:No coinciden los nombres de los parametros entre el XML y el que trae el request - Parametro: " + nParamsCons[i]);
				}
			}
			//Configuro las consultas para su ejecucion
		}
		catch (Exception e) {
			throw new WSException("Clase: ConsultaTX Error:Error al traspasar los valores de las variables desde el request Msg:" + e.getMessage());
		}
	}
	
	public Object getParameter(jakarta.servlet.http.HttpServletRequest request, HttpSession session, java.lang.String parameterName) throws java.lang.Exception {
		 if (request.getAttribute(parameterName) != null)
			return request.getAttribute(parameterName);
		else if (session != null)
			return session.getAttribute(parameterName);
		else
			return null;
	}
	private void setValues() throws WSException{
		try {
			for(int i=0;i<todo.size();i++){
				((TxParams)todo.get(i)).setConsulta(getConsulta(i));
				//
				String aux="";
				//Parametros de entrada de la iesima consulta
				String[] in = (String[])nparamsIn.get(i);
				//Parametros de salida de la iesima consulta
				String[] out =  (String[])nparamsOut.get(i);
				//Parametros del request (in y out)
				//Extraigo el iesimo conjunto de parametros que pertenecen a la iesima consulta
				Hashtable inreq = (Hashtable)reqValores.get(i);
				//Para cada parametro de entrada de la iesima consulta
				if(nparamsIn.get(i) != null)
					for(int j=0;j<((String[])nparamsIn.get(i)).length;j++){
						//Extraigo el jesimo nombre de parametro de entrada
						aux = in[j];
						//Saco el jesimo ConsParamsX de la iesima consulta				
						ConsParamsX par = (ConsParamsX)((TxParams)todo.get(i)).getParams().entrada.get(new Integer(j+1));
						//Entrego el conjunto de valores del vector de entrada a partir del nombre del parametro
						par.valparams = (ArrayList)inreq.get(aux);
						//Configuro el numero de parametros que esta consulta posee. Dado que solo puede haber parametros de entrada me basta con contar los de ese tipo
						if(inreq.get(aux) != null && ((TxParams)todo.get(i)).numeroPars == 0)
							((TxParams)todo.get(i)).numeroPars = ((ArrayList)inreq.get(aux)).size();							
					}
				//Para cada parametro de salida de la iesima consulta
				if(nparamsOut.get(i) != null)
					for(int j=0;j<((String[])nparamsOut.get(i)).length;j++){
						//Extraigo el jesimo nombre de parametro de salida
						aux = out[j];
						//Saco el jesimo ConsParamsX de la iesima consulta				
						ConsParamsX par = (ConsParamsX)((TxParams)todo.get(i)).getParams().salida.get(new Integer(j+1));
						//Entrego el conjunto de valores del vector de entrada a partir del nombre del parametro
						par.valparams = (ArrayList)inreq.get(aux);
					}
			}
		}
		catch (Exception e) {
			throw new WSException("Clase: ConsultaTX - Error: Problema al entregar los valores desde el Hashtable del request hacia la clase ConsParamsX - MSG: " + e.getMessage());
		}
	}
}
