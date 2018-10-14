package de.mhus.cherry.web.util.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletOutputStream;

import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

// https://en.wikipedia.org/wiki/Basic_access_authentication
public class BaseAuthFilter implements WebFilter {

	public static String NAME = "base_auth_filter";

	@Override
	public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException {
		vHost.getProperties().put(NAME + instance, new Config(config));
	}

	@Override
	public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
		Config config = (Config)call.getVirtualHost().getProperties().get(NAME + instance);
		if (config == null) {
			send401(call, config);
			return false;
		}
		String path = call.getHttpPath();
		if (!path.matches(config.included)) return true;
		if (MString.isSet(config.excluded) && path.matches(config.excluded)) return true;
		
		String auth = call.getHttpRequest().getHeader("Authorization");  
		if (auth == null) {
			send401(call, config);
			return false;
		}
        if (!auth.toUpperCase().startsWith("BASIC ")) {   
			send401(call, config);
            return false;  // we only do BASIC  
        }  
        // Get encoded user and password, comes after "BASIC "  
        String userpassEncoded = auth.substring(6);  
        // Decode it, using any base 64 decoder  
        String userpassDecoded = new String( Base64.decode(userpassEncoded) );
        // Check our user list to see if that user and password are "allowed"
        String[] parts = userpassDecoded.split(":",2);
        
        String account = null;
        String pass = null;
        if (parts.length > 0) account = MUri.decode(parts[0]);
        if (parts.length > 1) pass = MUri.decode(parts[1]);
        
        if (config.user.equals(account) && MPassword.equals( config.pass, pass) )
        		return true;
		
		send401(call, config);
		return false;
	}

	private void send401(InternalCallContext call, Config config) throws MException {
		try {
			call.getHttpResponse().sendError(401);
			call.getHttpResponse().setHeader("WWW-Authenticate", "BASIC realm=\""+config.realm+"\", charset=\"UTF-8\"");
			call.getHttpResponse().setContentType("text/html");
			ServletOutputStream os = call.getHttpResponse().getOutputStream();
			os.write(config.message.getBytes()); // utf-8?
			os.flush();
		} catch (IOException e) {
			throw new MException(e);
		}
	}

	@Override
	public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
	}

	private static class Config {

		private String realm;
		private String included;
		private String excluded;
		private String user;
		private String pass;
		private String message;

		public Config(IConfig config) {
			included = config.getString("included", ".*");
			excluded = config.getString("excluded", "");
			user = config.getString("user", "");
			pass = config.getString("pass", "");
			message = config.getString("message","Access denied");
			realm = config.getString("realm","Access");
		}
		
	}
	
}
