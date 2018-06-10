package de.mhus.cherry.web.impl.webspace;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class SopSessionFilter extends MLog implements WebFilter {

	public static final String SESSION_PARAMETER_NAME = "__sop_user_ticket";
	public static final String CONTEXT_PARAMETER_AAA_CONTEXT = "__sop_aaa_context";

	@Override
	public boolean doFilterBegin(InternalCallContext call) throws MException {
		try {
			HttpServletRequest req = call.getHttpRequest();
			
	
			String authHeader = req.getHeader("authorization");
			if (authHeader != null) {
				String encodedValue = authHeader.split(" ")[1];
				String decodedValue = Base64.decodeToString(encodedValue);
				log().i(decodedValue);
			}
			
			AccessApi aaa = MApi.lookup(AccessApi.class);
			
			String userTicket = call.getSession().getString(SESSION_PARAMETER_NAME,null);
			if (userTicket == null) return true; // guest?
			
			Locale locale = req.getLocale();
			AaaContext userContext = aaa.process(userTicket, locale);
			call.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, userContext);
			
		} catch (Throwable t) {
			throw new MException(t);
		}
		return true;
	}

	@Override
	public void doFilterEnd(InternalCallContext call) throws MException {
		AaaContext userContext = (AaaContext) call.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
		if (userContext == null) return;
	
		AccessApi aaa = MApi.lookup(AccessApi.class);
		aaa.release(userContext);
	}

	public static boolean isLoggedIn(CallContext context) {
		AaaContext userContext = (AaaContext) context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
		return userContext != null;
	}
	
	public static void login(CallContext context, String user, String pass) throws MException {
		
		AaaContext userContext = (AaaContext) context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
		if (userContext != null) throw new MException("already logged in",userContext.getAccountId());

		AccessApi aaa = MApi.lookup(AccessApi.class);
		
		String userTicket = aaa.createUserTicket(user, pass);
		context.getSession().setString(SESSION_PARAMETER_NAME, userTicket);

		Locale locale = context.getHttpRequest().getLocale();
		userContext = aaa.process(userTicket, locale);
		context.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, userContext);
	}

	public static void logout(CallContext context, String user, String pass) {
		
		AaaContext userContext = (AaaContext) context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
		if (userContext == null) return;
	
		AccessApi aaa = MApi.lookup(AccessApi.class);
		aaa.release(userContext);

		context.getSession().remove(SESSION_PARAMETER_NAME);
		context.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, null);
		
	}

}
