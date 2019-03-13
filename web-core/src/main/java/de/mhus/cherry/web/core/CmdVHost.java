package de.mhus.cherry.web.core;

import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.TypeHeaderFactory;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "cherry", name = "vhost", description = "Virtual Host Management")
@Service
public class CmdVHost implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command: list, info, config, use, release, current, restart, headerfactories", multiValued=false)
	String cmd;
	
	@Argument(index=1, name="vhost", required=false, description="Virtual host name", multiValued=false)
    String host;	

	@Argument(index=2, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;
	
	@Override
	public Object execute() throws Exception {
		
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

			ConsoleTable out = new ConsoleTable();
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
