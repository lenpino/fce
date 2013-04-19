package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (24-Oct-00 09:12:16)
 * @author: Juan Pablo Alamo
 */
import java.util.Enumeration;
import java.util.Vector;
public class TemplateGroupField extends TemplateField
{
	protected int fix_repeat;
	protected TemplateField var_repeat;
	protected Vector fields;
/**
 * GroupField constructor comment.
 */
public TemplateGroupField(TemplateMessage tm)
{
	super(tm);
	fields = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (25-Oct-00 11:26:14)
 * @param tm com.cmpc.mq.TemplateMessage
 * @param name java.lang.String
 */
public TemplateGroupField(TemplateMessage tm, String name)
{
	this(tm);
	this.setName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 18:56:25)
 * @param tf com.cmpc.mq.TemplateField
 */
public void addField(TemplateField field)
{
	fields.addElement(field);
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 18:36:29)
 * @return com.cmpc.mq.TemplateField
 * @param fieldname java.lang.String
 */
public TemplateField getInerField(String fieldname) throws NotFoundTemplateFieldException
{
	Enumeration enumeracion = fields.elements();
	while (enumeracion.hasMoreElements())
	{
		TemplateField tf = (TemplateField) enumeracion.nextElement();
		if (tf.getName().equalsIgnoreCase(fieldname))
			return tf;
		if (tf instanceof TemplateGroupField)
		{
			try
			{
				TemplateGroupField tgf = (TemplateGroupField) tf;
				tf = tgf.getInerField(fieldname);
				return tf;
			}
			catch (NotFoundTemplateFieldException e)
			{
				continue;
			}
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:03:44)
 * @param fixvalue int
 */
public void setRepeat(int fixvalue)
{
	this.fix_repeat = fixvalue;
	this.var_repeat = null;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 10:07:48)
 * @param fieldname java.lang.String
 * @exception com.cmpc.mq.NotFoundTemplateFieldException The exception description.
 */
public void setRepeat(String fieldname) throws NotFoundTemplateFieldException, TemplateGroupFieldException
{
	TemplateField tf = templatemessage.getField(fieldname);

	if ( tf instanceof TemplateGroupField )
		throw new TemplateGroupFieldException();
		
	var_repeat = tf;
	fix_repeat = 0;
}
}
