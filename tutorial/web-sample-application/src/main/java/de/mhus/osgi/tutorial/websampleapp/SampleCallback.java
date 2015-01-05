package de.mhus.osgi.tutorial.websampleapp;

import java.util.LinkedList;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.lib.portlet.callback.AbstractAjaxCallback;
import de.mhus.lib.portlet.callback.CallContext;

public class SampleCallback extends AbstractAjaxCallback {

	private SampleContext sampleContext;
	private LinkedList<WSEntity> list = new LinkedList<>();

	public SampleCallback(SampleContext sampleContext) {
		this.sampleContext = sampleContext;
		WSEntity e = new WSEntity();
		e.setName("First");
		list.add(e);
	}

	@Override
	protected void doRequest(CallContext context) throws Exception {
		String action = context.getProperties().getString("action", "");

		
		context.setSuccess(false);
		
		if (action.equals("list")) {

			for (WSEntity entity : list) {
				ObjectNode entry = context.addResult();
				entry.put("name", entity.getName());
			}
			context.setSuccess(true);
			
		} else
		if (action.equals("remove")) {
			String name = context.getProperties().getString("name", null);
			if (name == null) throw new NullPointerException("name is not set");
			name = name.trim();
			if (name.length() == 0) throw new NullPointerException("name is empty");

			// OK this is a trick, but it works for this demo !
			WSEntity entry = new WSEntity();
			entry.setName(name);
			list.remove(entry);
			context.addSuccess("removed=Item Removed");
			context.setSuccess(true);
		} else
		if (action.equals("add")) {
			
			String name = context.getProperties().getString("name", null);
			if (name == null) throw new NullPointerException("name is not set");
			name = name.trim();
			if (name.length() == 0) throw new NullPointerException("name is empty");

			WSEntity entry = new WSEntity();
			entry.setName(name);
			list.add(entry);
			context.addSuccess("created=Item Created");
			context.setSuccess(true);
			
		}
		
	}

}
