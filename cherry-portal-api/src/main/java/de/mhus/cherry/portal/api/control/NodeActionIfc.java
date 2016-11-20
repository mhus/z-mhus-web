package de.mhus.cherry.portal.api.control;

import de.mhus.lib.cao.CaoNode;

public interface NodeActionIfc {

	boolean canExecute(CaoNode node);
	
	void doExecute(CaoNode node, ActionFeedback feedback);
	
}
