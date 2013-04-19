package servicios.wsapi;

/**
 * Insert the type's description here.
 * Creation date: (11/14/00 4:13:39 PM)
 * @author: Leonardo Pino
 */
public interface CommBeanInterface {
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:19:00 PM)
 */
void close() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:16:50 PM)
 * @exception pruebas.MsgException The exception description.
 */
void connect() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:16:50 PM)
 * @exception pruebas.MsgException The exception description.
 */
void connect(java.lang.Object connStruct) throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:19:20 PM)
 */
void disconnect() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:16:13 PM)
 * @exception pruebas.MsgException The exception description.
 */
void init() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:17:16 PM)
 * @exception pruebas.MsgException The exception description.
 */
void open() throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:18:38 PM)
 * @return java.lang.Object
 * @exception pruebas.MsgException The exception description.
 */
Object recvMsg(byte[] par) throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:18:00 PM)
 * @param objData java.lang.Object
 * @exception pruebas.MsgException The exception description.
 */
void sendMsg(Object objData) throws servicios.generales.WSException;
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:20:41 PM)
 * @param paramComm java.lang.Object
 * @param paramTemplateMsg java.lang.Object
 * @exception pruebas.MsgException The exception description.
 */
void setProperties(Object paramComm) throws servicios.generales.WSException;
//Metodos para el manejo de Ids de los mensajes
void setMessageId(byte[] messageId) throws servicios.generales.WSException;
byte[] getMessageId() throws servicios.generales.WSException;
}
