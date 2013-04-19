package servicios.mensajeria;

/**
 * Insert the type's description here.
 * Creation date: (25-Oct-00 11:18:14)
 * @author: Juan Pablo Alamo
 */
public class TestTemplateMessage
{
/**
 * TestTemplateMessage constructor comment.
 */
public TestTemplateMessage()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (25-Oct-00 11:18:34)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	TemplateMessage tm = new TemplateMessage("msgMail");

	TemplateField tf;
	TemplateGroupField tgf, tgf2;

	try
	{
		// Agrega el campo "DAT1"
		tf = new TemplateField(tm);
		tf.setName("dat1");
		tf.setSize(10);
		tm.addField(tf);

		// Agrega el campo "DAT2"
		tf = new TemplateField(tm, "dat2");
		tf.setSize(2);
		tm.addField(tf);

		// Crea el grupo "G1"
		tgf = new TemplateGroupField(tm, "g1");
		tgf.setRepeat("dat2");

		// Agrega el grupo "G1" al "msgMail"
		tm.addField(tgf);

			// Agrega el campo "DAT4" al grupo "G1"
			tf = new TemplateField(tm, "dat4");
			tf.setSize("dat1");
			tgf.addField(tf);

			// Agrega el campo "DAT6" al grupo "G1"
			tf = new TemplateField(tm, "dat6");
			tf.setSize(10);
			tgf.addField(tf);

			// Crea el grupo "G2"
			tgf2 = new TemplateGroupField(tm, "g2");
			tgf2.setRepeat("DAT6");

				// Agrega el grupo "G2" al "msgMail"
				tm.addField(tgf2);

				// Agrega el campo "DAT11" al grupo "G1"
				tf = new TemplateField(tm, "dat11");
				tf.setSize(10);
				tgf2.addField(tf);
		
		// Agrega el campo "DAT9" al "msgMail"
		tf = new TemplateField(tm, "dat9");
		tf.setSize(2);
		tm.addField(tf);
	}
	catch (Exception e)
	{
		System.out.println("El contenido descrito en el XML no es correcto");
		System.exit(1);
	}
	System.out.println("El contenido descrito en el XML es correcto");	
}
}
