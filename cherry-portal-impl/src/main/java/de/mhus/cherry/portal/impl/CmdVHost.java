package de.mhus.cherry.portal.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.services.MOsgi;
import de.mhus.osgi.sop.api.Sop;

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
			out.setHeaderValues("Name", "Type","Bundle");
			for ( MOsgi.Service<VirtualHost> ref : MOsgi.getServiceRefs(VirtualHost.class, null)) {
				VirtualHost vhost = ref.getService();
				String item = ref.getName();
				if (item.startsWith("cherry_virtual_host_")) item = item.substring("cherry_virtual_host_".length());
				Bundle bundle = FrameworkUtil.getBundle(vhost.getClass());
				out.addRowValues(item, vhost.getClass().getName(), bundle.getSymbolicName());
			}
			out.print(System.out);
			return null;
		}
		
		VirtualHost vhost = MApi.lookup(CherryApi.class).findVirtualHost(host);
		if (vhost == null) {
			System.out.println("vHost not found");
			return null;
		}
		
		if (cmd.equals("config")) {
			if (parameters == null || parameters.length == 0) {
				Set<String> list = vhost.getConfigurationListName();
				for (String item : list) {
					System.out.println(item);
				}
			} else {
				List<String> list = vhost.getConfigurationList(parameters[0]);
				if (list != null) {
					System.out.println("List: " + parameters[0]);
					System.out.println("-----------------------------");
					for (String item : list)
						System.out.println(item);
				} else
					System.out.println("List is not defined");
			}
		} else
		if (cmd.equals("configset")) {
			String name = parameters[0];
			LinkedList<String> list = new LinkedList<String>();
			if (parameters.length == 1)
				vhost.setConfigurationList(name, null);
			else {
				for (int i=1; i < parameters.length; i++)
					list.add(parameters[i]);
				vhost.setConfigurationList(name, list);
			}
		} else
		if (cmd.equals("use")) {
			CherryCallContext callContext = new CherryCallContext();
			callContext.setHttpRequest(null);
			callContext.setHttpResponse(new CherryResponseWrapper(null));
			callContext.setHttpServlet(null);
			callContext.setVirtualHost(vhost);
			CherryApiImpl.instance.setCallContext(callContext);
			printCurrentVHost();
		} else
		if (cmd.equals("release")) {
			CherryApiImpl.instance.setCallContext(null);
			printCurrentVHost();
		} else
		if (cmd.equals("current")) {
			printCurrentVHost();
		}
		
		
		return null;
	}

	private void printCurrentVHost() {
		CallContext currentCall = CherryApiImpl.instance.getCurrentCall();
		if (currentCall != null) {
			System.out.println(currentCall.getVirtualHost());
		} else {
			System.out.println("*undefined*");
		}
	}

	
}
