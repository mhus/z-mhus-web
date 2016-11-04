package de.mhus.cherry.portal.impl.api;

public class DefaultBaseApi extends ApiNode {

	public DefaultBaseApi() {
		apiProvider.put("login", new LoginHandler());
		apiProvider.put("logout", new LogoutHandler());
	}
}
