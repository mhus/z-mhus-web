package de.mhus.cherry.web.impl.webspace;

import java.io.IOException;
import java.util.Date;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryActiveArea;
import de.mhus.lib.errors.MException;

public class HelloArea implements CherryActiveArea {

	@Override
	public boolean doRequest(CallContext call) throws MException {
		
		try {
			call.getHttpResponse().getWriter().write("Hello " + new Date());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

}
