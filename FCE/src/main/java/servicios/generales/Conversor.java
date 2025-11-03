package servicios.generales;

/**
 * Insert the type's description here.
 * Creation date: (21-03-2001 02:53:30 PM)
 * @author: Administrator
 */
public class Conversor {
/**
 * Conversor constructor comment.
 */
public Conversor() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (21-03-2001 02:55:05 PM)
 * @return java.lang.String
 * @param urlIn java.lang.String
 */
public String convUrls(String urlIn) {
	while (urlIn.indexOf("&") >= 0) 
		urlIn = urlIn.substring(0, urlIn.indexOf("&")) + "%26" + urlIn.substring(urlIn.indexOf("&") + 1);
	while(urlIn.indexOf("?") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf("?")) + "%63" + urlIn.substring(urlIn.indexOf("?") + 1);		
	while(urlIn.indexOf("#") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf("#")) + "%23" + urlIn.substring(urlIn.indexOf("#") + 1);		
	while(urlIn.indexOf("<") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf("<")) + "%3C" + urlIn.substring(urlIn.indexOf("<") + 1);		
	while(urlIn.indexOf(">") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf(">")) + "%3E" + urlIn.substring(urlIn.indexOf(">") + 1);		
	while(urlIn.indexOf("=") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf("=")) + "%3D" + urlIn.substring(urlIn.indexOf("=") + 1);		
	while(urlIn.indexOf("\"") >= 0)
		urlIn = urlIn.substring(0, urlIn.indexOf("\"")) + "%22" + urlIn.substring(urlIn.indexOf("\"") + 1);		
	return urlIn;
}
}
