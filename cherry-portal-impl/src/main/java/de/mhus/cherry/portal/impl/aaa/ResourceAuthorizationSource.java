package de.mhus.cherry.portal.impl.aaa;

import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.core.security.AuthorizationSource;
import de.mhus.osgi.sop.api.aaa.AaaUtil;

public class ResourceAuthorizationSource extends MLog implements AuthorizationSource {

	private ResourceProvider provider;

	public ResourceAuthorizationSource(ResourceProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Boolean hasResourceAccess(Account account, String acl) {
		
		String space = "general";
		String name = acl;
		if (!acl.startsWith("_") && MString.isIndex(acl, '_')) {
			space = MString.beforeIndex(acl, '_');
			name = MString.afterIndex(acl, '_');
		}
		CaoNode res = provider.getResource("/acls/" + space + "/" + name + ".acl");
		if (res == null) {
			log().w("acl not found", acl, space, name);
			return null;
		}
		String aclString = res.getString("acl", null);
		if (aclString == null) return false;
		String[] aclParts = aclString.split(",");
		
		return AaaUtil.hasAccess(account, aclParts);
	}

}
