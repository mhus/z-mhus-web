package de.mhus.cherry.portal.impl.aaa;

import java.util.HashSet;
import java.util.Map.Entry;

import de.mhus.lib.basics.IsNull;
import de.mhus.lib.cao.CaoActionStarter;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.errors.NotSupportedException;

public class ResourceAccount extends MLog implements Account {

	private CaoNode res;
	private HashSet<String> groups = new HashSet<>();
	private String name;
	private MProperties attributes = new MProperties();
	
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
		
		try {
			for (String name : res.getPropertyKeys()) {
				attributes.setString(name, res.getString(name) );
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

	@Override
	public IReadProperties getAttributes() {
		return attributes;
	}

	@Override
	public void putAttributes(IReadProperties properties) throws NotSupportedException {
		for (Entry<String, Object> entry : properties.entrySet()) {
			if (entry.getKey().startsWith("_")) continue;
			if (entry.getValue() instanceof IsNull)
				attributes.remove(entry.getKey());
			else
				attributes.put(entry.getKey(), entry.getValue());
		}
		doSave();
	}

	protected void doSave() {
		try {
			CaoWritableElement w = res.getWritableNode();
			w.putAll(attributes);
			CaoActionStarter action = w.getUpdateAction();
			action.doExecute(null);
		} catch (Throwable t) {
			log().w(res.getPath(), t.toString());
			throw new NotSupportedException(t);
		}
	}

	@Override
	public String toString() {
		return name;
	}
	
}
