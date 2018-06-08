package de.mhus.cherry.web.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.errors.MException;

public interface CherryApi {

	CallContext getCurrentCall();

	String getMimeType(String file);

	CallContext createCallContext(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws MException;

}
