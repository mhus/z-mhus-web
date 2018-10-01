package de.mhus.cherry.web.util.sample;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebArea;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class HelloArea implements WebArea {

	@Override
	public boolean doRequest(UUID instance, CallContext call) throws MException {
		
		try {
			call.getWriter().write("Hello " + new Date());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) {
		
	}

}
