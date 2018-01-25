package de.mhus.cherry.portal.impl.aaa;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.core.security.AuthorizationSource;
import de.mhus.osgi.sop.api.Sop;

public class CherryAuthorizationSourceDelegate implements AuthorizationSource {

	@Override
	public Boolean hasResourceAccess(Account account, String acl) {
		CallContext call = MApi.lookup(CherryApi.class).getCurrentCall();
		if (call == null)
			return null;
		
		VirtualHost vHost = call.getVirtualHost();
		if (vHost == null)
			return null;
		
		return vHost.getAuthorizationSource().hasResourceAccess(account, acl);

	}

}
