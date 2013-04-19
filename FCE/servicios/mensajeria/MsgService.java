package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (25-10-2000 02:03:10 PM)
 * @author: Leonardo Pino
 */
public class MsgService {
	protected TemplateMessage plantillaMsg;
	protected java.lang.String templateId;
/**
 * MsgService constructor comment.
 */
public MsgService() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:10:24 PM)
 * @return servicios.mensajeria.TemplateMessage
 */
public TemplateMessage getPlantillaMsg() {
	return plantillaMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:21:35 PM)
 * @return java.lang.String
 */
public java.lang.String getTemplateId() {
	return templateId;
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:12:08 PM)
 * @return servicios.mensajeria.TemplateMessage
 */
public TemplateMessage getTemplateMsg() {
	return getPlantillaMsg();
}
/**
 * Insert the method's description here.
 * Creation date: (25-10-2000 02:10:24 PM)
 * @param newPlantillaMsg servicios.mensajeria.TemplateMessage
 */
public void setPlantillaMsg(TemplateMessage newPlantillaMsg) {
	plantillaMsg = newPlantillaMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (26-10-2000 12:21:35 PM)
 * @param newTemplateId java.lang.String
 */
public void setTemplateId(java.lang.String newTemplateId) {
	templateId = newTemplateId;
}
}
