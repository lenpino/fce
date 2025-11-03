package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (06-09-2000 12:13:13 PM)
 * @author: Leonardo Pino
 * 24-03-2003:	Se agrega el metodo de setValues con nuevos parametros para configurar la salida a partir del statement
 * 						y asi poder cubrir los parametros de salida para los storeprocedures
 */
import java.sql.CallableStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import servicios.basedatos.ConsSQLparams;
import servicios.basedatos.DBParserParams;
public class OutputServiceBean extends servicios.generales.GenOutBean {
	/**
	 * ResultSetService constructor comment.
	 */
	public OutputServiceBean() {
		super();
	}
	/**
	 * ResultSetService constructor comment.
	 * @param jdbcResultSet java.sql.ResultSet
	 * @exception java.sql.SQLException The exception description.
	 */
	public OutputServiceBean(java.sql.ResultSet jdbcResultSet) throws java.sql.SQLException {
		super(jdbcResultSet);
	}
	/**
	 * ResultSetService constructor comment.
	 * @param defProp java.util.Properties
	 */
	public OutputServiceBean(java.util.Properties defProp) {
		super(defProp);
	}
	/**
	 * ResultSetService constructor comment.
	 * @param defProp servicios.basedatos.ResultSet
	 */
	public OutputServiceBean(servicios.generales.GenOutBean defProp) {
		super(defProp);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 11:31:45 AM)
	 */
	public void clearRS() {
		elemVS.clear();
		nextElem = 0;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10-11-2000 12:23:43 PM)
	 * @return java.lang.String
	 * @param valor java.lang.Object
	 */
	public String formatDate(Object valor) {
		String aux = valor.toString();
		if (!aux.equalsIgnoreCase("&nbsp;") && !aux.equalsIgnoreCase("")) {
			String anio = aux.substring(0, 4);
			String mes = aux.substring(4, 6);
			String dia = aux.substring(6);
			return dia + "/" + mes + "/" + anio;
		}
		else
			return aux;
	}
	/**
	 * Metodo utilitario para formatear la fecha en la salida.
	 * Creation date: (10-11-2011 12:23:43 PM)
	 * @return java.lang.String
	 * Cadena con la fecha en el formato deseado
	 * @param valor java.util.Date
	 * Fecha a formatear
	 */
	public static String formatDate(Date valor, String formato) {
		SimpleDateFormat sdf = new SimpleDateFormat(formato);
		return sdf.format(valor);
	}
	/**
	 * Este método se creó en VisualAge.
	 * @return java.lang.String
	 * @param value java.lang.String
	 * 30-08-2003:	Retorna un blanco en caso de que se ingrese un blanco
	 */
	public String formatMto(Object monto, String formato) {
		try {
			if(monto == null)
				return "&nbsp;";
			if(monto.toString().equalsIgnoreCase("&nbsp;"))
				return (String)monto;
			java.util.Locale pais = java.util.Locale.GERMANY;
			String value = monto.toString();
			java.text.DecimalFormat dec = (java.text.DecimalFormat) java.text.NumberFormat.getInstance(pais);
			java.lang.Double valor = null;
			dec.applyPattern(formato);
			valor = java.lang.Double.valueOf(value);
			return dec.format(valor.doubleValue());
		}
		catch (Exception e) {
			return "&nbsp;";
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20-11-2000 10:32:00 AM)
	 * @return java.lang.String
	 * @param rut java.lang.Object
	 */
	public String formatRut(Object rut) {
		String value = rut.toString();
		value = value.trim();
		java.util.Locale pais = java.util.Locale.GERMANY;
		if (!value.equalsIgnoreCase("&nbsp;")) {
			char DV = value.charAt(value.length() - 1);
			value = value.substring(0, value.length() - 1);
			java.text.DecimalFormat dec = (java.text.DecimalFormat) java.text.NumberFormat.getInstance(pais);
			java.lang.Double valor = null;
			dec.applyPattern("#,##0");
			valor = java.lang.Double.valueOf(value);
			return dec.format(valor.doubleValue()) + "-" + DV;
		}
		else
			return "&nbsp;";
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param column int
	 */
	@Override
	public Object getObject(int row, int column) throws java.sql.SQLException, ArrayIndexOutOfBoundsException {
		Object cols[];
		Object aux;
		if (elemVS == null) {
			return "Error, sin conjunto de resultados";
		}
		if (row > elemVS.size())
			throw new ArrayIndexOutOfBoundsException();
		if (row < 1)
			throw new ArrayIndexOutOfBoundsException();
		if (row > 0) {
			cols = (Object[]) elemVS.get(new Integer(row));
			if (cols == null)
				return null;
			aux = cols[column];
			if (aux instanceof String && (((String) aux).equalsIgnoreCase("null") || ((String) aux).equalsIgnoreCase("")))
				aux = "&nbsp;";
			else if (aux == null)
				aux = "&nbsp;";
			return aux;
		}
		else
			return "&nbsp;";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/9/00 6:20:59 p.m.)
	 * @return java.lang.Object
	 * @param row int
	 * @param column java.lang.String
	 * @exception java.sql.SQLException The exception description.
	 */
	@Override
	public Object getObject(int row, String columnName) throws java.sql.SQLException {
		int colNumber = getColumnNumber(columnName);
		if (colNumber > 0)
			return this.getObject(row, colNumber);
		else
			return "&nbsp;";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29-09-2000 01:56:16 PM)
	 * @param valor java.lang.Object
	 */
	public void setValues(Object valor) throws java.sql.SQLException, WSException {
		if (valor instanceof java.sql.ResultSet) {
			java.sql.ResultSet jdbcResultSet = (java.sql.ResultSet) valor;
			this.elemVS = new java.util.Properties();
			this.nextElem = 0;
			// Se obtiene la MetaData del RS original.
			java.sql.ResultSetMetaData metaData = jdbcResultSet.getMetaData();
			this.cantidadColumnas = metaData.getColumnCount();
			this.nombreColumnas = new String[this.cantidadColumnas + 1];
			this.tiposDatos = new String[this.cantidadColumnas];
			// Se copian los nombres de las columnas originales.
			for (int i = 0; i < this.cantidadColumnas; i++){
				this.nombreColumnas[i + 1] = metaData.getColumnName(i + 1);
				this.tiposDatos[i] = metaData.getColumnClassName(i+1);
			}
			// Se copian los datos.
			Object fila[];
			while (jdbcResultSet.next()) {
				fila = new Object[this.cantidadColumnas + 1];
				for (int j = 0; j < this.cantidadColumnas; j++)
					fila[j + 1] = jdbcResultSet.getObject(j + 1);
				this.addElem(fila);
			}
			//	jdbcResultSet.close();
			this.beforeFirst();
		}
		if(valor instanceof Vector){
			CallableStatement value = (CallableStatement)((Vector)valor).get(0);
			DBParserParams meta = (DBParserParams)((Vector)valor).get(1);
			this.setValues(value,meta);
		}
		if(valor instanceof Properties){
			this.cantidadColumnas = ((String[])((Properties)valor).get("nombre")).length - 1;
			this.nombreColumnas = (String[])((Properties)valor).get("nombre");
			this.tiposDatos = (String[])((Properties)valor).get("tipo");
			((Properties)valor).remove("tipo");
			((Properties)valor).remove("nombre");
			this.elemVS = (Properties)valor;
			this.nextElem = 0;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12-09-2000 11:28:12 AM)
	 * @param param java.sql.ResultSet
	 */
	public void setValues(java.sql.ResultSet jdbcResultSet) throws java.sql.SQLException {
		this.elemVS = new java.util.Properties();
		this.nextElem = 0;
		// Se obtiene la MetaData del RS original.
		java.sql.ResultSetMetaData metaData = jdbcResultSet.getMetaData();
		this.cantidadColumnas = metaData.getColumnCount();
		this.nombreColumnas = new String[this.cantidadColumnas + 1];
		this.tiposDatos = new String[this.cantidadColumnas];
		// Se copian los nombres de las columnas originales.
		for (int i = 0; i < this.cantidadColumnas; i++){
			this.nombreColumnas[i + 1] = metaData.getColumnName(i + 1);
			this.tiposDatos[i] = metaData.getColumnClassName(i+1);
		}
		// Se copian los datos.
		Object fila[];
		while (jdbcResultSet.next()) {
			fila = new Object[this.cantidadColumnas + 1];
			for (int j = 0; j < this.cantidadColumnas; j++)
				fila[j + 1] = jdbcResultSet.getObject(j + 1);
			this.addElem(fila);
		}
		//	jdbcResultSet.close();
		this.beforeFirst();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05-01-2001 03:57:32 PM)
	 * @param datos servicios.generales.GenOutBean
	 * @param numFilas int
	 * @param pagActual int
	 */
	public void setValues(GenOutBean datos, int numFilas, int pagActual) throws java.sql.SQLException {
		clearRS();
		this.elemVS = new java.util.Properties();
		this.nextElem = 0;
		this.cantidadColumnas = datos.getColumnCount();
		this.nombreColumnas = new String[this.cantidadColumnas + 1];
		this.tiposDatos = new String[this.cantidadColumnas];
		// Se copian los nombres de las columnas originales.
		for (int i = 0; i < this.cantidadColumnas; i++){
			this.nombreColumnas[i + 1] = datos.nombreColumnas[i + 1];
			this.tiposDatos[i] = datos.getTiposDatos()[i];
		}
		int indiceInicial = pagActual * numFilas + 1;
		// Se copian los datos.
		int indice = numFilas * (pagActual + 1);
		if (indice > datos.size())
			numFilas = datos.size() - (numFilas * pagActual);
		Object fila[];
		for (int i = indiceInicial; i < indiceInicial + numFilas; i++) {
			fila = new Object[this.cantidadColumnas + 1];
			for (int j = 0; j < this.cantidadColumnas; j++)
				fila[j + 1] = datos.getObject(i, j + 1);
			this.addElem(fila);
		}
		//	jdbcResultSet.close();
		this.beforeFirst();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02-03-2001 03:32:25 PM)
	 * @return java.lang.String
	 * @param columna int
	 */
	public String sumatoria(int columna) {
		try {
			double total = 0;
			Double cAux = new Double(total);
			String totalChar = "";
			String valchar = "";
			for (int i = 1; i <= this.elemVS.size(); i++) {
				valchar = (getObject(i, columna)).toString();
				total = total + (Double.valueOf(valchar)).doubleValue();
			}
			totalChar = Double.toString(total);
			return totalChar;
		}
		catch (Exception e) {
			return "&nbsp;";
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02-03-2001 03:33:37 PM)
	 * @return java.lang.String
	 * @param columna java.lang.String
	 */
	public String sumatoria(String columna) {
		try {
			double total = 0;
			Double cAux = new Double(total);
			String totalChar = "";
			String valchar = "";
			for (int i = 1; i <= this.elemVS.size(); i++) {
				valchar = (getObject(i, columna)).toString();
				total = total + (Double.valueOf(valchar)).doubleValue();
			}
			totalChar = Double.toString(total);
			return totalChar;
		}
		catch (Exception e) {
			return "&nbsp;" + elemVS.size();
		}
	}
	public void setValues(CallableStatement vals, DBParserParams pars) throws WSException {
		try {
			//Inicio de la clase
			CallableStatement resultados = vals;
			this.elemVS = new java.util.Properties();
			this.nextElem = 0;
			
			//Se obtiene la metadata
			this.cantidadColumnas = pars.salida.size();
			this.nombreColumnas = new String[this.cantidadColumnas + 1];
			
			//Copia el nombre del parametro de retorno
//			java.util.Enumeration enum = pars.salida.keys();
			int offset = pars.entrada.size()+1;
			ConsSQLparams aux2 = null;
			for(int i=0;i<pars.salida.size();i++){
				aux2 = (ConsSQLparams)pars.salida.get(new Integer(offset+i));
				this.nombreColumnas[i+1] = aux2.nomparam;
			}	
			// Se copian los datos
			Object fila[];
			fila = new Object[this.cantidadColumnas + 1];
			java.util.Enumeration enu = pars.salida.keys();
			int indice = 1;
			if(pars.entrada != null)
				indice = pars.entrada.size() + indice;				
			for(int i=0;i<pars.salida.size();i++){
				fila[i+1] = vals.getObject(indice);
				indice++;
			}
			this.addElem(fila);
		}
		catch (Exception e) {
			throw new servicios.generales.WSException("Clase: OutputServiceBean Error: Error al configurar los valores de salida Msg: " + e.getMessage());
		}
	}
	public void setValues(int filasMod) throws java.sql.SQLException{
		clearRS();
		this.elemVS = new java.util.Properties();
		this.nextElem = 0;
		this.cantidadColumnas = 1;
		this.nombreColumnas = new String[this.cantidadColumnas + 1];
		// Se usa eel nombre FilasMod para indicar la columna con el numero de filas modificadas.
		this.nombreColumnas[1] = "FilasMod";
		// Se copian los datos.
		Object fila[];
		
		fila = new Object[this.cantidadColumnas + 1];
		for (int j = 0; j < this.cantidadColumnas; j++)
				fila[j + 1] = new Integer(filasMod);
		this.addElem(fila);

		//	jdbcResultSet.close();
		this.beforeFirst();
	}
	public String getColumnName(int index) throws WSException{
		try{
			return this.nombreColumnas[index];
		}
		catch(Exception e){
			throw (WSException)e;
		}
	}
	/**
	 * Este método se creó en VisualAge.
	 * @return java.lang.String
	 * @param value java.lang.String
	 * 30-08-2003:	Retorna un blanco en caso de que se ingrese un blanco
	 */
	public String formatMto(Object monto, String formato, Locale pais) {
		try {
			if(monto == null)
				return "&nbsp;";
			if(monto.toString().equalsIgnoreCase("&nbsp;"))
				return (String)monto;
			String value = monto.toString();
			java.text.DecimalFormat dec = (java.text.DecimalFormat) java.text.NumberFormat.getInstance(pais);
			java.lang.Double valor = null;
			dec.applyPattern(formato);
			valor = java.lang.Double.valueOf(value);
			return dec.format(valor.doubleValue());
		}
		catch (Exception e) {
			return "&nbsp;";
		}
	}
}
