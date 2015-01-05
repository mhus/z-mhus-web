package de.mhus.osgi.cherry.accesslog;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import de.mhus.lib.core.MCast;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.api.central.CentralRequestHandler;
import de.mhus.osgi.cherry.api.central.ConfigurableHandler;
import de.mhus.osgi.cherry.api.util.ExtendedServletResponse;
import aQute.bnd.annotation.component.Component;

@Component(immediate=true)
public class AccessLogHandler implements CentralRequestHandler, ConfigurableHandler {

	public static final String TIME_KEY = "web-access-log-time";
	private static Logger log = Logger.getLogger("web-access-log");
	
	private boolean enabled = true;
	private long maxTime = 1000;
	
	@Override
	public boolean doHandleBefore(CentralCallContext context) throws IOException, ServletException {
		log.info("Request," + context.getHost() + "," + context.getTarget());
		context.setAttribute(TIME_KEY, System.currentTimeMillis());
		ExtendedServletResponse.inject(context);
		return false;
	}

	@Override
	public void doHandleAfter(CentralCallContext context)
			throws IOException, ServletException {
		
		long cur = System.currentTimeMillis();
		Long start = (Long) context.getAttribute(TIME_KEY);
		long time = 0;
		if (start != null) {
			time = cur - start;
		}
		int rc = 0;
		if (ExtendedServletResponse.isExtended(context))
			rc = ExtendedServletResponse.getExtendedResponse(context).getStatus();

		if (rc != 0 && rc != 200 || time > maxTime )
			log.info("Warn," + context.getBaseRequest().getHeader("host") + "," + context.getTarget() + "," + time + "," + rc);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public double getSortHint() {
		return -10;
	}

	@Override
	public void configure(Properties rules) {
		if (rules != null) {
			maxTime = MCast.tolong(rules.getProperty(getClass().getSimpleName() + ".alertTime"),maxTime);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
