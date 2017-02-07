package de.mhus.cherry.portal.api;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AaaUtil;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public abstract class AuthResourceRenderer extends MLog implements ResourceRenderer {

	private String[] acl;

	@Override
	public final void doRender(CallContext call) throws Exception {
		String[] acl = getAcl();
		if (acl != null) {
			AaaContext context = Sop.getApi(AccessApi.class).getCurrentOrGuest();
			if (!AaaUtil.hasAccess(context.getAccount(), acl)) {
				call.getVirtualHost().sendError(call, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		doRender2(call);
	}

	public abstract void doRender2(CallContext call) throws Exception;

	protected String[] getAcl() {
		return acl;
	}
	
	public void setAcl(String[] acl) {
		this.acl = acl;
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
