package de.mhus.cherry.portal.api;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.IProperties;
import de.mhus.osgi.sop.api.SApi;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface InternalCherryApi extends SApi {

	public static final String SESSION_ACCESS_USERNAME = "_cherry_username";
	public static final String SESSION_ACCESS_PASSWORD = "_cherry_password";
	public static final String SESSION_ACCESS_NAME = "_cherry_access";
	public static final String SESSION_DESTROY_ON_RELEASE = "_destroy_session_on_release";

	CallContext createCall(HttpServlet servlet, HttpServletRequest req, HttpServletResponse res) throws IOException;

	void releaseCall(CallContext call);

	void setCallContext(CallContext callContext);

//	IProperties getCherrySession(RequestWrapper request);
	IProperties getCherrySession(String sessionId);
	
	AaaContext getContext(String sessionId);

	String doLogin(String username, String password);

	String doLogout();

	boolean isLoggedIn();

	IProperties getBundleStore(Bundle bundle);

}
