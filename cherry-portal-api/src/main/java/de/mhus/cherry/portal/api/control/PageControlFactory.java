package de.mhus.cherry.portal.api.control;

import de.mhus.lib.basics.Named;

public interface PageControlFactory extends Named {

	@Override
	String getName();
	PageControl createPageControl();
	
}
