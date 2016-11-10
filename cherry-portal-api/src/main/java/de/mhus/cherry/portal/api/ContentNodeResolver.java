package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface ContentNodeResolver {

	CaoNode doResolve(CaoNode nav);

}
