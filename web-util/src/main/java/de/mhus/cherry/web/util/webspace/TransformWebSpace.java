package de.mhus.cherry.web.util.webspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.util.webspace.AbstractWebSpace;
import de.mhus.cherry.web.api.CanTransform;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.TransformUtil;

public class TransformWebSpace extends AbstractWebSpace implements CanTransform {

	protected IConfig cDir;
	protected String index = "index";
	protected String[] extensionOrder = new String[] { ".twig" };
	protected String[] removeExtensions = new String[] { ".html", ".htm" };
	protected String[] htmlExtensions = new String[] { ".html", ".htm" };
	protected File templateRoot;
	protected File htmlHeader = null;
	protected File htmlFooter = null;
	protected File errorTemplate = null;
	protected MProperties environment = null;
	
	@Override
	public void start(CherryApi api) throws MException {
		super.start(api);
		cDir = getConfig().getNode("transform");
		templateRoot = getDocumentRoot();
		environment = new MProperties();
		if (cDir != null) {
			charsetEncoding = cDir.getString("characterEncoding", charsetEncoding);
			if (cDir.isProperty("index"))
				index = cDir.getString("index");
			if (cDir.isProperty("templateRoot"))
				templateRoot = findTemplateFile(cDir.getString("templateRoot"));
			if (cDir.isProperty("extensionOrder")) {
				extensionOrder = MConfig.toStringArray(cDir.getNode("extensionOrder").getNodes(), "value");
				MCollection.updateEach(extensionOrder, e -> "." + e );
			}
			if (cDir.isProperty("removeExtensions")) {
				removeExtensions = MConfig.toStringArray(cDir.getNode("removeExtensions").getNodes(), "value");
				MCollection.updateEach(removeExtensions, e -> "." + e );
			}
			if (cDir.isProperty("htmlExtensions")) {
				htmlExtensions = MConfig.toStringArray(cDir.getNode("htmlExtensions").getNodes(), "value");
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
			IConfig cEnv = cDir.getNode("environment");
			if (cEnv != null) {
				for (String key : cEnv.getPropertyKeys()) {
					environment.put(key, cEnv.get(key));
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
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
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
		
		sendError(context, HttpServletResponse.SC_NOT_FOUND, null);

	}

	@Override
	protected void doGetRequest(CallContext context) throws Exception {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(templateRoot, path);

		if (file.exists() && file.isDirectory()) {
			path = path + "/" + index;
			file = new File(templateRoot, path);
		}
		if (file.exists()) {
			if (file.isDirectory()) {
				log().d("deny directory",file);
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			}
			if (hasTransformExtension(path)) {
				log().d("deny TransformExtension",path);
				// path = MString.beforeLastIndex(path, '.');
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			} else {
				prepareHead(context,file, path);
				try {
					boolean isHtml = hasHtmlExtension(path);
					OutputStream os = context.getOutputStream();
					
					if (isHtml && htmlHeader != null) {
						doTransform(context, htmlHeader);
					}
					
					FileInputStream is = new FileInputStream(file);
					MFile.copyFile(is, os);
					is.close();

					if (isHtml && htmlFooter != null) {
						doTransform(context, htmlFooter);
					}

					os.flush();
					
				} catch (Throwable t) {
					log().w(file,t);
					sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
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
				prepareHead(context,file, orgPath);
				try {
					doTransform(context, file);
				} catch (Throwable t) {
					sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t);
				}
				return;
			}
		}
		log().d("file not found",path, file);
		sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
	}

	public boolean hasTransformExtension(String path) {
		for (String extension : extensionOrder) {
			if (path.endsWith(extension)) return true;
		}
		return false;
	}

	public boolean hasHtmlExtension(String path) {
		for (String extension : htmlExtensions) {
			if (path.endsWith(extension)) return true;
		}
		return false;
	}

	/**
	 * Transform file into response.
	 * 
	 * @param context
	 * @param from
	 * @throws Exception 
	 */
	protected void doTransform(CallContext context, File from) throws Exception {
		
		MProperties param = new MProperties(environment);
		param.put("session", context.getSession().pub());
		param.put("sessionId", context.getSessionId());
		param.put("request", context.getHttpRequest().getParameterMap());
		param.put("path", context.getHttpPath());
		
		OutputStream os = context.getOutputStream();
		TransformUtil.transform(from, os, getDocumentRoot(), null, null, param, null);
		os.flush();
	}

	protected void prepareHead(CallContext context, File from, String path) {
		String mimeType = getMimeType(path);
		HttpServletResponse resp = context.getHttpResponse();
		if (mimeType != null)
			resp.setContentType(mimeType);
		resp.setCharacterEncoding(charsetEncoding);
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
	public void sendError(CallContext context, int sc, Throwable t) {
		if (traceAccess)
			log().i(name,context.getHttpHost(),"error",context.getHttpRequest().getRemoteAddr(),context.getHttpMethod(),context.getHttpPath(),sc);
		if (traceErrors) {
			if (t == null) {
				try {
					throw new Exception();
				} catch (Exception ex) {
					t = ex;
				}
			}
			log().i(name,context.getHttpHost(),sc,t);
		}
		if (context.getHttpResponse().isCommitted()) {
			log().w("Can't send error to committed content",name,sc);
			return;
		}
		
		if (errorTemplate != null) {

			try {
				if (sc == 401)
					context.getHttpResponse().sendError(400); // send 400 instead of 401 - 401 will cause the browser to ask for a password
				else
					context.getHttpResponse().sendError(sc);
				MProperties param = new MProperties();
				param.put("session", context.getSession().pub());
				param.put("sessionId", context.getSessionId());
				param.put("request", context.getHttpRequest().getParameterMap());
				param.put("path", context.getHttpPath());
				param.put("error", sc);
				param.put("errorMsg", MSystem.HTTP_STATUS_CODES.getOrDefault(sc, ""));
				
				ServletOutputStream os = context.getHttpResponse().getOutputStream();
				TransformUtil.transform(errorTemplate, os, getDocumentRoot(), null, null, param, null);
				context.getHttpResponse().setContentType("text/html");
				context.getHttpResponse().setCharacterEncoding(charsetEncoding);
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

	@Override
	public File getTemplateRoot() {
		return templateRoot;
	}
	
	@Override
	public void doTransform(CallContext context, String template) throws Exception {
		template = MFile.normalizePath(template);
		File from = new File(templateRoot, template);
		doTransform(context, from);
	}

}
