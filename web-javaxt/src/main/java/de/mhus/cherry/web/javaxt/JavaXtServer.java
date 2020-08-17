/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.javaxt;

import org.osgi.service.component.ComponentContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import de.mhus.lib.core.M;
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
