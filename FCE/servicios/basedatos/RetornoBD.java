package servicios.basedatos;

/**
 * Insert the type's description here.
 * Creation date: (12-09-2000 12:52:40 PM)
 * @author: Leonardo Pino
 */
public class RetornoBD {
	protected servicios.generales.GenOutBean jdbcRS;
/**
 * RetornoBD constructor comment.
 */
public RetornoBD() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12-09-2000 12:53:34 PM)
 * @return servicios.basedatos.ResultSet
 */
public servicios.generales.GenOutBean getJdbcRS() {
	return jdbcRS;
}
/**
 * Insert the method's description here.
 * Creation date: (12-09-2000 12:53:34 PM)
 * @param newJdbcRS servicios.basedatos.ResultSet
 */
public void setJdbcRS(servicios.generales.GenOutBean newJdbcRS) {
	jdbcRS = newJdbcRS;
}
}
