package de.mhus.cherry.web.api;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.errors.MException;

public interface CherryApi {

	CallContext getCurrentCall();

	String getMimeType(String file);

	CallContext createCallContext(Servlet servlet, HttpServletRequest request, HttpServletResponse response) throws MException;

}
