package de.mhus.cherry.portal.impl.aaa;

import de.mhus.lib.cao.CaoNode;

public class ReadAllAuthorizator extends DefaultAuthorizator {

	@Override
	public boolean hasReadAccess(CaoNode node) {
		return true;
	}
	
}
