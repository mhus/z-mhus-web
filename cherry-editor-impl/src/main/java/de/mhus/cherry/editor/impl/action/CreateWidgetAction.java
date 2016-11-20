package de.mhus.cherry.editor.impl.action;

import de.mhus.cherry.portal.api.control.ActionFeedback;
import de.mhus.cherry.portal.api.control.NodeActionIfc;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;

public class CreateWidgetAction extends MLog implements NodeActionIfc {

	@Override
	public boolean canExecute(CaoNode node) {
		return false;
	}

	@Override
	public void doExecute(CaoNode node, ActionFeedback feedback) {
		
	}

}
