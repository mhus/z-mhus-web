package de.mhus.cherry.portal.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.osgi.sop.api.Sop;

@Command(scope = "cherry", name = "vhost", description = "Virtual Host Management")
@Service
public class CmdVHost implements Action {

	@Argument(index=0, name="vhost", required=true, description="Virtual Host name or *", multiValued=false)
    String host;
	
	@Argument(index=1, name="cmd", required=true, description="Command: list,config", multiValued=false)
    String cmd;

	@Argument(index=2, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;
	
	@Override
	public Object execute() throws Exception {
		
		if (cmd.equals("list")) {
			
			return null;
		}
		
		VirtualHost vhost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		if (vhost == null) {
			System.out.println("vHost not found");
			return null;
		}
		
		if (cmd.equals("config")) {
			if (parameters == null || parameters.length == 0) {
				Set<String> list = vhost.getConfigurationListName();
				for (String item : list)
					System.out.println(item);
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
		}
		
		
		return null;
	}

	
}
