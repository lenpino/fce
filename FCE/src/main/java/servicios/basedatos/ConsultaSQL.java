package servicios.basedatos;

import java.util.Enumeration;
import java.util.Properties;

import servicios.generales.WSException;


public class ConsultaSQL{
	public String Consname;
	public String Consbean;
	public String Cons;
	public String colsparams[];
	public ConsSQLparams cparams[];
	public Params params[];

	/**
	 * Substring de todas las condiciones asociadas al WHERE en una sentencia SQL
	 */
	public String sqlwhere = "";
	protected java.lang.String query = "";
	private java.lang.String idPool;
public ConsultaSQL() {
	super();
}
public String getConsbean() {
	return this.Consbean;
}
public String[] getConscols() {
	return this.colsparams;
}
public String getConsname() {
	return this.Consname;
}
/**
 * 24-03-2003:	Se cambia este metodo para que retorne una clase DBParserParams en vez de ConsSQLParam
 * 27-03-2003:	Se coloca como llave de la lista de parametros su indice
 * */
public DBParserParams getConsparam() {
	DBParserParams parametros = new DBParserParams();
	parametros.entrada = new Properties();
	for(int i=0;i<this.cparams.length;i++)
		parametros.entrada.put(new Integer(i+1),cparams[i]);
	return parametros;
}
public String getConsulta() {
/*	int indice=0;
	String sqlaux = getQuery();
	while (sqlaux.indexOf("?") >= 0) {
		if(this.cparams[indice].typeparam.equalsIgnoreCase("C"))
			sqlaux = sqlaux.substring(0, sqlaux.indexOf("?")) + "'" + cparams[indice].valparam + "'" + sqlaux.substring(sqlaux.indexOf("?") + 1);
		else if(this.cparams[indice].typeparam.equalsIgnoreCase("D"))
			sqlaux = sqlaux.substring(0, sqlaux.indexOf("?")) + cparams[indice].valparam + sqlaux.substring(sqlaux.indexOf("?") + 1);
		indice++;	
	}
	*/
	return getQuery();
}
/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 02:52:52 PM)
 * @return java.lang.String
 */
public java.lang.String getIdPool() {
	return idPool;
}
public java.lang.String getParameter(jakarta.servlet.http.HttpServletRequest request, java.lang.String parameterName) throws java.lang.Exception {
	java.lang.String paramValues[] = null;
	java.lang.String key = null;
	java.lang.String paramValue = null;
	Enumeration keys;
	keys = request.getParameterNames();
	while (keys.hasMoreElements()) {
		key = (String) keys.nextElement();
		if (key.equalsIgnoreCase(parameterName))
			paramValues = request.getParameterValues(key);
		if (paramValues != null)
			paramValue = paramValues[0];
	}
	if (paramValue == null)
		paramValue = (String)request.getAttribute(parameterName);
	if (paramValue == null)
		paramValue = "";
	return paramValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 5:38:05 PM)
 */
public Params[] getParams() {
	return this.params ;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 11:13:24 PM)
 * @return java.lang.String
 */
public java.lang.String getQuery() {
	return query;
}
public String getTipParam(String nomparam) {
	if (this.getParams() != null) {
		for (int i = 0; i < this.getParams().length; i++) {
			String var = this.getParams()[i].name;
			if (var.equalsIgnoreCase(nomparam)) {
				return this.getParams()[i].type;
			}
		}
	}
	return null;
}
public void setConsbean(String var) {
	this.Consbean = var;
}
public void setConsname(String var) {
	this.Consname = var;
}

/**
 * Insert the method's description here.
 * Creation date: (24-05-2001 02:52:52 PM)
 * @param newIdPool java.lang.String
 */
public void setIdPool(java.lang.String newIdPool) {
	idPool = newIdPool;
}

/**
 * Recoge desde el request los parametros nombrados para la consulta
 * y le asigna su valor en el listado de ConsSqlParams
 * Si se trata de un tipo de dato F, entonces se asume que viene el listado de parametros
 * en el request, listo y completo.
 * Si se define la Query como dinamica en XML (<sql>?</sql>), se rescata la query desde el request
 * @param req
 * 	HttpRequest con los parametros nombrados
 */
public void setParameters(jakarta.servlet.http.HttpServletRequest req) throws WSException{
	try {
		if(this.query.equalsIgnoreCase("?")){
			this.query = (String)req.getAttribute("query");
			if(this.query == null)
				throw new WSException("Clase: ConsultaSQL - Error: Se define query variable pero no se entrega su valor via request");
		}
		//Si el numero de parametros es uno y el tipo es F entoces se extrae el listado de parametros y valores desde el request
		if(this.colsparams.length == 1 && getTipParam(this.colsparams[0]).equalsIgnoreCase("F"))
				this.cparams = (ConsSQLparams[])req.getAttribute(this.colsparams[0]);
		else {
			setSQLParams();
			for (int i = 0; i <this.cparams.length; i++) {
				String var1 = this.cparams[i].nomparam;
				this.cparams[i].valparam = getParameter(req, var1);
			}
		}
	} catch (Exception e) {
		throw new WSException("Clase: ConsultaSQL - Error: " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 5:38:05 PM)
 */
public void setParams(Properties lparams) {
	this.params = new Params[lparams.size()];
	Enumeration e = lparams.propertyNames();
	for (int i = 0; i < lparams.size(); i++) {
		String aux = (String)e.nextElement();
		this.params[i] = new Params();
		this.params[i].name = aux;
		this.params[i].type = ((ConsSQLparams)(lparams.get(aux))).typeparam;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 5:38:05 PM)
 */
public void setParamsNames(String var1[]) {
	int j=0;
	for (int i = 0; i < var1.length; i++) {
		if (var1[i]!=null)
		j++;
	}
	this.colsparams = new String[j];
	int i=0;
	int k=0;
	while(i<var1.length){
		if(var1[i] != null){
			this.colsparams[k] = var1[i];
			k++;
		}
		i++;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 11:13:24 PM)
 * @param newQuery java.lang.String
 */
public void setQuery(java.lang.String newQuery) {
	query = newQuery;
}
/**
 * Insert the method's description here.
 * Creation date: (11-04-2001 10:52:14 PM)
 * @param parametros java.lang.String[]
 */
public void setSQLParams() {
	this.cparams = new ConsSQLparams[this.colsparams.length];
	for(int i=0;i<this.colsparams.length;i++){
		cparams[i] = new ConsSQLparams();
		cparams[i].nomparam = new String( this.colsparams[i]);
		cparams[i].typeparam= new String(getTipParam(this.colsparams[i]));
	}
}
}
