/*
 * Created on 11-jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package servicios.wsapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servicios.generales.WSPgrmCallException;

/**
 * @author lpino
 *
 * Interface que completa metodos basicos faltantes en la definición de
 * BusinessObjectInterface como metodo de entrada de ejecucion y de salida
  */
public interface FceObjectInterface extends BusinessObjectInterface {
	//Metodo para poder configurar parametros basicos de la clase
	public void setParameters(Object parametros);
	//Metodo para ejecutar la clase (main)
	public void execute() throws servicios.generales.WSException, WSPgrmCallException;
	//Metodo que retorna algun resultado desde la interface
	public Object getObjectResult();
	//Metodo para configurar la inicializacion de la clase en base a el request y el response
	public void init(HttpServletRequest req, HttpServletResponse res);
}
