package de.mhus.cherry.web.impl.webspace;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.system.IApi;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.TransformUtil;

public class TransformWebSpace extends AbstractWebSpace {

	String characterEncoding = "utf-8";
	private IConfig cDir;
	private String index = "index.html";
	private String[] extensionOrder = new String[] { "twig" };
	private String[] removeExtensions = new String[] { ".html", ".htm" };

	@Override
	public void start(CherryApi api) throws MException {
		super.start(api);
		cDir = getConfig().getNode("transform");
		if (cDir != null) {
			characterEncoding = cDir.getString("characterEncoding", null);
			if (cDir.isProperty("indexe"))
				index = cDir.getString("index");
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doGetRequest(CallContext context) throws Exception {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(getDocumentRoot(), path);
		if (!file.exists()) {
			sendError(context, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
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
				try {
					FileInputStream is = new FileInputStream(file);
					ServletOutputStream os = context.getHttpResponse().getOutputStream();
					MFile.copyFile(is, os);
					os.close();
					is.close();
				} catch (Throwable t) {
					log().w(file,t);
					sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				return;
			}
		}
		
		// find template
		for (String extension : removeExtensions) {
			if (path.endsWith(extension)) {
				path = path.substring(0, path.length()-extension.length());
				break;
			}
		}
		
		for (String extension : extensionOrder) {
			String p = path + "." + extension;
			file = new File(p);
			if (file.exists() && file.isFile()) {
				doTransform(context, file, path);
				return;
			}
		}
		
	}

	private boolean hasTransformExtension(String path) {
		for (String extension : extensionOrder) {
			if (path.endsWith("." + extension)) return true;
		}
		return false;
	}

	private void doTransform(CallContext context, File from, String path) {
		MProperties param = new MProperties();
		File to = MApi.getFile(IApi.SCOPE.TMP, "");
		try {
			TransformUtil.transform(from, to, getDocumentRoot(), null, null, param, null);
			
			FileInputStream is = new FileInputStream(to);
			ServletOutputStream os = context.getHttpResponse().getOutputStream();
			MFile.copyFile(is, os);
			os.flush();
			is.close();
			
		} catch (Exception e) {
			log().e(alias,from,e);
		} finally {
			to.delete();
		}
		
	}

	protected void prepareHead(CallContext context, File file, String path) {
		String mimeType = context.getMimeType(file.getName());
		HttpServletResponse resp = context.getHttpResponse();
		if (mimeType != null)
			resp.setContentType(mimeType);
		resp.setContentLengthLong(file.length());
		resp.setCharacterEncoding(characterEncoding);
		resp.setHeader("Last-Modified", MDate.toHttpHeaderDate(file.lastModified()));
	}

}
