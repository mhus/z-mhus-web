package de.mhus.cherry.web.impl.webspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.TransformUtil;

public class TransformWebSpace extends AbstractWebSpace {

	String characterEncoding = "utf-8";
	private IConfig cDir;
	private String index = "index.html";
	private String[] extensionOrder = new String[] { ".twig" };
	private String[] removeExtensions = new String[] { ".html", ".htm" };
	private String[] htmlExtensions = new String[] { ".html", ".htm" };
	private File templateRoot;
	private File htmlHeader = null;
	private File htmlFooter = null;
	private File errorTemplate = null;

	private static final HashMap<Integer, String> errorCodes = new HashMap<Integer, String>() {
	    private static final long serialVersionUID = 1L;
	    {
	      put(100, "Continue");
	      put(101, "Switching Protocols");
	      put(200, "OK");
	      put(201, "Created");
	      put(202, "Accepted");
	      put(203, "Non-Authoritative Information");
	      put(204, "No Content");
	      put(205, "Reset Content");
	      put(300, "Multiple Choices");
	      put(301, "Moved Permanently");
	      put(302, "Found");
	      put(303, "See Other");
	      put(304, "Not Modified");
	      put(305, "Use Proxy");
	      put(307, "Temporary Redirect");
	      put(400, "Bad Request");
	      put(401, "Unauthorized");
	      put(402, "Payment Required");
	      put(403, "Forbidden");
	      put(404, "Not Found");
	      put(405, "Method Not Allowed");
	      put(406, "Not Acceptable");
	      put(407, "Proxy Authentication Required");
	      put(408, "Request Time-out");
	      put(409, "Conflict");
	      put(410, "Gone");
	      put(411, "Length Required");
	      put(412, "Precondition Failed");
	      put(413, "Request Entity Too Large");
	      put(414, "Request-URI Too Large");
	      put(415, "Unsupported Media Type");
	      put(416, "Requested range not satisfiable");
	      put(417, "SlimExpectation Failed");
	      put(500, "Internal Server Error");
	      put(501, "Not Implemented");
	      put(502, "Bad Gateway");
	      put(503, "Service Unavailable");
	      put(504, "Gateway Time-out");
	      put(505, "HTTP Version not supported");
	    }
	  };	
	@Override
	public void start(CherryApi api) throws MException {
		super.start(api);
		cDir = getConfig().getNode("transform");
		templateRoot = getDocumentRoot();
		if (cDir != null) {
			characterEncoding = cDir.getString("characterEncoding", null);
			if (cDir.isProperty("indexe"))
				index = cDir.getString("index");
			if (cDir.isProperty("templateRoot"))
				templateRoot = findTemplateFile(cDir.getString("templateRoot"));
			if (cDir.isProperty("extensionOrder")) {
				extensionOrder = cDir.getString("extensionOrder").split(",");
				MCollection.updateEach(extensionOrder, e -> "." + e );
			}
			if (cDir.isProperty("removeExtensions")) {
				removeExtensions = cDir.getString("removeExtensions").split(",");
				MCollection.updateEach(removeExtensions, e -> "." + e );
			}
			if (cDir.isProperty("htmlExtensions")) {
				htmlExtensions = cDir.getString("htmlExtensions").split(",");
				MCollection.updateEach(htmlExtensions, e -> "." + e );
			}
			if (cDir.isProperty("header")) {
				String header = cDir.getString("header");
				htmlHeader = new File(getDocumentRoot(), header);
				if (!htmlHeader.exists()) {
					log().w("ignore html header",htmlHeader.getAbsolutePath());
				}
			}
			if (cDir.isProperty("footer")) {
				String footer = cDir.getString("footer");
				htmlFooter = new File(getDocumentRoot(), footer);
				if (!htmlFooter.exists()) {
					log().w("ignore html footer",htmlFooter.getAbsolutePath());
				}
			}
			if (cDir.isProperty("error")) {
				String error = cDir.getString("error");
				errorTemplate = new File(getDocumentRoot(), error);
				if (!errorTemplate.exists()) {
					log().w("ignore error template",errorTemplate.getAbsolutePath());
				}
			}
		}
	}

	@Override
	protected void doDeleteRequest(CallContext context) throws Exception {
	}

	@Override
	protected void doPutRequest(CallContext context) throws Exception {
	}

	@Override
	protected void doPostRequest(CallContext context) throws Exception {
	}

	@Override
	protected void doHeadRequest(CallContext context) throws Exception {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(templateRoot, path);
		if (file.exists()) {
			if (file.isDirectory()) {
				path = path + "/" + index;
			} else
			if (hasTransformExtension(path)) {
				// path = MString.beforeLastIndex(path, '.');
				sendError(context, HttpServletResponse.SC_NOT_FOUND);
				return;
			} else {
				prepareHead(context,file, path);
				return;
			}
		}
		
		String orgPath = path;
		// find template
		for (String extension : removeExtensions) {
			if (path.endsWith(extension)) {
				path = path.substring(0, path.length()-extension.length());
				break;
			}
		}
		
		for (String extension : extensionOrder) {
			String p = path + extension;
			file = new File(templateRoot, p);
			if (file.exists() && file.isFile()) {
				prepareHead(context, file, orgPath);
			}
		}
		
		sendError(context, HttpServletResponse.SC_NOT_FOUND);

	}

	@Override
	protected void doGetRequest(CallContext context) throws Exception {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(templateRoot, path);

		if (file.exists()) {
			if (file.isDirectory()) {
				path = path + "/" + index;
				file = new File(templateRoot, path);
			}
			if (hasTransformExtension(path)) {
				// path = MString.beforeLastIndex(path, '.');
				sendError(context, HttpServletResponse.SC_NOT_FOUND);
				return;
			} else {
				prepareHead(context,file, path);
				try {
					boolean isHtml = hasHtmlExtension(path);
					ServletOutputStream os = context.getHttpResponse().getOutputStream();
					
					if (isHtml && htmlHeader != null) {
						doTransform(context, htmlHeader, null, null);
					}
					
					FileInputStream is = new FileInputStream(file);
					MFile.copyFile(is, os);
					is.close();

					if (isHtml && htmlFooter != null) {
						doTransform(context, htmlFooter, null, null);
					}

					os.flush();
					
				} catch (Throwable t) {
					log().w(file,t);
					sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				return;
			}
		}

		String orgPath = path;
		// find template
		for (String extension : removeExtensions) {
			if (path.endsWith(extension)) {
				path = path.substring(0, path.length()-extension.length());
				break;
			}
		}
		
		for (String extension : extensionOrder) {
			String p = path + extension;
			file = new File(templateRoot, p);
			if (file.exists() && file.isFile()) {
				doTransform(context, file, path, orgPath);
				return;
			}
		}
		
		sendError(context, HttpServletResponse.SC_NOT_FOUND);
	}

	private boolean hasTransformExtension(String path) {
		for (String extension : extensionOrder) {
			if (path.endsWith(extension)) return true;
		}
		return false;
	}

	private boolean hasHtmlExtension(String path) {
		for (String extension : htmlExtensions) {
			if (path.endsWith(extension)) return true;
		}
		return false;
	}

	private void doTransform(CallContext context, File from, String path, String orgPath) {
		
		MProperties param = new MProperties();
		param.put("session", context.getSession().pub());
		param.put("sessionId", context.getSessionId());
		param.put("request", context.getHttpRequest().getParameterMap());
		param.put("path", context.getHttpPath());
		
		try {
			if (orgPath != null)
				prepareHead(context,from, orgPath);
			ServletOutputStream os = context.getHttpResponse().getOutputStream();
			TransformUtil.transform(from, os, getDocumentRoot(), null, null, param, null);
			os.flush();
		} catch (Exception e) {
			log().e(name,from,e);
			if (orgPath != null)
				sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void prepareHead(CallContext context, File from, String path) {
		String mimeType = getMimeType(path);
		HttpServletResponse resp = context.getHttpResponse();
		if (mimeType != null)
			resp.setContentType(mimeType);
		resp.setCharacterEncoding(characterEncoding);
		resp.setHeader("Last-Modified", MDate.toHttpHeaderDate(from.lastModified()));
	}

	public File findTemplateFile(String path) {
		if (path.startsWith("/")) {
			if (MSystem.isWindows())
				return new File(path.substring(1));
			else
				return new File(path);
		}
		return new File(getDocumentRoot(), path);
	}

	@Override
	public void sendError(CallContext context, int sc) {
		if (traceAccess)
			log().i(name,context.getHttpHost(),"error",context.getHttpRequest().getRemoteAddr(),context.getHttpMethod(),context.getHttpPath(),sc);
		if (traceErrors)
			log().i(name,context.getHttpHost(),sc,Thread.currentThread().getStackTrace());
		if (context.getHttpResponse().isCommitted()) {
			log().w("Can't send error to committed content",name,sc);
			return;
		}
		
		if (errorTemplate != null) {

			try {
				MProperties param = new MProperties();
				param.put("session", context.getSession().pub());
				param.put("sessionId", context.getSessionId());
				param.put("request", context.getHttpRequest().getParameterMap());
				param.put("path", context.getHttpPath());
				param.put("error", sc);
				param.put("errorMsg", errorCodes.getOrDefault(sc, ""));
				
				ServletOutputStream os = context.getHttpResponse().getOutputStream();
				TransformUtil.transform(errorTemplate, os, getDocumentRoot(), null, null, param, null);
				context.getHttpResponse().setContentType("text/html");
				context.getHttpResponse().setCharacterEncoding(characterEncoding);
				os.flush();
				
			} catch (Throwable e) {
				log().e(name,errorTemplate,e);
			}
			return;
		}
		
		// fallback
		try {
			context.getHttpResponse().sendError(sc);
		} catch (IOException e) {
			log().t(e);
		}
	}

}
