package de.mhus.cherry.portal.impl.aaa;

import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.LoginHandler;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.lib.servlet.ResponseWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class BasicAuthHandler implements LoginHandler {

	@Override
	public AaaContext doLogin(RequestWrapper request, ResponseWrapper resonse) {
    	String auth = request.getHeader("Authorization");  
		if (auth == null) return null;
        if (!auth.toUpperCase().startsWith("BASIC ")) {   
            return null;  // we only do BASIC AUTH
        }  
        // Get encoded user and password, comes after "BASIC "  
        String userpassEncoded = auth.substring(6);  
        // Decode it, using any base 64 decoder  
        String userpassDecoded = new String( Base64.decode(userpassEncoded) );
        // Check our user list to see if that user and password are "allowed"
        String[] parts = userpassDecoded.split(":",2);

        String username = null;
        String password = null;
        if (parts.length > 0) username = MUri.decode(parts[0]);
        if (parts.length > 1) password = MUri.decode(parts[1]);
        
        AccessApi api = MApi.lookup(AccessApi.class);
        String ticket = api.createUserTicket(username, password);
        AaaContext context = api.process(ticket);
        
        // destroy session after request
		String sessionId = request.getSessionId();
		IProperties session = MApi.lookup(InternalCherryApi.class).getCherrySession(sessionId);
        session.setBoolean(InternalCherryApi.SESSION_DESTROY_ON_RELEASE, true);
        
		return context;
	}

}
