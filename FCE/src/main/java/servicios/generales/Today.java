package servicios.generales;

import java.util.Calendar;

/**
 * Insert the type's description here.
 * Creation date: (29/8/00 4:48:37 p.m.)
 * @author: Iván Pérez
 */
public class Today {
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:32:10 p.m.)
 * @return java.lang.String
 */
public static String getDate() {
	return getDate(0);
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:32:10 p.m.)
 * @return java.lang.String
 */
public static String getDate(int days) {
	java.util.Calendar cal = java.util.Calendar.getInstance();
	if (days != 0)
		cal.add(Calendar.DAY_OF_MONTH, days);
	return "" + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) < 10 ? "0" : "") + cal.get(Calendar.MONTH) + (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH);
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:19:11 p.m.)
 * @return java.lang.String
 * @param days int
 */
public static String getDateAMD(int days) {
	java.util.Calendar cal = java.util.Calendar.getInstance();
	cal.add(Calendar.DAY_OF_MONTH, days);
	return "" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) < 10 ? "0" : "") + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH);
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:18:15 p.m.)
 * @return java.lang.String
 */
public static String getTodayAMD() {
	return getTodayAMD('-');
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:12:28 p.m.)
 * @return java.lang.String
 */
public static String getTodayAMD(char sep) {
	java.util.Calendar c = java.util.Calendar.getInstance();
	return "" + c.get(Calendar.YEAR) + sep + (c.get(Calendar.MONTH) < 10 ? "0" : "") + c.get(Calendar.MONTH) + sep + (c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + c.get(Calendar.DAY_OF_MONTH);
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:12:28 p.m.)
 * @return java.lang.String
 */
public static String getTodayDMA() {
	return getTodayDMA('-');
}
/**
 * Insert the method's description here.
 * Creation date: (31/8/00 8:12:28 p.m.)
 * @return java.lang.String
 */
public static String getTodayDMA(char sep) {
	java.util.Calendar c = java.util.Calendar.getInstance();
	return "" + (c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + c.get(Calendar.DAY_OF_MONTH) + sep + (c.get(Calendar.MONTH) < 10 ? "0" : "") + c.get(Calendar.MONTH) + sep + c.get(Calendar.YEAR);
}
/**
 * Insert the method's description here.
 * Creation date: (29/8/00 4:49:44 p.m.)
 * @return java.lang.String
 */
public static String getTodayMillisec() {
	return new java.util.Date().getTime() + "";
}
/**
 * Insert the method's description here.
 * Creation date: (29/8/00 4:51:10 p.m.)
 * @return long
 */
public static long getTodayMillisecLong() {
	return new java.util.Date().getTime();
}
}
