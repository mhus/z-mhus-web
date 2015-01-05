package de.mhus.test.httpfilter;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;

@Component(properties = "alias=/", immediate=true)
public class RootServlet implements Servlet {

	public void init(ServletConfig config) throws ServletException {
		System.out.println("S I");
	}

	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		System.out.println("S: " + ((HttpServletRequest)req).getRequestURI() );
		
		if (((HttpServletRequest)req).getRequestURI() != null && ((HttpServletRequest)req).getRequestURI().endsWith(".txt")) {
			((HttpServletResponse)res).sendError(404);
		}
	}

	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroy() {
		System.out.println("S D");
		
	}

}
