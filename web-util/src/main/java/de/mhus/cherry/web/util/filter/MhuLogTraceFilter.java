/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.util.filter;

import java.util.UUID;

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
	public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException {
	}

	@Override
	public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
		
		if (call.isSession()) {
			String trail = call.getSession().getString(SESSION_LOG_TRAIL, null);
			if (trail != null) {
				MLogUtil.setTrailConfig(trail);
			}
		}
		return true;
	}

	@Override
	public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
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
