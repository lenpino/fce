package servicios.generales;

/**
 * @(#)ResultSet.java	5.6a 2000/01/04
 * @author: Iván Pérez
 * @Modificado: Leonardo Pino
 * 05-01-2001:	Cambio de nombre y reubicacion dentro del IBMWS
 *			
 */
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

import org.w3c.dom.Document;
public class GenOutBean implements Serializable{
	// Filas.
	protected Properties elemVS;
	// Cursor.
	protected int nextElem;

	// ** Metadata **
	protected String nombreColumnas[];
	protected int cantidadColumnas;
	
	//Clase para utilizar la API DOM de XML
	protected XmlHelper genXml;
	
	//La matriz traspuesta
	protected Properties elemVST;
	
	//Lista de los nombres de clases asociados a cada columna
	protected String[] tiposDatos;
/**
 * ResultVS constructor comment.
 */
public GenOutBean() {
	elemVS = new java.util.Properties();
	nextElem = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 5:39:37 p.m.)
 * @param jdbcResultSet java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
public GenOutBean(java.sql.ResultSet jdbcResultSet) throws java.sql.SQLException {
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

	jdbcResultSet.close();
	this.beforeFirst();
}
/**
 * ResultVS constructor comment.
 */
public GenOutBean(java.util.Properties defProp) {
	elemVS = defProp;
	nextElem = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 5:40:33 p.m.)
 * @param elemVS linea800.ResultSet
 */
public GenOutBean(GenOutBean defProp) {
	elemVS = defProp.elemVS;
	this.nombreColumnas = defProp.nombreColumnas;
	this.cantidadColumnas = defProp.cantidadColumnas;
	this.tiposDatos = defProp.tiposDatos;
	nextElem = 0;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param num int
 */
private int absVal(int num) {
	return  (num >= 0 ? num : -1 * num);
}
/**
 * This method was created in VisualAge.
 * @param key java.lang.String
 * @param vtaMonto java.lang.Double
 * @param vtaNum int
 * @param stockMonto java.lang.Double
 * @param stockNum int
 */
protected void addElem(Object col[]) throws java.sql.SQLException {
	Integer keyAux;

	if (elemVS == null)
		throw new java.sql.SQLException();
	
	keyAux	= new Integer(elemVS.size() + 1);	
	elemVS.put(keyAux, col);
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public void beforeFirst() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		nextElem = 0;
	}
}
/**
 * This method was created in VisualAge.
 */
public void close() throws java.sql.SQLException {
	if (elemVS != null)
		elemVS.clear();
	else
		throw new java.sql.SQLException();
}
/**
 * This method was created in VisualAge.
 */
public void fillTestData(int elems) throws java.sql.SQLException {
//	Object obj[] = {new Double(0), new Integer(0), new Double(0), new Integer(0)};
	java.util.Random randNum;

	if (elemVS == null)
		throw new java.sql.SQLException();

	randNum = new Random();
	Object obj[] = new Object[4];
	
	for (int Cont=0; Cont < elems; Cont++)
	{
		obj[0] = "" + Cont;
		obj[1] = new Integer(absVal(randNum.nextInt())/100000000);
		obj[2] = new Double(absVal(randNum.nextInt())/1000);
		obj[3] = new Integer(absVal(randNum.nextInt())/100000000);
		addElem(obj);
	}
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public boolean first() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		nextElem = 1;
		return true;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10-11-2000 04:43:37 PM)
 * Autor: Leonardo Pino
 * @return java.lang.Object
 * @param objeto java.lang.Object
 * @param formato java.lang.String
 */
private Object format(Object objeto, String formato) {
	if (objeto instanceof java.sql.Date) {
		java.util.Date laFecha = (java.util.Date) objeto;
		String fechaModifica;
		DateFormat formatFecha = new SimpleDateFormat(formato);
		fechaModifica = formatFecha.format(laFecha);
		return fechaModifica;
	}
	else
		if (objeto instanceof Double || objeto instanceof Float || objeto instanceof java.math.BigDecimal) {
			java.util.Locale pais = java.util.Locale.GERMANY;
			java.text.DecimalFormat num = (java.text.DecimalFormat)java.text.NumberFormat.getInstance(pais);
			if (objeto instanceof Double) {
				java.lang.Double valor = (Double) objeto;
				num.applyPattern(formato);
				return num.format(valor.doubleValue());
			}
			else if(objeto instanceof Float){
				java.lang.Float valor = (Float) objeto;
				num.applyPattern(formato);
				return num.format(valor.floatValue());
			}
			else{
				java.math.BigDecimal big = (java.math.BigDecimal)objeto;
				num.applyPattern(formato);
				return num.format(big.doubleValue());
			}
		}
	return null;
}
public int getColumnCount() {
	return this.cantidadColumnas;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 6:03:20 p.m.)
 * @return int
 * @param columnName java.lang.String
 */
protected int getColumnNumber(String columnName) {
	int colNumber = 0;
	for (int i = 1; i <= this.cantidadColumnas; i++)
		if (columnName.equalsIgnoreCase(this.nombreColumnas[i]))
			colNumber = i;
	return colNumber;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param column int
 */
public Object getObject(int column) throws java.sql.SQLException {
 	Object cols[];

 	if (elemVS == null)
 		throw new java.sql.SQLException();

 	if ( nextElem > 0 ) {
	 	cols = (Object [])elemVS.get(new Integer(nextElem));
	 	if ( cols == null )
	 	 	return null;
		return cols[column];
 	}
 	else
 		return null;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param column int
 */
public Object getObject(int row, int column) throws java.sql.SQLException, ArrayIndexOutOfBoundsException {
 	Object cols[];

 	if (elemVS == null)
 		throw new java.sql.SQLException();

 	if (row > elemVS.size())
 		throw new ArrayIndexOutOfBoundsException();

 	if (row > 0) {
	 	cols = (Object [])elemVS.get(new Integer(row));
	 	if (cols == null)
	 	 	return null;

		return cols[column];
 	}
 	else
 		return null;
}
/**
 * 
 * @param row
 * @param columnName
 * @param dato
 * @throws java.sql.SQLException
 * @author Cristian Fabres
 */
public void setObject(int row, String columnName, Object dato) throws java.sql.SQLException {
	int colNumber = getColumnNumber(columnName);
	this.setObject(row, colNumber, dato);
}
/**
 * 
 * @param row
 * @param column
 * @param dato
 * @throws java.sql.SQLException
 * @throws ArrayIndexOutOfBoundsException
 * @author Cristian Fabres
 * 
 */
public void setObject(int row, int column, Object dato) throws java.sql.SQLException, ArrayIndexOutOfBoundsException {
	Object cols[];

	if (elemVS == null)
		throw new java.sql.SQLException();
	if (row > elemVS.size())
		throw new ArrayIndexOutOfBoundsException();
	if (row > 0) {
		cols = (Object [])elemVS.get(new Integer(row));
		cols[column] = dato;
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param column int
 */
public Object getObject(int row, int column, String formato) throws java.sql.SQLException, ArrayIndexOutOfBoundsException {
 	Object cols[];

 	if (elemVS == null)
 		throw new java.sql.SQLException();
 	if (row > elemVS.size())
 		throw new ArrayIndexOutOfBoundsException();
 	if (row > 0) {
	 	cols = (Object [])elemVS.get(new Integer(row));
	 	if (cols == null)
	 	 	return null;
		return format(cols[column], formato);
 	}
 	else
 		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 6:20:59 p.m.)
 * @return java.lang.Object
 * @param row int
 * @param column java.lang.String
 * @exception java.sql.SQLException The exception description.
 */
public Object getObject(int row, String columnName) throws java.sql.SQLException {
	int colNumber = getColumnNumber(columnName);

	if (colNumber > 0)
		return this.getObject(row, colNumber);
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 6:17:55 p.m.)
 * @return java.lang.Object
 * @param columnName java.lang.String
 */
public Object getObject(String columnName) throws java.sql.SQLException {
	int colNumber = getColumnNumber(columnName);

	if (colNumber > 0)
		return this.getObject(colNumber);
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (05-04-2000 11:15:32 a.m.)
 * @return java.lang.Object[]
 */
public Object[] getRowArray(int fila) throws java.sql.SQLException, java.lang.ArrayIndexOutOfBoundsException {
 	Object cols[];

 	if (elemVS == null)
 		throw new java.sql.SQLException();

 	if (fila <= elemVS.size())
		return (Object [])elemVS.get(new Integer(fila));
	else
		throw new java.lang.ArrayIndexOutOfBoundsException("[Row req: " + fila + "; RS Size: " + elemVS.size() + "]");
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public boolean isFirst() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		if (nextElem == 0 || nextElem == 1)
			return true;
		else
			return false;
	}
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public boolean isLast() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		if (nextElem == elemVS.size())
			return true;
		else
			return false;
	}
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public boolean last() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		nextElem = elemVS.size();
		return true;
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean next() throws ArrayIndexOutOfBoundsException, java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	try {
		return (++nextElem <= elemVS.size());
	}
	catch (Exception e) {
		throw new ArrayIndexOutOfBoundsException();
	}
}
/**
 * Este método fue creado en VisualAge.
 * @param pos int
 */
public void relative(int pos) {
	if (elemVS != null)
		nextElem = pos;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int size() throws java.sql.SQLException {
	if (elemVS != null)
		return elemVS.size();
	else
		throw new java.sql.SQLException();
}
/**
 * 
 * @return boolean
 * @param column int
 */
public void sortDByColumn(int column) {
	int newCont = 0;			// Contador para la Key del nuevo objeto Properties.
	boolean inicioCiclo;		// Switch para el primer valor del elemento mayor.

	Integer aux = null;			// Objeto para conversiones.
	Integer aReg;				// Objeto con la Key actual.
	Integer hReg = null;		// Objeto con la Key del registro con la mayor columna.

	Double auxd = null;			// Objeto para conversiones.
	double elem1;				// Variable para comparación.
	double elem2;				// Variable para comparación.
	Object tuplaReg[];			// Objeto para almacenamiento temporal.

								// Nuevo objeto Properties para contener e anterior pero ordenado.
	Properties tempVS = new Properties();

	// Mientras tenga registros el objeto original, elemVS, se entra al ciclo.
	while (elemVS.size() > 0) {
		inicioCiclo = true;

		// Ciclo for para recorrer todos los elementos de la lista de Keys.
		for (Enumeration e = elemVS.keys() ; e.hasMoreElements() ;) {

			// Se copia la siguiente Key disponible a aReg convertida a entero.
			aReg = (Integer)e.nextElement();

			// Se saca el registro relacionado a aReg convertido a arreglo de Object.
			tuplaReg = (Object [])elemVS.get(aReg);

			// Se copia el elemento de la columna especificada en la llamada
			// al método, a elem1, para su posterior comparación.
			elem1 = Double.valueOf(tuplaReg[column].toString()).doubleValue();

			// Si es la primera entrada al ciclo...
			if (inicioCiclo) {
				// ... se copia el valor de aReg en hReg.
				hReg = new Integer(aReg.intValue());
				inicioCiclo = false;
			}

			// Se saca el registro relacionado a hReg convertido en arreglo de Object.
			tuplaReg = (Object [])elemVS.get(hReg);

			// Se copia el elemento de la columna especificada en la llamada
			// al método, a elem2, para su posterior comparación.
			elem2 = Double.valueOf(tuplaReg[column].toString()).doubleValue();

			// Si elem1 es mayor que elem2...
			if (elem1 > elem2)
				// ... se copia el valor de aReg a hReg.
				hReg = new Integer(aReg.intValue());
		}

		// Al terminar el ciclo se tiene la Key del registro, en hReg, cuya columna es
		// la mayor de todas, por lo que se agrega a tempVS una nueva clave correlativa,
		// y como elemento el registro relacionado a hReg.
		tempVS.put(Integer.valueOf(++newCont + ""), elemVS.get(hReg));

		// Además, se remueve de elemVS el registro asociado a hReg.
		elemVS.remove(hReg);
	}

	// Se reinicializa a 0 el contador para el next().
	nextElem = 0;
	// Se hace que elemVS referencie a tempVS.
	elemVS = tempVS;
}


public void sortDByColumn(int column,boolean asc) {
	int newCont = 0;			// Contador para la Key del nuevo objeto Properties.
	boolean inicioCiclo;		// Switch para el primer valor del elemento mayor.

	Integer aux = null;			// Objeto para conversiones.
	Integer aReg;				// Objeto con la Key actual.
	Integer hReg = null;		// Objeto con la Key del registro con la mayor columna.

	Double auxd = null;			// Objeto para conversiones.
	String elem1;				// Variable para comparación.
	String elem2;				// Variable para comparación.
	Object tuplaReg[];			// Objeto para almacenamiento temporal.

								// Nuevo objeto Properties para contener e anterior pero ordenado.
	Properties tempVS = new Properties();

	// Mientras tenga registros el objeto original, elemVS, se entra al ciclo.
	while (elemVS.size() > 0) {
		inicioCiclo = true;

		// Ciclo for para recorrer todos los elementos de la lista de Keys.
		for (Enumeration e = elemVS.keys() ; e.hasMoreElements() ;) {

			// Se copia la siguiente Key disponible a aReg convertida a entero.
			aReg = (Integer)e.nextElement();

			// Se saca el registro relacionado a aReg convertido a arreglo de Object.
			tuplaReg = (Object [])elemVS.get(aReg);

			// Se copia el elemento de la columna especificada en la llamada
			// al método, a elem1, para su posterior comparación.
			elem1 = tuplaReg[column].toString();

			// Si es la primera entrada al ciclo...
			if (inicioCiclo) {
				// ... se copia el valor de aReg en hReg.
				hReg = new Integer(aReg.intValue());
				inicioCiclo = false;
			}

			// Se saca el registro relacionado a hReg convertido en arreglo de Object.
			tuplaReg = (Object [])elemVS.get(hReg);

			// Se copia el elemento de la columna especificada en la llamada
			// al método, a elem2, para su posterior comparación.
			elem2 = tuplaReg[column].toString();

			if (asc) {
				if (elem1.compareTo(elem2) < 0) //Si elem1 es menor que elem2...
					hReg = new Integer(aReg.intValue());// ... se copia el valor de aReg a hReg.
			}
			else {
				if (elem1.compareTo(elem2) > 0) // Si elem1 es mayor que elem2...
					hReg = new Integer(aReg.intValue()); // ... se copia el valor de aReg a hReg.
			}				
		}

		// Al terminar el ciclo se tiene la Key del registro, en hReg, cuya columna es
		// la mayor de todas, por lo que se agrega a tempVS una nueva clave correlativa,
		// y como elemento el registro relacionado a hReg.
		tempVS.put(Integer.valueOf(++newCont + ""), elemVS.get(hReg));

		// Además, se remueve de elemVS el registro asociado a hReg.
		elemVS.remove(hReg);
	}

	// Se reinicializa a 0 el contador para el next().
	nextElem = 0;
	// Se hace que elemVS referencie a tempVS.
	elemVS = tempVS;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString(int pos) throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	return getObject(pos).toString();
}
/**
 * Insert the method's description here.
 * Creation date: (16/8/00 11:23:57 a.m.)
 * @param columna_condicion int
 * @param igual_a java.lang.Object
 * @param columna_destino int
 * @param queda java.lang.Object
 */
public void update(int columna_condicion, Object igual_a, int columna_destino, Object queda) {
	// Se respalda el cursor actual.
	int posFila = 0;
	Object[] fila = null;

	// Se busca la clave indicada.
	while (++posFila <= elemVS.size()) {
		fila = (Object[])elemVS.get(new Integer(posFila));

		// ¿Se cumple la condición?.
		if (fila[columna_condicion].toString().trim().equalsIgnoreCase(igual_a.toString().trim())) {
//JohnsonUtils.OutDebug.debug("ResultVS.update(): Se encontró un reg. con la col. " + columna_condicion + "=" + igual_a + ", y la col. " + columna_destino + "=" + fila[columna_destino].toString());
			fila[columna_destino] = queda;
			elemVS.put(new Integer(posFila), fila);
//JohnsonUtils.OutDebug.debug("ResultVS.update(): Se actualizó la col. " + columna_destino + " a " + queda);
		}
//		else
//JohnsonUtils.OutDebug.debug("ResultVS.update(): El reg. con la col. " + columna_condicion + " tiene el valor " + fila[columna_condicion].toString() + " distinto a " + igual_a); 
	}
	
}
/**
 * Este método fue creado en VisualAge.
 * @return boolean
 */
public boolean wasNull() throws java.sql.SQLException {
	if (elemVS == null)
		throw new java.sql.SQLException();
	else {
		if (nextElem > elemVS.size())
			return true;
		else
			return false;
	}
}

	private void trasponer() throws ArrayIndexOutOfBoundsException, SQLException {
		elemVST = new Properties();
		for (int i = 0; i < cantidadColumnas; i++) {
			Object[] columna = new Object[elemVS.size()];
			for (int j = 0; j < elemVS.size(); j++) {
				columna[j] = getObject(j + 1, i + 1);
			}
			elemVST.put(nombreColumnas[i + 1], columna);
		}
	}
	
	public Document exportXml() throws ArrayIndexOutOfBoundsException, SQLException{
		this.genXml = new XmlHelper();
		trasponer();
		genXml.creaRaizBean();
		for(int i=0; i < cantidadColumnas; i++){
			genXml.insertaDatosColumna(genXml.creaColumna(nombreColumnas[i+1], tiposDatos[i]), (Object[])elemVST.get(nombreColumnas[i+1]));
		}
		//System.out.println(genXml.printXML());
		return genXml.getDoc();
	}
	
	public void addData(GenOutBean datos) throws ArrayIndexOutOfBoundsException, SQLException, WSException{
		if(datos.getColumnCount() != this.getColumnCount())
			throw new WSException("Clase: GenOutBean - Error: No concuerda el numero de columnas - MSG: No se puede agregar los datos");
			Integer keyAux;
			for(int i=0;i<datos.size();i++){
				keyAux	= new Integer(elemVS.size() + 1);
				this.addElem(datos.getRowArray(i+1));
			}
	}
	public void importXml(Document xml){
		this.genXml = new XmlHelper();
		genXml.setDoc(xml);
	}
	public Document exportXmlShort() throws ArrayIndexOutOfBoundsException, SQLException{
		this.genXml = new XmlHelper();
		genXml.creaRaizBean();
		genXml.setNombreCols(this.nombreColumnas);
		for(int i=0;i<elemVS.size();i++){
			genXml.creaRegistro(getRowArray(i+1));
		}
		//System.out.println(genXml.printXML());
		return genXml.getDoc();
	}
	
	public GenOutBean(Document xml) throws SQLException{
		this.genXml = new XmlHelper();
		genXml.setDoc(xml);
		this.elemVS = new Properties();
		this.nextElem = 0;

		// Se obtiene la MetaData del RS original.
		this.nombreColumnas = genXml.retornaNomColumnas();;
		this.cantidadColumnas = this.nombreColumnas.length;
		this.tiposDatos = genXml.getTiposDatos();

		//Crea el numero de filas necesarios
		Object fila[];
		int numeroFilas = genXml.retornaColumna(0).length;
		for(int i=0; i< numeroFilas;i++){
			fila = new Object[this.cantidadColumnas + 1];
			this.addElem(fila);
		}
		Object[] aux;
//		Object xua = null;
//		String tipo;
		// Se copian los nombres de las columnas originales
		for (int i = 0; i < this.cantidadColumnas-1; i++){
			//Por cada columna se copian sus datos en las posiciones de respectivas de todas las filas
			aux = genXml.retornaColumna(i);
			for(int j=0;j<numeroFilas;j++){
				//Se instancia la clase que corresponda
				//Falta implemetar los contructores correctos para DATE y TIMESTAMP
//				if(tipo.equalsIgnoreCase("java.lang.String"))
//					xua = aux[j];
//				else if(tipo.equalsIgnoreCase("java.math.BigDecimal"))
//					xua = new BigDecimal((String)aux[j]);
//				else if(tipo.equalsIgnoreCase("java.lang.Integer"))
//					xua =new Integer((String)aux[j]);
//				else if(tipo.equalsIgnoreCase("java.sql.Timestamp"))
//					xua = aux[j];
//				else if(tipo.equalsIgnoreCase("java.lang.Boolean"))
//					xua = new Boolean((String)aux[j]);
//				else if(tipo.equalsIgnoreCase("java.sql.Date"))
//					xua = new Boolean((String)aux[j]);
//				else
//					System.out.println("Tipo no controlado por el sistema TIPO = " + tipo);
				((Object[])elemVS.get(new Integer(i+1)))[i+1] = aux[j];
			}
		}
		this.beforeFirst();		
	}
	public void setData(Properties data){
		elemVS = data;
		nextElem = 0;
	}
	/**
	 * @return
	 */
	public String[] getNombreColumnas() {
		return nombreColumnas;
	}

	/**
	 * @return
	 */
	public String[] getTiposDatos() {
		return tiposDatos;
	}

	/**
	 * @param strings
	 */
	public void setNombreColumnas(String[] strings) {
		nombreColumnas = strings;
	}

	/**
	 * @param strings
	 */
	public void setTiposDatos(String[] strings) {
		tiposDatos = strings;
	}
	public void setColumnNumber(int numero){
		this.cantidadColumnas = numero;
	}
}
