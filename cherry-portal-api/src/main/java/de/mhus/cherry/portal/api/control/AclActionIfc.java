package de.mhus.cherry.portal.api.control;

import de.mhus.cherry.portal.api.Acl;

public interface AclActionIfc {

	boolean canExecute(Acl acl);
	
	void doExecute(Acl acl, ActionFeedback feedback);
	
}
