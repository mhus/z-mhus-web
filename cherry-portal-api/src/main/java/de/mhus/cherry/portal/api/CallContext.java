package de.mhus.cherry.portal.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;

public interface CallContext {

	HttpServletRequest getHttpRequest();

	HttpServletResponse getHttpResponse();

	String getHttpPath();

	CaoNode getNavigationResource();

	CaoNode getResource();

	VirtualHost getVirtualHost();

	HttpServlet getHttpServlet();

	String getReturnType();

	String[] getSelectors();

	String getHttpMethod();

}
