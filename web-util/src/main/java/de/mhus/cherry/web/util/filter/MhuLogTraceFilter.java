package de.mhus.cherry.web.util.filter;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;

public class MhuLogTraceFilter implements WebFilter {

	public static final String SESSION_LOG_TRAIL = "__mhus_log_trail";

	@Override
	public void doInitialize(VirtualHost vHost, IConfig config) throws MException {
	}

	@Override
	public boolean doFilterBegin(InternalCallContext call) throws MException {
		
		if (call.isSession()) {
			String trail = call.getSession().getString(SESSION_LOG_TRAIL, null);
			if (trail != null) {
				MLogUtil.setTrailConfig(trail);
			}
		}
		return true;
	}

	@Override
	public void doFilterEnd(InternalCallContext call) throws MException {
		if (call.isSession()) {
			String trail = call.getSession().getString(SESSION_LOG_TRAIL, null);
			if (trail != null) {
				MLogUtil.releaseTrailConfig();
			}
		}
	}

	public static void setTrail(CallContext call, String trail) {
		call.getSession().setString(SESSION_LOG_TRAIL, trail);
		MLogUtil.setTrailConfig(trail);
		MLogUtil.log().d("Start Cherry Trail");
	}
	
	public static void releaseTrail(CallContext call, String trail) {
		if (call.isSession()) {
			call.getSession().remove(SESSION_LOG_TRAIL);
			MLogUtil.log().d("End Cherry Trail");
			MLogUtil.releaseTrailConfig();
		}
	}
	
}
