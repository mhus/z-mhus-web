package de.mhus.cherry.web.util.sample;

import java.io.IOException;
import java.util.Date;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebArea;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class HelloArea implements WebArea {

	@Override
	public boolean doRequest(CallContext call) throws MException {
		
		try {
			call.getWriter().write("Hello " + new Date());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void doInitialize(VirtualHost vHost, IConfig config) {
		// TODO Auto-generated method stub
		
	}

}
