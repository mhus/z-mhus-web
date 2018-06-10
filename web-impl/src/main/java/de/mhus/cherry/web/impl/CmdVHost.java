package de.mhus.cherry.web.impl;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "cherry", name = "vhost", description = "Virtual Host Management")
@Service
public class CmdVHost implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command: list,config, use, release, current", multiValued=false)
	String cmd;
	
	@Argument(index=1, name="vhost", required=false, description="Virtual host name", multiValued=false)
    String host;	

	@Argument(index=2, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;
	
	@Override
	public Object execute() throws Exception {
		
		if (cmd.equals("list")) {

			ConsoleTable out = new ConsoleTable();
			out.setHeaderValues("Name","Alias", "Type","Bundle");

			for (VirtualHost vhost : CherryApiImpl.instance().getVirtualHosts()) {
				Bundle bundle = vhost.getBundle();
				out.addRowValues(vhost.getName(),vhost.getVirtualHostAliases(), vhost.getClass().getName(), bundle.getSymbolicName() + "[" + bundle.getBundleId() + "]");
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
			vhost.stop(CherryApiImpl.instance());
			vhost.start(CherryApiImpl.instance());
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
