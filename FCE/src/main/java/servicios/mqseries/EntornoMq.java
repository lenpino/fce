package servicios.mqseries;

/**
 * Insert the type's description here.
 * Creation date: (10-10-2000 06:53:46 PM)
 * @author: Leonardo Pino
 */
public class EntornoMq {
	protected java.lang.String host;
	protected java.lang.String channel;
	protected java.lang.String port;
	protected java.lang.String password;
	protected java.lang.String userID;
/**
 * EntornoMq constructor comment.
 */
public EntornoMq() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (27-11-2000 12:09:05 PM)
 * @param host java.lang.String
 * @param canal java.lang.String
 * @param port java.lang.String
 */
public EntornoMq(String host, String canal, String port) {
	super();
	setHost(host);
	setChannel(canal);
	setPort(port);
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:56:07 PM)
 * @return java.lang.String
 */
public java.lang.String getChannel() {
	return channel;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:55:47 PM)
 * @return java.lang.String
 */
public java.lang.String getHost() {
	return host;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:56:24 PM)
 * @return java.lang.String
 */
public int getPort() {
	return Integer.parseInt(port);
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:56:07 PM)
 * @param newChannel java.lang.String
 */
public void setChannel(java.lang.String newChannel) {
	channel = newChannel;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:55:47 PM)
 * @param newHost java.lang.String
 */
public void setHost(java.lang.String newHost) {
	host = newHost;
}
/**
 * Insert the method's description here.
 * Creation date: (10-10-2000 06:56:24 PM)
 * @param newPort java.lang.String
 */
public void setPort(java.lang.String newPort) {
	port = newPort;
}
public java.lang.String getPassword() {
	return password;
}
public void setPassword(java.lang.String password) {
	this.password = password;
}
public java.lang.String getUserID() {
	return userID;
}
public void setUserID(java.lang.String userID) {
	this.userID = userID;
}
}
