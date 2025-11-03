package servicios.mqseries;

import java.util.Properties;
import java.util.Vector;

import servicios.generales.WSException;

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
/**
 * Insert the type's description here.
 * Creation date: (10/20/00 6:35:58 PM)
 * @author: Rodrigo Soto
 * Modified: Leonardo Pino
 */
public class CommBeanMQ implements servicios.wsapi.CommBeanInterface{
	private ResultadoMsg mResult = null;
	private java.lang.String strData;
	private com.ibm.mq.MQQueue QMsg = null;
	private int optsOpen = 0;
	private com.ibm.mq.MQGetMessageOptions optsGet;
	private com.ibm.mq.MQPutMessageOptions optsPut;
	private com.ibm.mq.MQMessage MsgData;
	public java.util.Properties listAttribMQ = null;
	private java.lang.String QNameMsg = "";
	private com.ibm.mq.MQQueueManager QMngr = null;
	private byte[] messageID = null;
	private ConectorMQ conexion = null;
/**
 * MsgMQSend constructor comment.
 */
public CommBeanMQ() {
	
	try {
		init();
		initAttribFromMQ();
	}
	catch (Exception e){
		System.out.println("Error: init() -> " + e);
	}
}
/**
 * close method comment.
 */
@Override
public void close() throws servicios.generales.WSException{
	try {
		closeQueue();
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al cerrar la cola, " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:07:23 PM)
 * @exception servicios.generales.WSException The exception description.
 */
private void closeQMngr() throws servicios.generales.WSException {
	// No hace nada, pues se supone que el QueueManager es de uso PUBLIC
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:07:23 PM)
 * @exception servicios.generales.WSException The exception description.
 */
public void closeQMngr(boolean killQMngr) throws servicios.generales.WSException {
			
	try {
		if (QMngr != null && killQMngr == true) {
			QMngr.disconnect();
			QMngr = null;
		}
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error al cerrar la Queue" + e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:06:40 PM)
 * @exception servicios.generales.WSException The exception description.
 */
private void closeQueue() throws servicios.generales.WSException {

	try {
		if (QMsg != null) {
			QMsg.close();
			QMsg = null;
		}
		
		MsgData = null;
		optsPut = null;
		optsGet = null;
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error al cerrar la Queue" + e);
	}
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (01-12-2000 12:16:05)
 */
public void commit() {
	try {
		QMngr.commit();
	}
	catch(Exception e) {}
}
/**
 * connect method comment.
 */
@Override
public void connect() throws servicios.generales.WSException {
	try {
		if (getQMngr() == null) 
			throw new servicios.generales.WSException("Error NO se ha definido el QueueManager ");
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error al abrir el QueueManager: " + e);
	}
}
/**
 * connect method comment.
 */
@Override
public void connect(java.lang.Object connStruct) throws servicios.generales.WSException {
	if (connStruct != null) {
		try {
			this.conexion = (ConectorMQ) connStruct;
			this.conexion.init();
			connectQMngr(conexion.getQMgr());
		}
		catch (MQException e) {
			throw new servicios.generales.WSException("Clase: CommBeanMQ - ERROR: Error MQ al conectarse al Queue Manager - MSG:" + e.getMessage());
		}
		catch (WSException e) {
			throw e;
		}
	}
	else
		connect();
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:22:45 PM)
 * @param QMngrIn com.ibm.mq.MQQueueManager
 * @exception servicios.generales.WSException The exception description.
 */
private void connectQMngr(com.ibm.mq.MQQueueManager QMngrIn) throws servicios.generales.WSException {

	if (QMngrIn == null)
		throw new servicios.generales.WSException("Error, el QManager de parámetro NO es válido.");
		
	QMngr = QMngrIn;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:22:45 PM)
 * @param nameQMngr java.lang.String
 * @exception servicios.generales.WSException The exception description.
 */
public void connectQMngr(String nameQMngr) throws servicios.generales.WSException {

	try {
		QMngr = new com.ibm.mq.MQQueueManager(nameQMngr);
		return;
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error, NO se pudo Abrir QManager: " + e);
	}
}
/**
 * disconnect method comment.
 */
@Override
public void disconnect() throws servicios.generales.WSException {
	try {
		QMngr.disconnect();
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error " + e.getMessage() + " al cerrar la cola");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 10:34:00 PM)
 * @return java.lang.String
 * @param name java.lang.String
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public String getAttrib(String name) throws servicios.generales.WSException {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:01:37 PM)
 * @exception servicios.generales.WSException The exception description.
 */
private String getMsg() throws servicios.generales.WSException {
	return getMsg(optsGet);
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:01:37 PM)
 * @param getOpts com.ibm.mq.MQGetMessageOptions
 * @exception servicios.generales.WSException The exception description.
 */
private String getMsg(MQGetMessageOptions getOpts) throws servicios.generales.WSException {

	String strData=null;
	
	if (getOpts == null)
		throw new servicios.generales.WSException("Error, las opciones de Get NO son válidas");
		
	try {
		MsgData = null;
		MsgData = new com.ibm.mq.MQMessage();
		MsgData.format =MQC.MQFMT_STRING;
		QMsg.get(MsgData, getOpts);
		byte[] bData = new byte[MsgData.getMessageLength()];
		MsgData.readFully(bData, 0, MsgData.getMessageLength());
		strData = new String(bData);
		return(strData);
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error: getMsg() -> " + e);
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:01:37 PM)
 * @param msgIn com.ibm.mq.MQMessage
 * @param getOpts com.ibm.mq.MQGetMessageOptions
 * @exception servicios.generales.WSException The exception description.
 */
private com.ibm.mq.MQMessage getMsg(MQMessage msgIn, MQGetMessageOptions getOpts) throws servicios.generales.WSException {
	
	com.ibm.mq.MQMessage	msgOut;

	if (msgIn == null)
		throw new servicios.generales.WSException("Error, el campo MQMessage no es válido.");
	if (getOpts == null)
		throw new servicios.generales.WSException("Error, las opciones de Get NO son válidas");
		
	try {
		msgOut = new com.ibm.mq.MQMessage();
		msgOut.format =MQC.MQFMT_STRING;
		QMsg.get(msgOut, getOpts);
		return(msgOut);
		
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error: putMsg() -> " + e);
	}
		
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 02:14:17)
 * @return com.ibm.mq.MQGetMessageOptions
 */
private MQGetMessageOptions getOptsGet() {
	return optsGet;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (17-11-2000 02:08:52)
 * @return com.ibm.mq.MQPutMessageOptions
 */
private MQPutMessageOptions getOptsPut() {
	return optsPut;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:50:05)
 * @return com.ibm.mq.MQQueueManager
 */
public com.ibm.mq.MQQueueManager getQMngr() {
	return QMngr;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:35:52)
 * @return java.lang.String
 */
public java.lang.String getQNameMsg() {
	return QNameMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 3:53:07 PM)
 * @return int
 * @param name java.lang.String
 */
public static int getValueCteMQ(String name) {

	Class c;
	
	try {
		c = Class.forName("com.ibm.mq.MQC");
	}
	catch(Exception e) {return 0;}

	java.lang.reflect.Field fields[] = c.getDeclaredFields();

	try {
		for (int i=0; i<fields.length; i++) {
			if (fields[i].getName().equalsIgnoreCase(name)) 
				return( fields[i].getInt(null) );
		}
	}
	catch(Exception e) {return 0;}
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:13:37 PM)
 * @exception servicios.generales.WSException The exception description.
 */
@Override
public void init() throws servicios.generales.WSException {
	try {
		MsgData = new com.ibm.mq.MQMessage();
//		optsOpen = com.ibm.mq.MQC.MQOO_INPUT_AS_Q_DEF | com.ibm.mq.MQC.MQOO_OUTPUT;
		optsOpen = 0;
		optsGet = new com.ibm.mq.MQGetMessageOptions();
		optsPut = new com.ibm.mq.MQPutMessageOptions();
		mResult = new ResultadoMsg();
		strData = "";
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al init(): " + e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 11:15:53 PM)
 */
private void initAttribFromMQ() {
	Class c;
	Vector vMQMsg = new Vector();
	Vector vMQPut = new Vector();
	Vector vMQGet = new Vector();
	listAttribMQ = new Properties();
	try {
		c = Class.forName("com.ibm.mq.MQMD");
	}
	catch (Exception e) {
		return;
	}
	java.lang.reflect.Field fields[] = c.getDeclaredFields();
	try {
		for (int i = 0; i < fields.length; i++) {
			vMQMsg.addElement(fields[i].getName());
		}
	}
	catch (Exception e) {
		return;
	}
	try {
		c = Class.forName("com.ibm.mq.MQMessage");
	}
	catch (Exception e) {
		return;
	}
	fields = c.getDeclaredFields();
	try {
		for (int i = 0; i < fields.length; i++) {
			vMQMsg.addElement(fields[i].getName());
		}
	}
	catch (Exception e) {
		return;
	}
	try {
		c = Class.forName("com.ibm.mq.MQPutMessageOptions");
	}
	catch (Exception e) {
		return;
	}
	fields = c.getDeclaredFields();
	try {
		for (int i = 0; i < fields.length; i++) {
			vMQPut.addElement(fields[i].getName());
		}
	}
	catch (Exception e) {
		return;
	}
	try {
		c = Class.forName("com.ibm.mq.MQGetMessageOptions");
	}
	catch (Exception e) {
		return;
	}
	fields = c.getDeclaredFields();
	try {
		for (int i = 0; i < fields.length; i++) {
			vMQGet.addElement(fields[i].getName());
		}
	}
	catch (Exception e) {
		return;
	}
	listAttribMQ.put("MSG", vMQMsg);
	listAttribMQ.put("GET", vMQGet);
	listAttribMQ.put("PUT", vMQPut);
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	
	CommBeanMQ	 					msg = new CommBeanMQ();
	int								openOpt;
	String							strAux = "Nothing";
	
	openOpt = com.ibm.mq.MQC.MQOO_INPUT_AS_Q_DEF | com.ibm.mq.MQC.MQOO_OUTPUT;
	
	try {
		msg.setHostname("150.1.1.1");
		msg.setChannel("JAVA.CHANNEL");
		msg.setPort(1414);
		
		msg.connectQMngr("S60A.QM");
		msg.setQueueName("S60A.GCWCOTI");
		msg.openQueue(openOpt);
		
		msg.putMsg("Prueba de IDA");
		
		String txtMsg = "";
		txtMsg = msg.getMsg();
		
		msg.closeQueue();
		msg.closeQMngr();
		
		System.out.println("Todo OK. String GET: >>" + txtMsg + "<<");
	}
	catch (Exception e) {
		System.out.println("Error: main() -> " + e);
	}
	System.out.println("Terminado el proceso.");
}
/**
 * open method comment.
 */
@Override
public void open() throws servicios.generales.WSException {
	try {
		openQueue();
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al abrir la cola, " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:33:54 PM)
 * @exception servicios.generales.WSException The exception description.
 */
private void openQueue() throws servicios.generales.WSException {

	if (QNameMsg == null)
		throw new servicios.generales.WSException("Error: El valor del nombre de la Queue es inválido.");
		
	try {
		if (QMngr == null) {
			System.out.println("Objeto del Queue Manager es invalido, se intentará reconectar");
			reconnectMQMgr();
		}
		if (!QMngr.isConnected()){
			System.out.println("Objeto del Queue Manager no esta conectado, se intentará reconectar");
			reconnectMQMgr();
		}
		if (QNameMsg != null) {
			QMsg = QMngr.accessQueue(QNameMsg, optsOpen, null, null, null);
		}
		else
			throw new WSException("No existe el nombre de la cola, objeto NULO");
	}
	catch (MQException e) {
		System.out.println("Error al accesar a la cola " + QNameMsg + " Tipo apertura = " + optsOpen);
		Object o = e.exceptionSource;
		System.out.println("MQException se genero en el objeto '" + o.toString());
		System.out.println("Reason code = " + e.reasonCode);
		System.out.println("Completion code = " + e.completionCode);
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error al accesar la Queue: " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/00 7:33:54 PM)
 * @param nameQueue java.lang.String
 * @param openOptions int
 * @exception servicios.generales.WSException The exception description.
 */
public void openQueue(int openOptions) throws servicios.generales.WSException {

	try {
	QMsg = QMngr.accessQueue(QNameMsg, openOptions, null, null, null);
	return;
	}
	catch(com.ibm.mq.MQException e) {
		throw new servicios.generales.WSException("Error al abrir la Queue (" + QNameMsg +"): " + e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:07:03 PM)
 * @param msgIn com.ibm.mq.MQMessage
 * @param putOpts com.ibm.mq.MQPutMessageOptions
 * @exception servicios.generales.WSException The exception description.
 */
public void putMsg(MQMessage msgIn, MQPutMessageOptions putOpts) throws servicios.generales.WSException {
		

	if (msgIn == null)
		return;
	if (putOpts == null)
		return;
			
	try {
		QMsg.put(msgIn, putOpts);
		//Solo si se especifica la creacion de nuevos ids para todos los mensajes estos IDs estaran disponibles
		if((putOpts.options & MQC.MQPMO_NEW_MSG_ID) > 0)
			setMessageId(msgIn.messageId);
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error: putMsg() -> " + e);
	}
	
	return;	
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:07:03 PM)
 * @param strData java.lang.String
 * @exception servicios.generales.WSException The exception description.
 */
private void putMsg(String strData) throws servicios.generales.WSException {
	putMsg(strData, optsPut);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 12:07:03 PM)
 * @param strData java.lang.String
 * @param putOpts com.ibm.mq.MQPutMessageOptions
 * @exception servicios.generales.WSException The exception description.
 */
private void putMsg(String strData, MQPutMessageOptions putOpts) throws servicios.generales.WSException {
	if (strData == null)
		throw new servicios.generales.WSException("La data ingresada NO tiene contenido");
	if (putOpts == null)
		throw new servicios.generales.WSException("El objeto de Opciones de PUT no es válido");
	optsPut = putOpts;
	MsgData = null;
	MsgData = new com.ibm.mq.MQMessage();
	try {
		MsgData.format = MQC.MQFMT_STRING;
		MsgData.write(strData.getBytes());
		putMsg(MsgData, optsPut);
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error: putMsg() -> " + e);
	}
	return;
}
/**
 * recvMsg method comment.
 */
@Override
public java.lang.Object recvMsg(byte[] idMsg) throws servicios.generales.WSException {
	try {
//		printIDMQ(idMsg);
		mResult.setDataMsg(getMsg(getOptsGet(),idMsg));
		return mResult;
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al recivir mensajes, " + e.getMessage());
	}
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (01-12-2000 12:16:29)
 */
public void rollback() {
	try {
		QMngr.backout();
	}
	catch(Exception e) {}
}
/**
 * sendMsg method comment.
 */
@Override
public void sendMsg(java.lang.Object objMsg) throws servicios.generales.WSException {
	//	mResult = (ResultadoMsg)objMsg;
	try {
		putMsg((String) objMsg, getOptsPut());
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al colocar mensaje en la cola, " + e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 6:01:03 PM)
 * @param params java.util.Properties
 */
public void setAccessAttrib(Properties params) {}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 10:33:11 PM)
 * @param name java.lang.String
 * @param value java.lang.Object
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setAttrib(String name, Object value) throws servicios.generales.WSException {
	
	
	
	
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/00 10:34:59 PM)
 * @param name java.lang.String
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setChannel(String name) throws servicios.generales.WSException {
	
	if (name == null) {
		throw new servicios.generales.WSException("Valor para Channel NO válido");
	}
	
	com.ibm.mq.MQEnvironment.channel = name;
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/00 10:30:37 PM)
 * @param name java.lang.String
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setHostname(String name) throws servicios.generales.WSException {

	if (name == null) {
		throw new servicios.generales.WSException("Valor para HostName NO válido");
	}
	
	com.ibm.mq.MQEnvironment.hostname = name;
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 5:58:07 PM)
 * @param params java.util.Vector
 */
public void setOptsGet(Vector params) throws servicios.generales.WSException {

	optsGet.options = 0;
	for (int i =0; i<params.size(); i++) {
		String elem = (String)params.elementAt(i);
		int    valOpt  = getValueCteMQ(elem);
		
		if (valOpt > 0) 
			optsGet.options = optsGet.options | valOpt;
		else
			throw new servicios.generales.WSException("Error: Constante MQ (" + elem + "): No se encontró ");
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 5:56:38 PM)
 * @param params java.util.Vector
 */
public void setOptsOpen(Vector params) throws servicios.generales.WSException {

	optsOpen = 0;
	for (int i =0; i<params.size(); i++) {
		String elem = (String)params.elementAt(i);
		int valOpt = getValueCteMQ(elem);
		
		if (valOpt > 0) 
			optsOpen = optsOpen | valOpt;
		else
			throw new servicios.generales.WSException("Error: Constante MQ (" + elem + "): No se encontró ");
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 5:59:09 PM)
 * @param params java.util.Vector
 */
public void setOptsPut(Vector params) throws servicios.generales.WSException {
	optsPut.options = 0;
	for (int i = 0; i < params.size(); i++) {
		String elem = (String) params.elementAt(i);
		int valOpt = getValueCteMQ(elem);
		if (valOpt > 0)
			optsPut.options = optsPut.options | valOpt;
		else
			throw new servicios.generales.WSException("Error: Constante MQ (" + elem + "): No se encontró ");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/00 10:39:07 PM)
 * @param value java.lang.String
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setPassword(String value) throws servicios.generales.WSException {
	
	if (value == null) {
		throw new servicios.generales.WSException("Valor para Password NO válido");
	}
	
	com.ibm.mq.MQEnvironment.password = value;
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/00 10:33:08 PM)
 * @param value int
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setPort(int value) throws servicios.generales.WSException {

	if (value <1 ) {
		throw new servicios.generales.WSException("Valor para Port NO válido");
	}
	
	com.ibm.mq.MQEnvironment.port = value;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 10:25:31 PM)
 * @param prop java.util.Properties
 */
private void setPropAttr(Properties prop) throws servicios.generales.WSException {

	if (prop == null) {
		throw new servicios.generales.WSException("Valor para Properties NO válido");
	}
	
	//com.ibm.mq.MQEnvironment.properties = prop;

	Vector vWR = (Vector)prop.get("WR");
	Vector vRD  = (Vector)prop.get("RD");

	if (vWR != null) setOptsOpen(vWR);
	if (vRD != null) setOptsOpen(vRD);

}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 10:23:59 PM)
 * @param props java.util.Properties
 */
private void setPropCmd(Properties prop) throws servicios.generales.WSException {
	if (prop == null) {
		throw new servicios.generales.WSException("Valor para Properties NO válido");
	}

	//com.ibm.mq.MQEnvironment.properties = prop;

	Vector vOpen = (Vector) prop.get("OPEN");
	Vector vGet = (Vector) prop.get("GET");
	Vector vPut = (Vector) prop.get("PUT");
	if (vOpen != null)
		setOptsOpen(vOpen);
	if (vGet != null)
		setOptsGet(vGet);
	if (vPut != null)
		setOptsPut(vPut);
}
/**
 * setProperties method comment.
 */
@Override
public void setProperties(java.lang.Object paramComm) throws servicios.generales.WSException {
	try {
		servicios.mqseries.CommParms vParamComm = (servicios.mqseries.CommParms) paramComm;
		setPropCmd(vParamComm.getCmdOptions());
		setQNameMsg(vParamComm.getCola());
//		setPropAttr(vParamComm.getAttribs());
	}
	catch (Exception e) {
		throw new servicios.generales.WSException("Error al configurar los parametros, " + e.getMessage());
	}
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:50:05)
 * @param newQMngr com.ibm.mq.MQQueueManager
 */
public void setQMngr(com.ibm.mq.MQQueueManager newQMngr) {
	QMngr = newQMngr;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:50:05)
 * @param newQMngr com.ibm.mq.MQQueueManager
 */
public void setQMngr(String nameQMngr) throws servicios.generales.WSException {

	try {
		QMngr = new com.ibm.mq.MQQueueManager(nameQMngr);
	}
	catch(Exception e) {
		throw new servicios.generales.WSException("Error: El nombre del QueueManager no es válido. " + e);
	}
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:35:52)
 * @param newQNameMsg java.lang.String
 */
public void setQNameMsg(java.lang.String newQNameMsg) {
	QNameMsg = newQNameMsg;
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (19-11-2000 19:40:43)
 * @param nameQueue java.lang.String
 */
public void setQueueName(String nameQueue) {
	QNameMsg = nameQueue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/00 10:37:58 PM)
 * @param value java.lang.String
 * @exception pruebas.servicios.generales.WSException The exception description.
 */
public void setUserId(String value) throws servicios.generales.WSException {
	
	if (value == null) {
		throw new servicios.generales.WSException("Valor para UserId NO válido");
	}
	
	com.ibm.mq.MQEnvironment.userID = value;
}
	@Override
	public byte[] getMessageId() throws WSException {
		return this.messageID;
	}

	@Override
	public void setMessageId(byte[] messageId) throws WSException {
		this.messageID = messageId;
	}
	private String getMsg(MQGetMessageOptions getOpts, byte[] idMsg) throws servicios.generales.WSException {

		String strData=null;
	
		if (getOpts == null)
			throw new servicios.generales.WSException("Error, las opciones de Get NO son válidas");
		
		try {
			MsgData = null;
			MsgData = new com.ibm.mq.MQMessage();
			//El unico criterio de pareo para los mensajes MQ que es soportado por el FCE es el de id de mensaje
			if(idMsg != null){
				getOpts.matchOptions = MQC.MQMO_MATCH_MSG_ID;
				//Asigna el ID entrante como el valor a parear
				MsgData.messageId = idMsg;
			}
			//Si se define que habra un tiempo de espera limitado se asignan 5 segundos en DURO!!!!
			//Si no viene una definicion de espera entonces el thread espera hasta que llegue un mensaje con
			//el ID correcto, lo que es arriesgado pues puede dejar al thread pegado
			if((getOpts.options & MQC.MQGMO_WAIT)>0){
				//Espero 60 segundos por el  mensaje con el ID correcto
				getOpts.waitInterval = 60000;				
			}
			MsgData.format =MQC.MQFMT_STRING;
			QMsg.get(MsgData, getOpts);
			byte[] bData = new byte[MsgData.getMessageLength()];
			MsgData.readFully(bData, 0, MsgData.getMessageLength());
			strData = new String(bData);
			return(strData);
		}
		catch(Exception e) {
			throw new servicios.generales.WSException("Error: getMsg() -> " + e);
		}
		
	}
	public void reconnectMQMgr() throws MQException{
		//Reconeccion al Queue Manager
		System.out.println("Reconectando al Queue Manager");
		try {
			this.conexion.init();
			connectQMngr(conexion.getQMgr());
		}
		catch (MQException e) {
			e.printStackTrace();
		}
		catch (WSException e) {
			e.printStackTrace();
		}
	}
	public void printIDMQ(byte[] data){
		for(int i=0;i < data.length;i++){
			System.out.print(data[i]);
		}
	}
}
