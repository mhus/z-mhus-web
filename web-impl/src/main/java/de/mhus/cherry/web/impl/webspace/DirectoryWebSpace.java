package de.mhus.cherry.web.impl.webspace;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class DirectoryWebSpace extends AbstractWebSpace {

	String characterEncoding = "utf-8";
	private IConfig cDir;
	private String[] indexes = new String[] {"index.html"};
	
	@Override
	public void start(CherryApi api) throws MException {
		super.start(api);
		cDir = getConfig().getNode("directory");
		if (cDir != null) {
			characterEncoding = cDir.getString("characterEncoding", null);
			if (cDir.isProperty("indexes"))
				indexes = cDir.getString("indexes").split(",");
		}
	}

	@Override
	protected void doDeleteRequest(CallContext context) {
	}

	@Override
	protected void doPutRequest(CallContext context) {
	}

	@Override
	protected void doPostRequest(CallContext context) {
	}

	@Override
	protected void doHeadRequest(CallContext context) {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(getDocumentRoot(), path);
		if (!file.exists()) {
			sendError(context, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (file.isDirectory()) {
			file = findIndex(file);
			if (file == null) {
				//TODO support directory indexing ?
				sendError(context, HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		prepareHead(context,file);
		
	}

	@Override
	protected void doGetRequest(CallContext context) {
		String path = context.getHttpPath();
		path = MFile.normalizePath(path);
		File file = new File(getDocumentRoot(), path);
		if (!file.exists()) {
			sendError(context, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (file.isDirectory()) {
			file = findIndex(file);
			if (file == null) {
				//TODO support directory indexing ?
				sendError(context, HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		prepareHead(context,file);
		
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
	}

	protected File findIndex(File dir) {
		
		for (String index : indexes) {
			File file = new File(dir, index);
			if (file.exists() && file.isFile()) 
				return file;
		}
		
		
		return null;
	}

	protected void prepareHead(CallContext context, File file) {
		String mimeType = getMimeType(file.getName());
		HttpServletResponse resp = context.getHttpResponse();
		if (mimeType != null)
			resp.setContentType(mimeType);
		resp.setContentLengthLong(file.length());
		resp.setCharacterEncoding(characterEncoding);
		resp.setHeader("Last-Modified", MDate.toHttpHeaderDate(file.lastModified()));
	}

}
