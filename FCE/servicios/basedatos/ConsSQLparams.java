package servicios.basedatos;

/**
 * 12-02-2003:	Se agrega el campo para definir el largo maximo del parametro
 * 27-10-2003:	Nuevos constructores
 * 19-08-2004:	Valor default de lenparam = 0
 * */

public class ConsSQLparams {
	public String nomparam;
	public String valparam;
	public String typeparam;
	public int lenparam =0;
	
	public ConsSQLparams(){
		super();
	}
	
	public ConsSQLparams(String nombre, String tipo){
		super();
		nomparam = nombre;
		typeparam = tipo;
	}
}
