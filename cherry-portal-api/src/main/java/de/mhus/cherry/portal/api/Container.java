package de.mhus.cherry.portal.api;

import de.mhus.lib.core.MTimeInterval;

public class Container {

	public static long timeout = MTimeInterval.MINUTE_IN_MILLISECOUNDS * 5;

	private Object object;
	private long timestamp = System.currentTimeMillis();
	
	public Container(Object value) {
		object = value;
	}

	public boolean isTimeout() {
		
		return System.currentTimeMillis() - timestamp > timeout;
	}

	public String getString() {
		return object == null ? null : object.toString();
	}

	public Object getObject() {
		return object;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
