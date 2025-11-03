package servicios.generales;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Bean para paginación de bean de resultados.
 * @author: Iván Pérez
 * @modified: Leonardo Pino
 * 12-01-2001:	Agrega metodo de inicializacion y variable de instancia program. Ademas
 *				modifica los metodos que crean los links para incluir el nombre del programa
 * 14-02-2001:	Agrega metodos para incluir parametros adicionales en los links de
 *				paginacion
 * 14-02-2001:	Agrega logica para evitar que se use el mismo paginador si el programa
 *				es distinto o si los parametros son distintos
 * 10-02-2004:  Agregan dos metodos, getPaginaAnterior() y getPaginaSiguiente().
 * 				Estos metodos devuelven un HashTable con las variables reqName,nombreParam y tdms.
 * 				Se crearon con el fin de que la sgte y anterior pagina se puedan mover con Post y no
 * 				con Get, como se hace en los metodos getPaginaAnt() y getPaginaSig()
 * 03-09-2004:	Implementa la interface serializable para poder  persistir las sesiones
 */
public class BeanPaginacion implements Serializable{
	// Campos relacionados con el servlet ResultSet.
	protected servicios.generales.GenOutBean result;
	protected servicios.generales.OutputServiceBean salida = new servicios.generales.OutputServiceBean();
	protected String destinoServlet;
	protected int largoRS = 0;
	protected java.lang.String beanName = "";
	protected java.lang.String prgName = "";
		
	// Líneas que se mostrarán por cada página.
	protected int lineasPorPagina = 0;

	// Indica la página actual a mostrar.
	protected int paginaActual = 0;

	// Contador de página para mostrar los rangos.
	protected int contPagina;

	// Contador de línea para mostrar los datos.
	protected int contLinea;

	// Columna del ResultSet que se muestra como etiqueta en los rangos.
	protected int columnaParaRango;

	// Nombre el parámetro en que va la página escogida.
	protected String nombreParam = "pagina";

	// Nombre de las imágenes para página anterior/siguiente.
	protected String pagAntAct = null;
	protected String pagSigAct = null;
	protected String pagAntInact = null;
	protected String pagSigInact = null;
	protected java.lang.String program = "";
	protected java.lang.String imagesPath = "";
	protected java.util.Properties paramVals;
/**
 * Insert the method's description here.
 * Creation date: (05-01-2001 03:46:21 PM)
 */
public BeanPaginacion() {
	salida = new servicios.generales.OutputServiceBean();
	lineasPorPagina = 0;
	paginaActual = 0;
	largoRS = 0;
	nombreParam = "pagina";
}
/**
 * Este método debería redefinirse en clases hija que deseen aplicar
 * algún formato a los límites de los rangos de la página.
 * @return java.lang.String
 * @param valor java.lang.String
 */
protected String formateaColumnaRango(String valor) {
	return valor;
}
protected final String formatFecha(String fecha) {

	// Si la fecha viene aaaa-MM-dd.
	if (fecha.charAt(4) == '-')
		return fecha.substring(8) + "-" + fecha.substring(5, 7) + "-" + fecha.substring(0, 4);
	else
		if (fecha.charAt(2) == '-') {

			// Si la fecha viene dd-MM-aaaa.
			return fecha;
		}
		else {

			// Si la fecha viene aaaaMMdd.
			if (Integer.parseInt(fecha.substring(4)) < 1300)
				return fecha.substring(6) + "-" + fecha.substring(4, 6) + "-" + fecha.substring(0, 4);

			// Si la fecha viene ddMMaaaa.
			else
				return fecha.substring(0, 2) + "-" + fecha.substring(2, 4) + "-" + fecha.substring(4);
		}
}
protected final String formatMto(String value) {
	java.text.DecimalFormat dec;
	java.lang.Double valor = null;

	//
	dec = new java.text.DecimalFormat();
	dec.applyPattern("#,##0");
	valor = java.lang.Double.valueOf(value);

	//
	return dec.format(valor.doubleValue());
}
protected final String formatRUT(String rutSF) {
	int largo = rutSF.length();
	int posAct = (largo - 2) % 3 + 1;
	StringBuffer rutFormateado = new StringBuffer();

	// Se inicia el formateo.
	rutFormateado.append(rutSF.substring(0, posAct));

	// Se recorre el String.
	while (posAct < largo - 3) {
		posAct += 3;
		rutFormateado.append('.').append(rutSF.substring(posAct - 3, posAct));
	}

	// Se agrega el DV.
	rutFormateado.append('-').append(rutSF.substring(largo - 1));

	// Retorno.
	return rutFormateado.toString();
}
protected final String formatString(String value, int Len) {
	String ceros;

	//
	ceros = new String("00000000000000000000");
	if (value.length() > Len)
		return value.substring(Len);

	//
	return ceros.substring(0, Len - value.length()) + value;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 11:35:34 AM)
 * @return java.lang.String
 */
public java.lang.String getBeanName() {
	return beanName;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 11:01:07 AM)
 * @return java.lang.String
 */
public java.lang.String getImagesPath() {
	return imagesPath;
}
public final void getNextRango() throws ArrayIndexOutOfBoundsException {
	contPagina++;
	if ((contPagina - 1) * lineasPorPagina >= largoRS)
		throw new ArrayIndexOutOfBoundsException();
}
public final void getNextRow() throws ArrayIndexOutOfBoundsException {

	// Si el RS es null, se arroja ArrayIndexOutOfBoundsException().
	if (result == null)
		throw new ArrayIndexOutOfBoundsException();

	// Si se pasó el final del RS, se arroja ArrayIndexOutOfBoundsException().
	if (contLinea + contPagina * lineasPorPagina >= largoRS)
		throw new ArrayIndexOutOfBoundsException("Se pasó el final del ResultSet.");

	// Se avanza el cursor, y se verifica que no se haya pasado del final de la página.
	if (++contLinea > lineasPorPagina)
		throw new ArrayIndexOutOfBoundsException("Fuera del rango de la página en el ResultSet.");
}
/**
 * Insert the method's description here.
 * Creation date: (05-01-2001 03:38:20 PM)
 * @return java.lang.Object
 */
public Object getOutputBean() throws servicios.generales.WSException {
	try {
		salida.setValues(result, this.lineasPorPagina, this.paginaActual);
		return salida;
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al configurar la salida paginada");
	}
}
public final String getPaginaAnt() {
	StringBuffer link = new StringBuffer();
	if (pagAntAct != null) {
		if (paginaActual > 0) {
			link.append("<A HREF=\"").append(destinoServlet).append('?');
			link.append("reqName=").append(getProgram()).append('&');
			link.append(nombreParam).append('=').append(paginaActual - 1);
			link.append("&tdms=").append(System.currentTimeMillis());
			link.append("\"><IMG SRC=\"").append(pagAntAct).append("\" BORDER=\"0\"></A>");
		}
		else
			link.append("<IMG SRC=\"").append(pagAntInact).append("\" BORDER=\"0\">");
	}
	return link.toString();
}
public final String getPaginaAntCP(String parametros) {
	StringBuffer link = new StringBuffer();
	if (pagAntAct != null) {
		if (paginaActual > 0) {
			link.append("<A HREF=\"").append(destinoServlet).append('?');
			link.append("reqName=").append(getProgram()).append('&');
			link.append(nombreParam).append('=').append(paginaActual - 1);
			link.append("&tdms=").append(System.currentTimeMillis());
			link.append(parametros);
			link.append("\"><IMG SRC=\"").append(pagAntAct).append("\" BORDER=\"0\"></A>");
		}
		else
			link.append("<IMG SRC=\"").append(pagAntInact).append("\" BORDER=\"0\">");
	}
	return link.toString();
}
public final String getPaginaSig() {
	StringBuffer link = new StringBuffer();
	if (pagSigAct != null) {
		if ((paginaActual + 1) * lineasPorPagina < largoRS) {
			link.append("<A HREF=\"").append(destinoServlet).append('?');
			link.append("reqName=").append(getProgram()).append('&');
			link.append(nombreParam).append('=').append(paginaActual + 1);
			link.append("&tdms=").append(System.currentTimeMillis());
			link.append("\"><IMG SRC=\"").append(pagSigAct).append("\" BORDER=\"0\"></A>");
		}
		else
			link.append("<IMG SRC=\"").append(pagSigInact).append("\" BORDER=\"0\">");
	}
	return link.toString();
}
public final String getPaginaSigCP(String parametros) {
	StringBuffer link = new StringBuffer();
	if (pagSigAct != null) {
		if ((paginaActual + 1) * lineasPorPagina < largoRS) {
			link.append("<A HREF=\"").append(destinoServlet).append('?');
			link.append("reqName=").append(getProgram()).append('&');
			link.append(nombreParam).append('=').append(paginaActual + 1);
			link.append("&tdms=").append(System.currentTimeMillis());
			link.append(parametros);
			link.append("\"><IMG SRC=\"").append(pagSigAct).append("\" BORDER=\"0\"></A>");
		}
		else
			link.append("<IMG SRC=\"").append(pagSigInact).append("\" BORDER=\"0\">");
	}
	return link.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 03:07:58 PM)
 * @return java.lang.String[]
 */
public java.util.Properties getParamVals() {
	return paramVals;
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 02:37:04 PM)
 * @return java.lang.String
 */
public java.lang.String getPrgName() {
	return prgName;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:53:33 AM)
 * @return java.lang.String
 */
public java.lang.String getProgram() {
	return program;
}
public final String getRango() {
	StringBuffer linkRango = new StringBuffer();
	int finRango;
	try {

		// Se calcula la última fila del rango actual (si el el último rango podría
		// que no sea el número de líneas por página predeterminado, sino que menor).
		if (contPagina * lineasPorPagina > largoRS)
			finRango = largoRS;
		else
			finRango = contPagina * lineasPorPagina;

		// Si el rango mostrado NO es el actual, se agrega el link.
		if (paginaActual != contPagina - 1) {
			linkRango.append("<A HREF=\"").append(destinoServlet).append("?");
			linkRango.append("reqName=").append(getProgram()).append('&');
			linkRango.append(nombreParam).append("=").append(contPagina - 1);
			linkRango.append("&tdms=").append(System.currentTimeMillis()).append("\">");
		}

		// El rango de valores. Con el método setColumnaEnRango() se puede definir
		// cual de las columnas es la que se usará como límites a mostrar de los
		// rangos. Si estos valores requieren algún tratamiento especial, en la
		// clase hija de esta se puede redefinir el método formateaColumnaRango() por
		// lo que se desee. Obviamente, el ResultSet que se entregue debería estar
		// ordenado por la columna que aquí se especifique.
		linkRango.append(formateaColumnaRango(result.getObject((contPagina - 1) * lineasPorPagina + 1, columnaParaRango).toString()));

		// Si el inicio del rango es igual al fin (por ej. la última página
		// tiene sólo un elemento para mostrar), basta con un sólo valor.
		if (finRango != contPagina * lineasPorPagina + 1)
			linkRango.append('-').append(formateaColumnaRango(result.getObject(finRango, columnaParaRango).toString()));

		// Si el rango mostrado NO es el actual, se cierra el link.
		if (paginaActual != contPagina - 1)
			linkRango.append("</A>");
	}
	catch (java.sql.SQLException e) {
	}

	// Se retorna el rango, con o sin links.
	return linkRango.toString();
}
public final String getRangoParams(String parametros) {
	StringBuffer linkRango = new StringBuffer();
	int finRango;
	try {

		// Se calcula la última fila del rango actual (si el el último rango podría
		// que no sea el número de líneas por página predeterminado, sino que menor).
		if (contPagina * lineasPorPagina > largoRS)
			finRango = largoRS;
		else
			finRango = contPagina * lineasPorPagina;

		// Si el rango mostrado NO es el actual, se agrega el link.
		if (paginaActual != contPagina - 1) {
			linkRango.append("<A HREF=\"").append(destinoServlet).append("?");
			linkRango.append("reqName=").append(getProgram()).append('&');
			linkRango.append(nombreParam).append("=").append(contPagina - 1);
			linkRango.append("&tdms=").append(System.currentTimeMillis()).append(parametros).append("\">");
		}

		// El rango de valores. Con el método setColumnaEnRango() se puede definir
		// cual de las columnas es la que se usará como límites a mostrar de los
		// rangos. Si estos valores requieren algún tratamiento especial, en la
		// clase hija de esta se puede redefinir el método formateaColumnaRango() por
		// lo que se desee. Obviamente, el ResultSet que se entregue debería estar
		// ordenado por la columna que aquí se especifique.
		linkRango.append(formateaColumnaRango(result.getObject((contPagina - 1) * lineasPorPagina + 1, columnaParaRango).toString()));

		// Si el inicio del rango es igual al fin (por ej. la última página
		// tiene sólo un elemento para mostrar), basta con un sólo valor.
		if (finRango != contPagina * lineasPorPagina + 1)
			linkRango.append('-').append(formateaColumnaRango(result.getObject(finRango, columnaParaRango).toString()));

		// Si el rango mostrado NO es el actual, se cierra el link.
		if (paginaActual != contPagina - 1)
			linkRango.append("</A>");
	}
	catch (java.sql.SQLException e) {
	}

	// Se retorna el rango, con o sin links.
	return linkRango.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (02-03-2001 04:09:52 PM)
 * @return servicios.generales.GenOutBean
 */
public GenOutBean getResult() {
	return this.result;
}
public final void inicializaContadores() {
	contLinea = 0;
	contPagina = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 09:37:26 AM)
 * @param pageParams servicios.control.PageData
 */
public void init(jakarta.servlet.http.HttpServletRequest req, servicios.control.PageData pageParams) {
	setBeanName(pageParams.getBeanPaginado());
	setPrgName(req.getParameter("reqName"));
	initParamVals(req.getParameterNames(),req);
	setResultSet((servicios.generales.GenOutBean)req.getAttribute(pageParams.getBeanPaginado()));
	setDestinoServlet(pageParams.getAkaServlet());
	setNombreParametro("pagina");
	setColumnaEnRango(pageParams.getNumColumna());
	setProgram(pageParams.getPrograma());
	setImagesPath(pageParams.imagenes[0]);
	setImagenesPaginacion(pageParams.imagenes[1],pageParams.imagenes[2],pageParams.imagenes[3],pageParams.imagenes[4]);
	this.lineasPorPagina = pageParams.getNumLineas();
	inicializaContadores();
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 03:21:01 PM)
 * @param parametros java.util.Enumeration
 */
private void initParamVals(java.util.Enumeration paramsName, jakarta.servlet.http.HttpServletRequest request) {
	java.util.Properties parametros;
	String nombre;
	String valorSeleccion;
	String values[];
	parametros = new java.util.Properties();
	while(paramsName.hasMoreElements()){
		nombre = (String)paramsName.nextElement();
		valorSeleccion = request.getParameterValues(nombre)[0];
		parametros.put(nombre,valorSeleccion);											
	}
	setParamVals(parametros);
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 03:44:48 PM)
 * @return boolean
 * @param req jakarta.servlet.http.HttpServletRequest
 */
public boolean mismosParams(jakarta.servlet.http.HttpServletRequest req) {
	java.util.Enumeration enumeracion;
	String nomParam;
	String valorSeleccion;
	String valorActual;
	String valorNuevo;
	//Extraigo el nombre de los parametros del request entrante
	enumeracion = req.getParameterNames();

	while(enumeracion.hasMoreElements()){
		//Saco el nombre del parametro y lo asigno a nomparam
		nomParam = (String)enumeracion.nextElement();
		//Rescato el valor actual del parametro, si no esta es null y no me importa
		valorActual = getParamVals().getProperty(nomParam);
		//Rescato el valor del parametro que viene en el request entrante
		valorNuevo = req.getParameterValues(nomParam)[0];
		//Si ambos valores estan y si el parametro es distinto del parametro del tiempo
		if(valorActual != null && valorNuevo !=null && !nomParam.equalsIgnoreCase("tdms"))
			if(!valorActual.equalsIgnoreCase(valorNuevo))
				//Si no son iguales se retorna falso sin seguir buscando
				return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 11:35:34 AM)
 * @param newBeanName java.lang.String
 */
public void setBeanName(java.lang.String newBeanName) {
	beanName = newBeanName;
}
public final void setColumnaEnRango(int numeroColumna) {
	columnaParaRango = numeroColumna;
}
public final void setDestinoServlet(String destino) {
	destinoServlet = destino;
}
public final void setImagenesPaginacion(String antAct, String antInact, String sigAct, String sigInact) {
	pagAntAct = getImagesPath() + antAct;
	pagAntInact = getImagesPath() + antInact;
	pagSigAct = getImagesPath() + sigAct;
	pagSigInact = getImagesPath() + sigInact;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 11:01:07 AM)
 * @param newImagesPath java.lang.String
 */
public void setImagesPath(java.lang.String newImagesPath) {
	imagesPath = newImagesPath;
}
public final void setLineasPorPagina(int lpp) {
	lineasPorPagina = lpp;
}
public final void setNombreParametro(String nomParam) {
	nombreParam = nomParam;
}
public final void setPaginaActual(int indAct) {
	paginaActual = indAct;
	inicializaContadores();
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 03:07:58 PM)
 * @param newParamVals java.lang.String[]
 */
public void setParamVals(java.util.Properties newParamVals) {
	paramVals = newParamVals;
}
/**
 * Insert the method's description here.
 * Creation date: (14-02-2001 02:37:04 PM)
 * @param newPrgName java.lang.String
 */
public void setPrgName(java.lang.String newPrgName) {
	prgName = newPrgName;
}
/**
 * Insert the method's description here.
 * Creation date: (12-01-2001 10:53:33 AM)
 * @param newProgram java.lang.String
 */
public void setProgram(java.lang.String newProgram) {
	program = newProgram;
}
public final void setResultSet(servicios.generales.GenOutBean rs) {
	try {
		paginaActual = 0;
		result = rs;
		largoRS = result.size();
	}
	catch (java.sql.SQLException e) {
	}
}
protected final java.lang.Object valueAtColumn(int column) throws java.lang.ArrayIndexOutOfBoundsException {

	// Comprueba si es RS es null para generar una excepción.
	if (result == null)
		throw new java.lang.ArrayIndexOutOfBoundsException("ResultSet está vacío.");
	try {

		// Se comprueba si se salió del rango de la página actual.
		if (contLinea > lineasPorPagina)
			throw new java.lang.ArrayIndexOutOfBoundsException("Fuera del rango de la página en el ResultSet.");

		// Retorna el elemento correspondiente al nuevo índice.
		return result.getObject(contLinea + paginaActual * lineasPorPagina, column);
	}
	catch (java.sql.SQLException e) {
	}
	return "*";
}
public final Hashtable getPaginaAnterior() {
	Hashtable paginaAnt = new Hashtable();
	paginaAnt.put("reqName",getProgram());
	if (paginaActual > 0)
		paginaAnt.put(nombreParam, new Integer(paginaActual - 1));
	else
		paginaAnt.put(nombreParam, "-1");
	paginaAnt.put("tdms",new Long(System.currentTimeMillis()));
	return paginaAnt;
}
public final Hashtable getPaginaSiguiente() {
	Hashtable paginaSig = new Hashtable();
	paginaSig.put("reqName",getProgram());
	if ((paginaActual + 1) * lineasPorPagina < largoRS)
		paginaSig.put(nombreParam,new Integer(paginaActual + 1));
	else
		paginaSig.put(nombreParam,"-1");
	paginaSig.put("tdms",new Long(System.currentTimeMillis()));
	return paginaSig;
}
public int getLineasPorPagina() {
	return lineasPorPagina;
}
}
