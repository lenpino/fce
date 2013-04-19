package servicios.basedatos;

import java.util.ArrayList;

/**
 * @autor lpino
 * @Fecha Jul 26, 2003
*/
public class ConsParamsX extends ConsSQLparams {
	public ArrayList valparams;
	public ConsParamsX(String nombre, String tipo){
		super(nombre,tipo);
	}
	public ConsParamsX(){
		super();
	}
}
