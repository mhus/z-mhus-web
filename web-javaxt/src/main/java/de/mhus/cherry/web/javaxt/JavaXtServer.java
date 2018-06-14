package de.mhus.cherry.web.javaxt;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.PropertiesConfig;
import de.mhus.osgi.services.SimpleServiceIfc;
import javaxt.http.Server;

@Component(immediate=true)
public class JavaXtServer extends MLog implements SimpleServiceIfc {

	private static JavaXtServer instance;
	private Server server;

	public void start() {
		IConfig config = MApi.getCfg(JavaXtServer.class, new PropertiesConfig());
		//Start the server
        try {
            int port = config.getInt("port",9080);
            int numThreads = config.getInt("threads", 50);
            log().i("Start JavaXT Server",port,numThreads, config);
            server = new javaxt.http.Server(port, numThreads, new MainServlet(config));
            server.start();
        }
        catch (Exception e) {
            System.out.println("Server could not start because of an " + e.getClass());
            System.exit(1);
        }
	}

	public void stop() {
		server.stop();
	}

	@Override
	public String getSimpleServiceInfo() {
		return null;
	}

	@Override
	public String getSimpleServiceStatus() {
		return null;
	}

	@Override
	public void doSimpleServiceCommand(String cmd, Object... param) {
	}
	
	@Activate
	public void doActivate(ComponentContext ctx) {
		start();
		instance = this;
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		instance = null;
		stop();
	}
	
	public static JavaXtServer instance() {
		return instance;
	}

}
