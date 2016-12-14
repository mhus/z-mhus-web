package de.mhus.cherry.portal.impl.aaa;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoAspect;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.auth.Authorizator;
import de.mhus.lib.core.IProperties;
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
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_READ );
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasWriteAccess(CaoNode node) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_WRITE);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasActionAccess(CaoAction action) {
		if (action == null || action.getName() == null) return false;
		return true;
	}

	@Override
	public boolean hasReadAccess(CaoNode node, String name) {
		return true;
	}

	@Override
	public boolean hasStructureAccess(CaoNode node) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_STRUCTURE);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasDeleteAccess(CaoNode node) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_DELETE);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasCreateAccess(CaoNode node, String name, IProperties properties) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_CREATE);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean hasContentAccess(CaoNode node, String rendition) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_READ);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasContentWriteAccess(CaoNode node, String rendition) {
		try {
			return Sop.getApi(CherryApi.class).hasResourceAccess(node, CherryApi.ACL_RENDITION);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean hasWriteAccess(CaoNode node, String name) {
		return hasWriteAccess(node);
	}

	@Override
	public boolean hasAspectAccess(CaoNode node, Class<? extends CaoAspect> ifc) {
		return false;
	}

	@Override
	public String mapReadName(CaoNode node, String name) {
		return name;
	}

	@Override
	public String mapReadRendition(CaoNode node, String rendition) {
		return rendition;
	}

	@Override
	public Collection<String> mapReadNames(CaoNode node, Collection<String> set) {
		return set;
	}

	@Override
	public String mapWriteName(CaoNode node, String name) {
		return name;
	}

	@Override
	public boolean hasActionAccess(CaoConfiguration configuration, CaoAction action) {
		if (action == null || action.getName() == null || configuration == null) return false;
		
		CaoList list = configuration.getList();
		for (CaoNode n : list) {
			switch (action.getName()) {
			case CaoAction.CREATE:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_CREATE )) return false;
				break;
			case CaoAction.DELETE:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_DELETE )) return false;
				break;
			case CaoAction.DELETE_RENDITION:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_RENDITION )) return false;
				break;
			case CaoAction.MOVE:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_STRUCTURE )) return false;
				break;
			case CaoAction.RENAME:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_STRUCTURE )) return false;
				break;
			case CaoAction.UPDATE:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_WRITE )) return false;
				break;
			case CaoAction.UPLOAD_RENDITION:
				if (!Sop.getApi(CherryApi.class).hasResourceAccess(n, CherryApi.ACL_RENDITION )) return false;
				break;
			}
		}
		return true;
	}

}
