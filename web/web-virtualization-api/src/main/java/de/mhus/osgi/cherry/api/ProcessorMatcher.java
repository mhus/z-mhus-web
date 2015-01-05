package de.mhus.osgi.cherry.api;

import java.util.LinkedList;
import java.util.regex.Pattern;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;

public class ProcessorMatcher {
	
	private LinkedList<Pattern> filters = new LinkedList<>();
	private String processor;
	
	public boolean matches(ResourceNode res) throws MException {
		
		String name = res.getName();
		for (Pattern filter : filters)
			if (filter.matcher(name).matches()) return true;
		return false;
	}

	public void addFilter(String filter) {
		filters.add( Pattern.compile(filter) );
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}
	
}
