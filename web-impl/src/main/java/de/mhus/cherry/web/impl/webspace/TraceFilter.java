package de.mhus.cherry.web.impl.webspace;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryFilter;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.errors.MException;

public class TraceFilter extends MLog implements CherryFilter {

	private static final String CALL_START = "filter_TraceFilter_start";

	@Override
	public boolean doFilterBegin(CallContext context) throws MException {
		long start = System.currentTimeMillis();
		context.setAttribute(CALL_START, start);
		log().i("access",context.getHttpHost(),context.getHttpMethod(),context.getHttpPath());
		return true;
	}

	@Override
	public void doFilterEnd(CallContext context) throws MException {
		Long start = (Long) context.getAttribute(CALL_START);
		if (start != null) {
			long duration = System.currentTimeMillis() - start;
			String durationStr = MTimeInterval.getIntervalAsString(duration);
			log().i("duration",durationStr,duration,context.getHttpHost(),context.getHttpMethod(),context.getHttpPath());
		}
	}

}
