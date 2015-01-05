package de.mhus.osgi.cherry.service;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;

/**
 * If you reach this servlet, there is no content.
 * It's only a placeholder to allow triggering central request handler.
 * 
 * @author mikehummel
 *
 */
@Component(properties = "alias=/", immediate=true)
public class RootServlet implements Servlet {

	public void init(ServletConfig config) throws ServletException {
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
			((HttpServletResponse)res).sendError(404);
	}

	public String getServletInfo() {
		return null;
	}

	public void destroy() {
	}

}
