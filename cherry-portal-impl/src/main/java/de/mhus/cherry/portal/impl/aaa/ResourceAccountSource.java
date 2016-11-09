package de.mhus.cherry.portal.impl.aaa;

import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.core.security.AccountSource;

public class ResourceAccountSource extends MLog implements AccountSource {

	private ResourceProvider provider;
	public ResourceAccountSource(ResourceProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Account findAccount(String account) {
		
		CaoNode res = provider.getResource("/accounts/" + account.substring(0,1) + "/" + account + ".account");
		if (res == null) {
			log().d("account not found", account);
			return null;
		}
		
		return new ResourceAccount(res);
	}

}
