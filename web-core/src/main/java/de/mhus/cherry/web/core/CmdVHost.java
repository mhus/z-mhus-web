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
package de.mhus.cherry.web.core;

import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.TypeHeaderFactory;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "cherry", name = "vhost", description = "Virtual Host Management")
@Service
public class CmdVHost extends AbstractCmd {

	@Argument(index=0, name="cmd", required=true, description="Command: list, info, config, use, release, current, restart, headerfactories", multiValued=false)
	String cmd;
	
	@Argument(index=1, name="vhost", required=false, description="Virtual host name", multiValued=false)
    String host;	

	@Argument(index=2, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;
	
    @Option(name = "-ct", aliases = { "--console-table" }, description = "Console table options", required = false, multiValued = false)
    String consoleTable;

	@Override
	public Object execute2() throws Exception {
		
		if (cmd.equals("info")) {
			VirtualHost h = CherryApiImpl.instance().getVirtualHosts().get(host);
			if (h == null) {
				System.out.println("Virtual host not found");
				return null;
			}
			System.out.println("Name: " + h.getName());
			System.out.println("Bundle: " + h.getBundle() );
			System.out.println("Charset: " + h.getCharsetEncoding());
			System.out.println("Class: "+h.getClass().getCanonicalName());
			System.out.println("Updated: "+ h.getUpdated());
			IProperties p = h.getProperties();
			if (p != null) {
				for (Entry<String, Object> entry : p.entrySet())
					System.out.println("Property: " + entry.getKey() + "=" + entry.getValue());
			} else
				System.out.println("No Properties");
			System.out.println("Config:");
			System.out.println(h.getConfig().dump());
		} else
		if (cmd.equals("list")) {

			ConsoleTable out = new ConsoleTable(consoleTable);
			out.setHeaderValues("Alias","Name","Type","Bundle","Updated");

			for (Entry<String, VirtualHost> entry : CherryApiImpl.instance().getVirtualHosts().entrySet()) {
				VirtualHost vhost = entry.getValue();
				Bundle bundle = vhost.getBundle();
				out.addRowValues(entry.getKey(), vhost.getName(), vhost.getClass().getName(), bundle.getSymbolicName() + "[" + bundle.getBundleId() + "]", vhost.getUpdated());
			}
			out.print(System.out);
			return null;
		}
		if (cmd.equals("release")) {
			CherryApiImpl.instance().setCallContext(null);
			printCurrentVHost();
			return null;
		}
		if (cmd.equals("current")) {
			printCurrentVHost();
			return null;
		}
		
        if (cmd.equals("headerfactories")) {
            for (TypeHeaderFactory factory : CherryApiImpl.instance().getTypeHeaderFactories()) {
                System.out.println("> " + factory.getClass().getCanonicalName() + ": " + factory);
            }
        }


		VirtualHost vhost = CherryApiImpl.instance().findVirtualHost(host);
		if (vhost == null) {
			System.out.println("vHost not found: " + host);
			return null;
		}

		if (cmd.equals("use")) {
			CherryCallContext callContext = new CherryCallContext(null, null, new CherryResponseWrapper(null), vhost);
			CherryApiImpl.instance().setCallContext(callContext);
			printCurrentVHost();
		} else
		if (cmd.equals("restart")) {
			CherryApiImpl.instance().restart(vhost);
			System.out.println("OK");
		} else {
			System.out.println("Command not found");
		}
		
		return null;
	}

	private void printCurrentVHost() {
		CallContext currentCall = CherryApiImpl.instance().getCurrentCall();
		if (currentCall != null) {
			System.out.println(currentCall.getVirtualHost());
		} else {
			System.out.println("*undefined*");
		}
	}

	
}
