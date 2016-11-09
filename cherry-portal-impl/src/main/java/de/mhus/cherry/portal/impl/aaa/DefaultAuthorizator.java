package de.mhus.cherry.portal.impl.aaa;

import java.util.List;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoAspect;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.auth.Authorizator;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AaaUtil;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class DefaultAuthorizator implements Authorizator {

	@Override
	public boolean hasReadAccess(CaoNode node) {
		try {
			String acl = Sop.getApi(CherryApi.class).getRecursiveString(node, "acl:read");
			if (acl == null) return true;
			AccessApi aaa = Sop.getApi(AccessApi.class);
			AaaContext context = aaa.getCurrentOrGuest();
			String[] list = acl.split("\\|");
			return AaaUtil.hasAccess(context.getAccount(), list);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasWriteAccess(CaoNode node) {
		try {
			String acl = Sop.getApi(CherryApi.class).getRecursiveString(node, "acl:write");
			if (acl == null) return true;
			AccessApi aaa = Sop.getApi(AccessApi.class);
			AaaContext context = aaa.getCurrentOrGuest();
			List<String> list = MCollection.toList(acl.split("\\|"));
			return AaaUtil.hasAccess(context.getAccount(), list);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasActionAccess(CaoAction action) {
		return true;
	}

	@Override
	public boolean hasReadAccess(CaoNode node, String name) {
		return true;
	}

	@Override
	public boolean hasContentAccess(CaoNode node, String rendition) {
		return true;
	}

	@Override
	public boolean hasWriteAccess(CaoNode node, String name) {
		return true;
	}

	@Override
	public boolean hasAspectAccess(CaoNode node, Class<? extends CaoAspect> ifc) {
		return true;
	}

}
