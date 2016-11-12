package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface ActionCallback {

	void doAction(CallContext call, CaoNode widget);

}
