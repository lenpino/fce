package servicios.generales;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Wrapper compatible con tu API original pero usando Log4j2.
 * - Usa LogManager.getLogger(Class)
 * - Reemplaza NDC por ThreadContext.push()/pop() (stack)
 * - Configuración externa vía Configurator.initialize(...)
 *
 * Nota: el archivo de config debe ser log4j2.(xml|properties|json|yaml).
 * Si quieres recarga automática, define "monitorInterval" en el archivo de config.
 */
public class Logger {

    private boolean initialized = false;
    private org.apache.logging.log4j.Logger logger;

    public Logger(Class<?> component) {
        if (!initialized) {
            init();
        }
        this.logger = LogManager.getLogger(component);
    }

    public Logger(Class<?> component, String path) throws WSException {
        if (!initialized) {
            init(path);
        }
        this.logger = LogManager.getLogger(component);
    }

    /* ------------ Métodos de log, conservando firmas con Object ------------ */

    public void debug(Object o) {
        logger.debug("{}", o);
    }

    public void debug(Object o, Throwable t) {
        logger.debug("{}", o, t);
    }

    public void info(Object o) {
        logger.info("{}", o);
    }

    public void info(Object o, Throwable t) {
        logger.info("{}", o, t);
    }

    public void warn(Object o) {
        logger.warn("{}", o);
    }

    public void warn(Object o, Throwable t) {
        logger.warn("{}", o, t);
    }

    public void error(Object o) {
        logger.error("{}", o);
    }

    public void error(Object o, Throwable t) {
        logger.error("{}", o, t);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /* --------------------- Inicialización / Config --------------------- */

    private void init() {
        if (initialized) return;
        // Log4j2 se auto-configura desde el classpath si existe log4j2.xml/.properties/etc.
        initialized = true;
    }

    private void init(String path) throws WSException {
        try {
            if (initialized) return;
            // Acepta rutas tipo "file:/.../log4j2.xml" o ruta de sistema "C:/.../log4j2.xml"
            // También funciona con ".properties", ".json", ".yaml"
            Configurator.initialize("app-logging", path);
            initialized = true;
        } catch (Exception e) {
            throw new WSException("Clase: Logger Error: No se pudo inicializar Log4j2. Msg: " + e.getMessage());
        }
    }

    /* --------------------- Contexto tipo NDC (stack) --------------------- */

    public void logPush(String contexto) {
        ThreadContext.push(contexto);
    }

    public void logPop() {
        ThreadContext.pop();
    }
}
