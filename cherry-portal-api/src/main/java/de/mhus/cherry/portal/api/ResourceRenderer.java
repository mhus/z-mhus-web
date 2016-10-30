package de.mhus.cherry.portal.api;

import java.util.Set;

public interface ResourceRenderer {

	String RESOURCE_JAVASCRIPT = "js";
	String RESOURCE_CSS = "css";

	void doRender(CallContext call) throws Exception;

	void doCollectResourceLinks(String name, Set<String> list);

		
}
