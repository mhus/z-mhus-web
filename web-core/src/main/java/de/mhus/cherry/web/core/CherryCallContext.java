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
package de.mhus.cherry.web.core;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebSession;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.logging.MLogUtil;

public class CherryCallContext implements InternalCallContext {

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private String httpPath;
	private VirtualHost virtualHost;
	private Servlet httpServlet;
	private String sessionId;
	private String host;
	private OutputStream outputStream = null;
	private OutputStreamWriter writer;
	private String remoteIp;

	public CherryCallContext(Servlet servlet, HttpServletRequest req, HttpServletResponse res, VirtualHost vHost) {
		httpRequest = req;
		if (req == null) return;
		httpPath = req.getPathInfo();
		req.setAttribute(CallContext.REQUEST_ATTRIBUTE_NAME, this);
		sessionId = req.getSession().getId();
		host = req.getHeader("Host");
		
		httpResponse = res;
		outputStream = new HttpWrapperOutoutStream(res);
		
		httpServlet = servlet;
		virtualHost = vHost;
		remoteIp = req.getRemoteAddr();
	}

	@Override
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	@Override
	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	@Override
	public String getHttpPath() {
		return httpPath;
	}

	@Override
	public VirtualHost getVirtualHost() {
		return virtualHost;
	}

	@Override
	public Servlet getHttpServlet() {
		return httpServlet;
	}

	@Override
	public String getHttpMethod() {
		if (httpRequest.getParameter("_method") != null) return httpRequest.getParameter("_method").toLowerCase();
		return httpRequest.getMethod().toLowerCase();
	}

	@Override
	public WebSession getSession() {
		return CherryApiImpl.instance().getCherrySession(this, sessionId);
	}

	@Override
	public boolean isSession() {
		return CherryApiImpl.instance().isCherrySession(sessionId);
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		httpRequest.setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return httpRequest.getAttribute(name);
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, httpPath);
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String getHttpHost() {
		return host;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void setOutputStream(OutputStream os) {
		if (os != null)
			outputStream = os;
	}

	@Override
	public synchronized Writer getWriter() {
		if (writer == null) {
			try {
				writer = new OutputStreamWriter(outputStream, virtualHost.getCharsetEncoding());
			} catch (UnsupportedEncodingException e) {
				MLogUtil.log().e(e);
				return null;
			}
		}
		return writer;
	}

	@Override
	public String getRemoteIp() {
		return remoteIp;
	}

	@Override
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

}
