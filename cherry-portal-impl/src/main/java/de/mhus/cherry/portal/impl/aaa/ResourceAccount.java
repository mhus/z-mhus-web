package de.mhus.cherry.portal.impl.aaa;

import java.util.HashSet;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.security.Account;

public class ResourceAccount extends MLog implements Account {

	private CaoNode res;
	private HashSet<String> groups = new HashSet<>();
	private String name;
	
	public ResourceAccount(CaoNode res) {
		this.res = res;
		this.name = res.getName();
		try {
			for (CaoNode n : res.getNode("groups").getNodes()) {
				groups.add(n.getName());
			}
		} catch (Throwable t) {
			log().d(name, t);
		}
	}

	@Override
	public boolean hasGroup(String role) {
		return groups.contains(role);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid() {
		return res.getBoolean("enabled", false);
	}

	@Override
	public boolean validatePassword(String password) {
		return MPassword.validatePassword(password, res.getString("password", null));
	}

	@Override
	public boolean isSyntetic() {
		return false;
	}

	@Override
	public String getDisplayName() {
		return res.getString("title", name);
	}

}
