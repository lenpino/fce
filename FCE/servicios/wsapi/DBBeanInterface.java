package servicios.wsapi;

/**
 * Insert the type's description here.
 * Creation date: (29-09-2000 10:23:02 AM)
 * @author: Leonardo Pino
 * 23-05-2001:	Se agrega metodo setPool para configurar tipos de pool genericos y soportar version WAS 2.03
 *							Se eliminan los metodos setPassword y setUserID dado que no se necesitan siempre en la nueva
 *							forma de ejecutar.
 * 11-02-2003:	Se agrega el metodo de setParams para configurar los parametros al nivel del bean y asi usar las optimizaciones 
 * 						del PrepareStatement y el otro statement
 * 14-03-2003:	Se elimina el soporte para las clases de IBM com.ibm.db.*
 */
public interface DBBeanInterface {
/**
 * Insert the method's description here.
 * Creation date: (22-11-2000 10:47:12 AM)
 */
void closeConn() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 01:49:46 PM)
 */
void closeResultSet() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 01:17:44 PM)
 */
void closeStmt() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 01:16:41 PM)
 */
void execute() throws servicios.generales.WSException, java.sql.SQLException;
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 01:24:28 PM)
 */
void executeUpdate() throws servicios.generales.WSException, java.sql.SQLException,java.io.IOException;
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 03:45:18 PM)
 * @return java.lang.Object
 */
Object getResult();
/**
 * Insert the method's description here.
 * Creation date: (16-04-2001 10:33:00 AM)
 * @return int
 */
int getResultUpdate();
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 10:44:02 AM)
 * @param valor com.ibm.db2.jdbc.app.stdext.javax.sql.DataSource
 */
void setDataSource(javax.sql.DataSource valor);
/**
 * Insert the method's description here.
 * Creation date: (23-05-2001 01:04:59 PM)
 * @param elPool java.lang.Object
 */
void setPool(Object elPool);
/**
 * Insert the method's description here.
 * Creation date: (29-09-2000 12:52:11 PM)
 * @param value java.lang.String
 */
void setSQLString(String consultaSql);

void setParams(Object params);
}
