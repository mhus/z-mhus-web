package de.mhus.cherry.editor.impl;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinRequest;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.security.AccessControl;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.servlet.HttpServletRequestWrapper;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.lib.vaadin.servlet.VaadinRequestWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class UiAccessControl extends MLog implements AccessControl {

	private String sessionId;

	public UiAccessControl(VaadinRequest request) {
		sessionId = request.getWrappedSession().getId();
		
		if (!isUserSignedIn()) {
			// try auto login
			String host = request.getHeader("Host");
			VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
			AaaContext context = vHost.doLogin(new VaadinRequestWrapper(request), null);
			if (context != null) {
				IProperties session = Sop.getApi(InternalCherryApi.class).getCherrySession(sessionId);
				session.put(InternalCherryApi.SESSION_ACCESS_NAME, context);
			}
		}
	}
	
	@Override
	public boolean signIn(String username, String password) {
		try {
			InternalCherryApi internal = Sop.getApi(InternalCherryApi.class);
			String ret = internal.doLogin(username, password);
			return ret == null;
		} catch (Throwable t) {
			log().d(username,t);
			return false;
		}
	}

	@Override
	public boolean isUserSignedIn() {
		try {
			InternalCherryApi internal = Sop.getApi(InternalCherryApi.class);
			return internal.isLoggedIn();
		} catch (Throwable t) {
			log().d(t);
			return false;
		}
	}

	@Override
	public boolean hasGroup(String role) {
		try {
			AaaContext current = Sop.getApi(CherryApi.class).getCurrentCall().getAaaContext();
			return current.getAccount().hasGroup(role);
		} catch (Throwable t) {
			log().d(t);
			return false;
		}
	}

	@Override
	public String getName() {
		try {
			AaaContext current = Sop.getApi(CherryApi.class).getCurrentCall().getAaaContext();
			return current.getAccount().getName();
		} catch (Throwable t) {
			log().d(t);
			return null;
		}
	}

	@Override
	public void signOut() {
		try {
			Sop.getApi(InternalCherryApi.class).doLogout();
		} catch (Throwable t) {
			log().d(t);
		}
	}

	@Override
	public Account getAccount() {
		try {
			AaaContext current = Sop.getApi(CherryApi.class).getCurrentCall().getAaaContext();
			return current.getAccount();
		} catch (Throwable t) {
			log().d("getAccount",t.toString());
			return null;
		}
	}

}
