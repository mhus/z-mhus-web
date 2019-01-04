package de.mhus.cherry.web.util.sample;

import de.mhus.cherry.web.api.WebFilter;

import java.util.UUID;

import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class TraceFilter extends MLog implements WebFilter {

	private static final String CALL_START = "filter_TraceFilter_start";

	@Override
	public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
		long start = System.currentTimeMillis();
		call.setAttribute(CALL_START, start);
		log().i("access",call.getHttpHost(),call.getHttpMethod(),call.getHttpPath());
		return true;
	}

	@Override
	public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
		Long start = (Long) call.getAttribute(CALL_START);
		if (start != null) {
			long duration = System.currentTimeMillis() - start;
			String durationStr = MPeriod.getIntervalAsString(duration);
			log().i("duration",durationStr,duration,call.getHttpHost(),call.getHttpMethod(),call.getHttpPath());
		}
	}

	@Override
	public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) {
		
	}

}
