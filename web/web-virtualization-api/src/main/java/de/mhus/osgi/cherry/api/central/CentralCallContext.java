package de.mhus.osgi.cherry.api.central;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CentralCallContext {

	private String target;
	private HttpServletRequest baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HashMap<String, Object> attributes = new HashMap<>();
	private int lastHandler = -1;
	
	public CentralCallContext(String target, HttpServletRequest baseRequest,
			HttpServletRequest request, HttpServletResponse response) {
		this.setTarget(target);
		this.baseRequest = baseRequest;
		this.setRequest(request);
		this.setResponse(response);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public HttpServletRequest getBaseRequest() {
		return baseRequest;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getHost() {
		return getBaseRequest().getHeader("host");
	}
	
	public void setAttribute(String key, Object val) {
		attributes.put(key, val);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public int getLastHandler() {
		return lastHandler;
	}

	public void setLastHandler(int lastHandler) {
		this.lastHandler = lastHandler;
	}

}
