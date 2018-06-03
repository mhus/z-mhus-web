package de.mhus.cherry.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CallContext {

	HttpServletRequest getRequest();
	HttpServletResponse getResponse();
	
	WebSpace getWebSpace();
	
}
