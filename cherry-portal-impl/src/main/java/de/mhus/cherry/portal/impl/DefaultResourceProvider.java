package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;

public class DefaultResourceProvider implements ResourceProvider {
	
	private CaoConnection con;
	
	public DefaultResourceProvider() {
	}
	
	public DefaultResourceProvider(CaoConnection con) {
		this.con = con;
	}

	@Override
	public CaoNode getResource(String path) {
		if (path.startsWith("/"))
			return con.getResourceByPath(path);
		else
			return con.getResourceById(path);
	}

	public CaoConnection getCon() {
		return con;
	}
	public void setCon(CaoConnection con) {
		this.con = con;
	}

	@Override
	public String getName() {
		return con.getName();
	}

}
