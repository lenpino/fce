package servicios.mqseries;

/**
 * Insert the type's description here.
 * Creation date: (10-10-2000 10:15:08 AM)
 * @author: Leonardo Pino
 */
// import com.ibm.mqbind.*;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQQueueManager;
public class ConectorMQ {
  	private String qManagerName = "";          
  	private MQQueueManager qMgr;                         
	protected int idQmgr = 0;
	protected EntornoMq envMq;
/**
 * ConectorMQ constructor comment.
 */
public ConectorMQ() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:57:22 PM)
 * @return servicios.mqseries.EntornoMq
 */
public EntornoMq getEnvMq() {
	return envMq;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:50:00 PM)
 * @return int
 */
public int getIdQmgr() {
	return idQmgr;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 10:42:46 AM)
 * @return java.lang.String
 */
public String getQManagerName() {
	return this.qManagerName;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 10:37:53 AM)
 * @return com.ibm.mq.MQQueueManager
 */
public MQQueueManager getQMgr() {
	return this.qMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 10:30:20 AM)
 */
public void init() throws MQException{
	  MQEnvironment.hostname = envMq.getHost();
	  MQEnvironment.channel  = envMq.getChannel();
	  MQEnvironment.port     = envMq.getPort();
	  if(envMq.getPassword() != null)
		  MQEnvironment.password = envMq.getPassword();
	  if(envMq.getUserID() != null)
		  MQEnvironment.userID = envMq.getUserID();
	  MQPoolToken token=MQEnvironment.addConnectionPoolToken();
	  if(qManagerName.equalsIgnoreCase("") || qManagerName == null)
	  	qMgr = new com.ibm.mq.MQQueueManager("");
	  else
	  	qMgr = new com.ibm.mq.MQQueueManager(qManagerName);
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:57:22 PM)
 * @param newEnvMq servicios.mqseries.EntornoMq
 */
public void setEnvMq(EntornoMq newEnvMq) {
	envMq = newEnvMq;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:50:00 PM)
 * @param newIdQmgr int
 */
public void setIdQmgr(int newIdQmgr) {
	idQmgr = newIdQmgr;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 10:45:45 AM)
 * @param valor java.lang.String
 */
public void setQManagerName(String valor) {
	this.qManagerName = valor;	
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 10:39:35 AM)
 * @param valor com.ibm.mq.MQQueueManager
 */
public void setQMgr(MQQueueManager valor) {
	this.qMgr = valor;
}
}
