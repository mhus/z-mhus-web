package de.mhus.osgi.cherry.api.central;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CentralRequestHandler {

	boolean doHandleBefore(CentralCallContext context)
			throws IOException, ServletException;
	
	void doHandleAfter(CentralCallContext context)
			throws IOException, ServletException;

	boolean isEnabled();

	double getSortHint();
	
}
