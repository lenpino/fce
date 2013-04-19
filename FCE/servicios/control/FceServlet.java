package servicios.control;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.LogManager;

/**
 * @author lpino
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FceServlet extends CtrlServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected servicios.control.ServiceMgr manager;
	private String uploadedFileName = "";
	private byte[] uploadedFileData = null;
	/**
	 * Constructor for CtrlExtranet
	 */
	public FceServlet() {
		super();
	}
	@Override
	public void init() throws javax.servlet.ServletException {
		super.init();
		try {
			ServletConfig config = getServletConfig();
			ServletContext contexto = config.getServletContext();
			manager = new servicios.control.ServiceMgr();
			tipoSesion = config.getInitParameter("sesion");
			manager.init(config);
			manager.setListaXsl(this.xslList);
			contexto.setAttribute("contexto", manager);
		}
		catch (Exception e) {
			System.out.println("Error al inicializar el ServiceMgr: " + e.getMessage());
		}
	}
	@Override
	public void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
		performTask(request, response);
	}
	@Override
	public void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
		performTask(request, response);
	}
	public void performTask(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws ServletException {
		ThreadService servicio = new ThreadService(manager);
		try {
			String contentType = request.getContentType();
			if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
				uploadFile(request);
			}
		  // Set to expire far in the past.
		  response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		  // Set standard HTTP/1.1 no-cache headers.
		  response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		  // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		  response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		  // Set standard HTTP/1.0 no-cache header.
		  response.setHeader("Pragma", "no-cache");
			servicio.setRes(response);
			String pagJsp = servicio.execute(request);
			if (tipoSesion.equalsIgnoreCase("cookie"))
				callPage(pagJsp, request, response);
			else if (tipoSesion.equalsIgnoreCase("url"))
				callPageURLRW(pagJsp, request, response);
			else
				throw new Exception();
		}
		catch (Exception theException) {
			handleError(request, response, theException);
		}
		finally{
			//Limpieza del thread asegurando su recoleccion por el GC
			servicio = null;
		}
	}
	public void uploadFile(javax.servlet.http.HttpServletRequest request){
		try {
			int formDataLength = request.getContentLength();
			DataInputStream in = new DataInputStream(request.getInputStream());
			String contentType = request.getContentType();
			byte dataBytes[] = new byte[formDataLength];

			int bytesRead = 0;
			int totalBytesRead = 0;;
			while (totalBytesRead < formDataLength) {
				bytesRead = in.read(dataBytes, totalBytesRead, formDataLength);
				totalBytesRead += bytesRead;
			}
			// Crea un string para su manipulacion
			String file = new String(dataBytes, "ISO-8859-1");
			dataBytes = null;

			int lastIndex = contentType.lastIndexOf("=");
			String boundary = contentType.substring(lastIndex + 1, contentType.length());
			String match_str = "--" + boundary + "\r\n"+ "Content-Disposition: form-data; name=\"file_n\"";
			int index1 = file.indexOf(match_str);
			String saveFile = file.substring(file.indexOf("filename=\"", index1) + 10);
			saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
			saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1, saveFile.indexOf("\""));
			this.uploadedFileName = saveFile;
			 // position in upload file
			int pos;
			// find position of upload file section of copyRequest
			pos = file.indexOf("filename=\"", index1);
			// find position of  line
			pos = file.indexOf("\n", pos) + 1;
			// find position of content-type line
			pos = file.indexOf("\n", pos) + 1;
			// find position of blank line
			pos = file.indexOf("\n", pos) + 1;

			int boundaryLocation = file.indexOf(boundary, pos) - 4;
			// Los bytes del archivo que se subio se ubican entre pos y boundaryLocation
			file = file.substring(pos, boundaryLocation);
			this.uploadedFileData = file.getBytes("ISO-8859-1");
			//Coloca los datos en el request
			request.setAttribute("fileName", this.uploadedFileName);
			request.setAttribute("fileData", this.uploadedFileData);
			//Configura el prgfileupload
			request.setAttribute("reqName", "prgfileupload");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void destroy(){
		super.destroy();
		LogManager.shutdown();
	}
}
