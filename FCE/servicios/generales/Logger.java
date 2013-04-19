package servicios.generales;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;
import org.apache.log4j.PropertyConfigurator;
//04/01/2003:	Se crea un nuevo constructor y un nuevo init para que se entregue en forma
//						dinamica el path del archivo de propiedades
public class Logger {
	private boolean initialized = false;
	private Category category = null;
	public Logger(Class component) {
		super();
		if (!initialized) {
			init();
		}
		category = Category.getInstance(component);
	}
	public Logger(Class component, String path) throws WSException {
		super();
		if (!initialized) {
			init(path);
		}
		category = Category.getInstance(component);
	}
	public void debug(Object o) {
		category.debug(o);
	}
	public void debug(Object o, Throwable t) {
		category.debug(o, t);
	}
	public void error(Object o) {
		category.error(o);
	}
	public void error(Object o, Throwable t) {
		category.error(o, t);
	}
	public void info(Object o) {
		category.info(o);
	}
	public void info(Object o, Throwable t) {
		category.info(o, t);
	}
	private void init() {
		if (initialized) {
			return;
		}
		initialized = true;
	}
	private void init(String path) throws WSException {
		try {
			if (initialized) {
				return;
			}
//			PropertyConfigurator.configureAndWatch(path, 60000);
			PropertyConfigurator.configure(path);
			initialized = true;
		}
		catch (Exception e) {
			throw new WSException("Clase: Logger Error: Error al inicializar el sistema de trace y log Msg: " + e.getMessage());
		}
	}
	public boolean isDebugEnabled() {
		return category.isDebugEnabled();
	}
	public boolean isInfoEnabled() {
		return category.isInfoEnabled();
	}
	public void warn(Object o) {
		category.warn(o);
	}
	public void warn(Object o, Throwable t) {
		category.warn(o, t);
	}
	public void logPush(String contexto){
		NDC.push(contexto);
	}
	public void logPop(){
		NDC.pop();
	}
}
