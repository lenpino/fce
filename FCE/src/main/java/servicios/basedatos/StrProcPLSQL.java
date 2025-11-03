package servicios.basedatos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import oracle.jdbc.OracleTypes;
import oracle.sql.CLOB;
import servicios.generales.WSException;

/**
 * @author Leonardo
 * Clase especializada en el manejo de los stores procedures escritos en PLSQL
 * para Oracle. Requiere que en el archivo de configuracion de base de datos de la 
 * aplicacion, se especifique esta clase y ademas que el parametro de retorno con
 * el ResultSet se registre como tipo O en los parametros de los SPs
 *
 */
public class StrProcPLSQL extends StrProcBean30 {
	
	public int indiceRS = 0;
	private boolean isReturnProperties = false;
	Properties elemVS;
	public StrProcPLSQL() {
		super();
	}

	@Override
	public synchronized void execute() throws servicios.generales.WSException, java.sql.SQLException {
		try {
			initialize();
			stmt = conn.prepareCall(this.SQLString);
			regParams(stmt);
			stmt.execute();
			if(indiceRS != 0)
            {
                resultOriginal = (ResultSet)stmt.getObject(indiceRS);
                
                ResultSetMetaData rsmd=resultOriginal.getMetaData();
                for(int i=0;i<rsmd.getColumnCount();i++){
                	if(rsmd.getColumnType(i+1)==OracleTypes.CLOB){
                		isReturnProperties=true;
                		break;
                	}
                }
            }
		}
		catch (java.lang.Exception e) {
			if (stmt != null)
				stmt.close();
			if (resultOriginal != null)
				resultOriginal.close();
			if (conn != null)
				conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: StrProcPLSQL - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}
	/**
	 * Este metodo construye los datos necesarios para el OutPutServiceBean mientras la conexion esta aun abierta hacia la BD
	 * En particular si encuentra CLOB en el retorno lo extrae hacia un String
	 */
	@Override
	public Object getResult() {
		if(isReturnProperties){
			if(elemVS != null)
				return elemVS;
			elemVS = new java.util.Properties();
	//		// Se copian los datos.
			Object fila[];
			Integer keyAux;
			try {
				java.sql.ResultSetMetaData metaData = this.resultOriginal.getMetaData();
				int cantidadColumnas = metaData.getColumnCount();
				
				while (this.resultOriginal.next()) {
					fila = new Object[cantidadColumnas + 1];
					for (int j = 0; j < cantidadColumnas; j++){
						//Para el caso de los CLOB
						if(this.resultOriginal.getObject(j + 1) instanceof oracle.sql.CLOB){
							CLOB obj=(CLOB) this.resultOriginal.getObject(j + 1);
							obj.open(CLOB.MODE_READONLY);
							
				            InputStream in = obj.getAsciiStream();
				            BufferedReader reader=new BufferedReader(new InputStreamReader(in));

				            StringBuffer stbuff=new StringBuffer();
				            String linea=null;
				            while((linea=reader.readLine())!=null){
				            	stbuff.append(linea).append("\n");
				            }
				            
				            reader.close();
					        in.close();
					        obj.close();

					        fila[j + 1] = stbuff.toString();
						}
						else
							fila[j + 1] = this.resultOriginal.getObject(j + 1);
					}
					keyAux	= new Integer(elemVS.size() + 1);	
					elemVS.put(keyAux, fila);
				}
				
				// Se obtiene la MetaData del RS original.
				String[] nombreColumnas = new String[cantidadColumnas + 1];
				String[] tiposDatos = new String[cantidadColumnas];
				// Se copian los nombres de las columnas originales.
				for (int i = 0; i < cantidadColumnas; i++){
					nombreColumnas[i + 1] = metaData.getColumnName(i + 1);
					tiposDatos[i] = metaData.getColumnClassName(i+1);
				}
				elemVS.put("nombre", nombreColumnas);
				elemVS.put("tipo", tiposDatos);
	
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return elemVS;
		}
		else{
			return this.resultOriginal;
		}
	}

	private void regParams(CallableStatement stmt) throws WSException {
		int index = 1;
		try {
			if(this.parametros != null && this.parametros.entrada != null){
				java.util.Enumeration enumeracion = this.parametros.entrada.keys();
				ConsSQLparams aux = null;
				Integer llave = null;
				for (int i = 0; i < this.parametros.entrada.size(); i++) {
					llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsSQLparams)(this.parametros.entrada.get(llave));
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.INTEGER);
							else
								stmt.setInt(index,Integer.parseInt(aux.valparam));
							break;
						}
						case 'T':{ 
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.TIMESTAMP);
							else
								stmt.setTimestamp(index,Timestamp.valueOf(aux.valparam));
							break;
						}
						case 'C':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.VARCHAR);
							else 
								stmt.setString(index,aux.valparam);
							break;
						}
						case 'B':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.BIT);
							else
								stmt.setBoolean(index,(new Boolean(aux.valparam)).booleanValue());
							break;
						}							
						case 'D':{
							stmt.setObject(index,aux.valparam,java.sql.Types.DATE);
							break;
						}
						case 'U':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,java.sql.Types.NUMERIC);
							else{
								BigDecimal bdecimal = new BigDecimal(aux.valparam);
								stmt.setBigDecimal(index,bdecimal);
							}
							break;
						}
						case 'L':{
							if(aux.valparam == null || aux.valparam.equalsIgnoreCase(""))
								stmt.setNull(index,OracleTypes.CLOB);
							else{
								oracle.sql.CLOB data =  oracle.sql.CLOB.createTemporary(conn, true, oracle.sql.CLOB.DURATION_SESSION);
	                            data.open(oracle.sql.CLOB.MODE_READWRITE);
	                            OutputStream out = data.getAsciiOutputStream();
	                            out.write(aux.valparam.getBytes());
	                            out.close();
	                            stmt.setClob(index, data);
	                            //Solo en el caso de retornarse un CLOB se necesita retornar un Properties en vez de un ResultSet
	                            //por la necesidad de mantener la conexion abierta
	                            isReturnProperties = true;
							}
							break;
						}
						default:
							throw new WSException("Clase: StrProcPLSQL Msg: No existe definición (parametro de entrada) en consulta " + getSQLString() + " para el tipo " +aux.typeparam );
					}
				}
			}
			if(this.parametros != null && this.parametros.salida != null){
				java.util.Enumeration enumeracion = this.parametros.salida.keys();
				ConsSQLparams aux = null;
				Integer llave = null;
				for (int i = 0; i < this.parametros.salida.size(); i++) {
					llave = (Integer)enumeracion.nextElement();
					index = llave.intValue();
					aux = (ConsSQLparams)(this.parametros.salida.get(llave));
					this.indiceRS = index;
					switch (aux.typeparam.charAt(0)){
						case 'N':{ 
							stmt.registerOutParameter(index,java.sql.Types.INTEGER);
							break;
						}
						case 'T':{ 
							stmt.registerOutParameter(index,java.sql.Types.TIMESTAMP);
							break;
						}
						case 'C':{ 
							stmt.registerOutParameter(index,java.sql.Types.VARCHAR);
							break;
						}
						case 'D':{
							stmt.registerOutParameter(index,java.sql.Types.DATE);
							break;
						}
						case 'U':{
//							BigDecimal bdecimal = new BigDecimal(aux.valparam);
							stmt.registerOutParameter(index,java.sql.Types.DECIMAL);
							break;
						}
						case 'O':{
							stmt.registerOutParameter(index,OracleTypes.CURSOR);
							break;
						}
						case 'L':{
							stmt.registerOutParameter(index,OracleTypes.CLOB);
							break;
						}
						default:
							throw new WSException("Clase: StrProcPLSQL Msg: No existe definición (parametro de salida) en consulta " + getSQLString() + " para el tipo " +aux.typeparam );
					}
				}
			}
		} 
		catch (Exception e) {
			if (e instanceof WSException)
				throw (WSException)e;
			else
				throw new WSException("Clase: StrProcPLSQL Msg: Error al registrar parametros para consulta: " + getSQLString() + " Error: "+ e.getMessage());
		}
	}
	
	/**
	 * 17-06-2003:	Retorno del numero de filas modificadas en la ejecución
	 */
	@Override
	public void executeUpdate() throws java.sql.SQLException, servicios.generales.WSException {
		try {
			initialize();
			stmt = conn.prepareCall(getSQLString());
			regParams(stmt);
			stmt.executeUpdate();
            if(indiceRS != 0){
                numFilas = stmt.getInt(indiceRS);
            }
            else {
                numFilas = 0;
            }
		}
		catch (java.lang.Exception e) {
			stmt.close();
			conn.close();
			if (e instanceof servicios.generales.WSException)
				throw (servicios.generales.WSException) e;
			else
				throw new servicios.generales.WSException("Clase: StrProcPLSQL - Error al ejecutar la consulta " + getSQLString() + "Mensaje " + e.getMessage());
		}
	}

}
