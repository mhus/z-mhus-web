package de.mhus.cherry.web.api;

public interface CherryApi {

	CallContext getCurrentCall();

	String getMimeType(String file);

}
