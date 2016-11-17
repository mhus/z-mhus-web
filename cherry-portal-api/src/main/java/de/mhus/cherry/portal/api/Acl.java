package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public class Acl {

	private CaoNode defining;
	private String acl;
	private String name;

	public Acl(CaoNode defining, String name, String acl) {
		this.defining = defining;
		this.acl = acl;
		this.name = name;
	}
	
	public CaoNode getDefiningNode() {
		return defining;
	}
	
	public String getAcl() {
		return acl;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getAces() {
		return acl.split(",");
	}
}
