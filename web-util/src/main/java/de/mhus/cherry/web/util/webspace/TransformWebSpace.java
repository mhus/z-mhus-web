package de.mhus.cherry.web.util.webspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CanTransform;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.util.CherryWebUtil;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.core.crypt.MRandom;
import de.mhus.lib.core.io.http.MHttp;
import de.mhus.lib.core.util.SoftHashMap;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.TransformUtil;

public class TransformWebSpace extends AbstractWebSpace implements CanTransform {

	protected IConfig cDir;
	protected String index = "index";
	protected String[] extensionOrder = new String[] { ".twig" };
	protected String[] removeExtensions = new String[] { ".html", ".htm" };
	protected String[] htmlExtensions = new String[] { ".html", ".htm" };
    protected String[] denyExtensions = new String[] { ".cfg" };
    protected String cfgExtension = ".cfg";
	protected File templateRoot;
	protected File errorTemplate = null;
	protected MProperties environment = null;
    private boolean csrfEnabled;
    private int stamp = 0;
    private SoftHashMap<File, MProperties> cfgCache = new SoftHashMap<>();
    private MProperties cfgDefault = new MProperties();
    private String htmlHeader;
    private String htmlFooter;
	
	@Override
	public void start(CherryApi api) throws MException {
	    
	    MRandom rnd = MApi.lookup(MRandom.class);
	    stamp = rnd.getInt();
	    
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
				MCollection.updateEach(extensionOrder, e -> "." + e.toLowerCase() );
			}
            if (cDir.isProperty("denyExtensions")) {
                denyExtensions = MConfig.toStringArray(cDir.getNode("denyExtensions").getNodes(), "value");
                MCollection.updateEach(denyExtensions, e -> "." + e.toLowerCase() );
            }
			if (cDir.isProperty("removeExtensions")) {
				removeExtensions = MConfig.toStringArray(cDir.getNode("removeExtensions").getNodes(), "value");
				MCollection.updateEach(removeExtensions, e -> "." + e.toLowerCase() );
			}
			if (cDir.isProperty("htmlExtensions")) {
				htmlExtensions = MConfig.toStringArray(cDir.getNode("htmlExtensions").getNodes(), "value");
				MCollection.updateEach(htmlExtensions, e -> "." + e.toLowerCase() );
			}
			if (cDir.isProperty("header")) {
				String header = cDir.getString("header");
				File htmlHeaderF = new File(getDocumentRoot(), header);
				if (!htmlHeaderF.exists()) {
					log().w("ignore html header",htmlHeaderF.getAbsolutePath());
				} else
				    htmlHeader = htmlHeaderF.getAbsolutePath();
			}
			if (cDir.isProperty("footer")) {
				String footer = cDir.getString("footer");
				File htmlFooterF = new File(getDocumentRoot(), footer);
				if (!htmlFooterF.exists()) {
					log().w("ignore html footer",htmlFooterF.getAbsolutePath());
				} else
				    htmlFooter = htmlFooterF.getAbsolutePath();
			}
			if (cDir.isProperty("error")) {
				String error = cDir.getString("error");
				errorTemplate = new File(getDocumentRoot(), error);
				if (!errorTemplate.exists()) {
					log().w("ignore error template",errorTemplate.getAbsolutePath());
				}
			}
			if (cDir.containsKey("cfgExtension"))
			    cfgExtension = "." + cDir.getString("cfgExtension").toLowerCase();
			
			csrfEnabled = cDir.getBoolean("csrfEnabled", true);
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
		String orgPath = path;
		String lowerPath = path.toLowerCase();
		if (file.exists()) {
			if (file.isDirectory()) {
				path = path + "/" + index;
			} else
			if (hasTransformExtension(lowerPath)) {
				// path = MString.beforeLastIndex(path, '.');
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			} else {
				prepareHead(context,file, path);
				return;
			}
		}
		// deny ?
        for (String extension : denyExtensions) {
            if (lowerPath.endsWith(extension)) {
                sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
                return;
            }
        }		
		// find template
		for (String extension : removeExtensions) {
			if (lowerPath.endsWith(extension)) {
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
		String lowerPath = path.toLowerCase();
        // deny ?
        for (String extension : denyExtensions) {
            if (lowerPath.endsWith(extension)) {
                sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
                return;
            }
        }

		if (file.exists()) {
		    
		    IReadProperties fileConfig = findConfig(file);
		    
			if (file.isDirectory()) {
				log().d("deny directory",file);
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			}
			if (hasTransformExtension(lowerPath)) {
				log().d("deny TransformExtension",path);
				// path = MString.beforeLastIndex(path, '.');
				sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			} else {
				prepareHead(context,file, path);
				try {
					boolean isHtml = hasHtmlExtension(path);
					OutputStream os = context.getOutputStream();
					
					String htmlHeaderLocal = fileConfig.getString("htmlHeader", htmlHeader);
					if (isHtml && MString.isSet(htmlHeaderLocal)) {
						doTransform(context, new File(htmlHeaderLocal), null);
					}
					
					String transformType = fileConfig.getString("transform", null);
					if (MString.isSet(transformType)) {
                        doTransform(context, file, null);
					} else {
    					FileInputStream is = new FileInputStream(file);
    					MFile.copyFile(is, os);
    					is.close();
					}
					
					String htmlFooterLocal = fileConfig.getString("htmlFooter", htmlFooter);
					if (isHtml && MString.isSet(htmlFooterLocal)) {
						doTransform(context, new File(htmlFooterLocal), null);
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
					doTransform(context, file, null);
				} catch (Throwable t) {
					sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t);
				}
				return;
			}
		}
		log().d("file not found",path, file);
		sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
	}

	public  IReadProperties findConfig(CallContext context) {
        String path = context.getHttpPath();
        path = MFile.normalizePath(path);
        File file = new File(templateRoot, path);
        String lowerPath = path.toLowerCase();
        // deny ?
        for (String extension : denyExtensions) {
            if (lowerPath.endsWith(extension)) {
                return null;
            }
        }
        if (file.exists() && file.isDirectory()) {
            path = path + "/" + index;
            file = new File(templateRoot, path);
        }
        if (file.exists()) {
            if (hasTransformExtension(lowerPath))
                return null;
            return findConfig(file);
        }
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
                return findConfig(file);
            }
        }

        return null;
	}
	
	private IReadProperties findConfig(File file) {
	    File cfgFile = new File(file, cfgExtension);
	    if (cfgFile.exists()) {
	        MProperties out = cfgCache.get(cfgFile);
	        if (    out == null 
	                || 
	                out.getLong("_cfg_modified", 0) != cfgFile.lastModified() 
	                || 
	                out.getLong("_cfg_size", 0) != cfgFile.length()
	            ) {
	            out = MProperties.load(cfgFile);
	            out.setLong("_modified", cfgFile.lastModified());
	            out.setLong("_size", cfgFile.length());
	            cfgCache.put(cfgFile,out);
	        }
	        return out;
	    }
        return cfgDefault;
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
	protected void doTransform(CallContext context, File from, String type) throws Exception {
		
		MProperties param = new MProperties(environment);
        param.put("stamp", stamp);
		param.put("session", context.getSession().pub());
		param.put("sessionId", context.getSessionId());
		param.put("request", context.getHttpRequest().getParameterMap());
		param.put("path", context.getHttpPath());
		if (csrfEnabled)
		    param.put("csrfToken", CherryWebUtil.createCsrfToken(context));
		
		OutputStream os = context.getOutputStream();
		TransformUtil.transform(from, os, getDocumentRoot(), null, null, param, type);
		os.flush();
	}

	protected void prepareHead(CallContext context, File from, String path) {
		HttpServletResponse resp = context.getHttpResponse();
		resp.setCharacterEncoding(charsetEncoding);
		resp.setHeader("Last-Modified", MDate.toHttpHeaderDate(from.lastModified()));
		super.prepareHead(context, MFile.getFileSuffix(from), path);
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
			log().d(name,context.getHttpHost(),"error",context.getHttpRequest().getRemoteAddr(),context.getHttpMethod(),context.getHttpPath(),sc);
		if (traceErrors) {
			if (t == null) {
				try {
					throw new Exception();
				} catch (Exception ex) {
					t = ex;
				}
			}
			log().d(name,context.getHttpHost(),sc,t);
		}
		if (context.getHttpResponse().isCommitted()) {
			log().w("Can't send error to committed content",name,sc);
			return;
		}
		
		if (errorTemplate != null) {

			try {
				context.getHttpResponse().setStatus(sc);
				MProperties param = new MProperties();
				param.put("stamp", stamp);
				param.put("session", context.getSession().pub());
				param.put("sessionId", context.getSessionId());
				param.put("request", context.getHttpRequest().getParameterMap());
				param.put("path", context.getHttpPath());
				if (csrfEnabled)
				    param.put("csrfToken", CherryWebUtil.createCsrfToken(context));
				param.put("error", sc);
				param.put("errorMsg", MHttp.HTTP_STATUS_CODES.getOrDefault(sc, ""));
				
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
		doTransform(context, from, null);
	}

}
