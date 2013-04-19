package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (19-Oct-00 10:37:48)
 * @author: Juan Pablo Alamo
 */
import java.util.Enumeration;
import java.util.Vector;
public class TemplateMessage
{
	protected String name;
	protected Vector fields;
/**
 * Header constructor comment.
 */
public TemplateMessage()
{
	super();
	fields = new Vector();
}
/**
 * Header constructor comment.
 */
public TemplateMessage(String name)
{
	super();
	this.name = name;
	fields = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 09:57:53)
 * @param field com.cmpc.mq.TemplateField
 */
public void addField(TemplateField field)
{
	fields.addElement(field);
}
/**
 * Insert the method's description here.
 * Creation date: (27-10-2000 10:23:52 AM)
 * @param groupname java.lang.String
 * @param tf servicios.mensajeria.TemplateField
 */
public void addFieldToGroup(String groupname, TemplateField tempfield) throws NotFoundTemplateFieldException {
	TemplateField tf = getField(groupname);
	if ( tf instanceof TemplateGroupField )
	{
		TemplateGroupField tgf = (TemplateGroupField)tf;
		tgf.addField(tempfield);
		return;
	}
	throw new NotFoundTemplateFieldException();
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 09:59:03)
 * @param field com.cmpc.mq.TemplateField
 */
public TemplateField getField(String fieldname) throws NotFoundTemplateFieldException
{
	Enumeration enumeracion = fields.elements();
	while (enumeracion.hasMoreElements())
	{
		TemplateField tf = (TemplateField) enumeracion.nextElement();
		if (tf.getName().equalsIgnoreCase(fieldname))
			return tf;
		if (tf instanceof TemplateGroupField)
		{
			TemplateGroupField tgf = (TemplateGroupField) tf;
			try
			{
				tf = tgf.getInerField(fieldname);
				return tf;
			}
			catch (NotFoundTemplateFieldException e)
			{
				continue;
			}
		}
	}
	throw new NotFoundTemplateFieldException();
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 18:54:41)
 * @return java.lang.String
 */
public java.lang.String getName()
{
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (24-Oct-00 18:54:41)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName)
{
	name = newName;
}
}
