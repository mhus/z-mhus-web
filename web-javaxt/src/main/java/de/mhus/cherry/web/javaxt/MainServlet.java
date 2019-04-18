/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.javaxt;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.core.CherryApiImpl;
import de.mhus.lib.core.M;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

public class MainServlet extends HttpServlet {
	// http://www.javaxt.com/javaxt-server/SSL_Support/
	public MainServlet(IConfig config) throws Exception {
		if (config.getBoolean("ssl", false)) {
			setKeyStore(new java.io.File(config.getString("keystore", MApi.getFile(MApi.SCOPE.ETC, "keystore.jks").getPath())), config.getString("keystorePassword","password"));
	        setTrustStore(new java.io.File(config.getString("truststore",MApi.getFile(MApi.SCOPE.ETC, "truststore.jks").getPath())), config.getString("truststorePassword","password"));
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
