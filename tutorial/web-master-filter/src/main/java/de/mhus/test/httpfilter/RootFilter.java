package de.mhus.test.httpfilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import aQute.bnd.annotation.component.Component;

@Component(properties="urlPatterns=/*",immediate=true)
public class RootFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("F I");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("F: " + ((HttpServletRequest)request).getRequestURI() );
		chain.doFilter(request, response);
	}

	public void destroy() {
		System.out.println("F D");
	}

}
