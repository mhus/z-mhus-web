package de.mhus.cherry.web.javaxt;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.core.CherryApiImpl;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.system.IApi;
import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

public class MainServlet extends HttpServlet {
	// http://www.javaxt.com/javaxt-server/SSL_Support/
	public MainServlet(IConfig config) throws Exception {
		if (config.getBoolean("ssl", false)) {
			setKeyStore(new java.io.File(config.getString("keystore", MApi.getFile(IApi.SCOPE.ETC, "keystore.jks").getPath())), config.getString("keystorePassword","password"));
	        setTrustStore(new java.io.File(config.getString("truststore",MApi.getFile(IApi.SCOPE.ETC, "truststore.jks").getPath())), config.getString("truststorePassword","password"));
		}
	}
	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		InternalCallContext call = null;
		
		try {
			
			call = CherryApiImpl.instance().createCallContext(this, (javax.servlet.http.HttpServletRequest)request, (javax.servlet.http.HttpServletResponse)response);
			if (call == null) {
				sendNotFoundError((HttpServletResponse)response);
				return;
			}
			
			call.getVirtualHost().doRequest(call);
			call.getHttpResponse().getOutputStream().flush();
		} catch (Throwable t) {
			MLogUtil.log().w(t);
			sendInternalError((HttpServletResponse)response,t);
		}
		
	}

	private void sendNotFoundError(HttpServletResponse response) {
		if (response.isCommitted()) return; // can't send error any more
		try {
			response.sendError(404);
		} catch (IOException e) {
		}
	}

	private void sendInternalError(HttpServletResponse response, Throwable t) {
		if (response.isCommitted()) return; // can't send error any more
		try {
			response.sendError(500, t.getMessage());
		} catch (IOException e) {
		}
	}

}
