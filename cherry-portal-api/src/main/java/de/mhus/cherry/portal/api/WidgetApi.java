package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.sop.api.SApi;

public interface WidgetApi extends SApi {

	void doRender(CallContext call, ResourceNode widget) throws Exception;

	CaoNode getResource(CallContext call);

}
