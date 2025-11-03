package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (19-Oct-00 10:44:40)
 * @author: Juan Pablo Alamo
 */
public class TemplateField
{
	protected String name;
	protected int fix_size = 0;
	protected TemplateField var_size = null;
	protected TemplateMessage templatemessage;
/**
 * Campo constructor comment.
 */
public TemplateField(TemplateMessage tm)
{
	super();
	this.templatemessage = tm;
}
/**
 * Insert the method's description here.
 * Creation date: (25-Oct-00 11:23:02)
 * @param tm com.cmpc.mq.TemplateMessage
 * @param name java.lang.String
 */
public TemplateField(TemplateMessage tm, String name)
{
	this(tm);
	this.setName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:02:27)
 * @return java.lang.String
 */
public java.lang.String getName()
{
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:02:27)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName)
{
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:23:16)
 * @param value int
 */
public void setSize(int value)
{
	var_size = null;
	fix_size = value;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:23:16)
 * @param value int
 */
public void setSize(String fieldname) throws NotFoundTemplateFieldException, TemplateGroupFieldException
{
	TemplateField tf = templatemessage.getField(fieldname);

	if ( tf instanceof TemplateGroupField )
		throw new TemplateGroupFieldException();

	var_size = tf;
	fix_size = 0;
}
}
