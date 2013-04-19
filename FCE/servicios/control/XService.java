package servicios.control;

public class XService extends Service {

	@Override
	public void setServicio(String newServicio) {
		if(newServicio.equalsIgnoreCase("db"))
			servicio = "basedatos.CtrlServiceDB";
		else if(newServicio.equalsIgnoreCase("etc"))
			servicio = "generales.CtrlServiceEtc";
		else if(newServicio.equalsIgnoreCase("msg"))
			servicio = "mensajeria.CtrlServiceMsg";
		else if(newServicio.equalsIgnoreCase("mq"))
			servicio = "mqseries.CtrlServiceMQ";
		else
			servicio="ERROR";
	}
	public void setServicioCtrl(String claseControladora){
		servicio = claseControladora;
	}
}
