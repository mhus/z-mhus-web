package de.mhus.osgi.cherry.cmd;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.cherry.api.central.CentralRequestHandler;
import de.mhus.osgi.cherry.api.central.CentralRequestHandlerAdmin;

@Command(scope = "http", name = "centrallist", description = "Return the list of central handlers")
public class CmdCentralHandlerList implements Action {

	public Object execute(CommandSession session) throws Exception {
		
		BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();
		
		ConsoleTable table = new ConsoleTable();
		table.getHeader().add("Nr");
		table.getHeader().add("Order");
		table.getHeader().add("Enabled");
		table.getHeader().add("Class");
		table.getHeader().add("Bundle-Id");
		table.getHeader().add("Bundle-Name");

		ServiceReference<CentralRequestHandlerAdmin> ref = ctx.getServiceReference(CentralRequestHandlerAdmin.class);
		if (ref == null) {
			System.out.println("CentralRequestHandlerAdmin not found");
			return null;
		}
		CentralRequestHandlerAdmin admin = ctx.getService(ref);
		if (admin == null) {
			System.out.println("CentralRequestHandlerAdmin not found");
			return null;
		}
		
		int cnt = 0;
		for (CentralRequestHandler handler : admin.getCentralHandlers()) {
			
			Bundle b = FrameworkUtil.getBundle(handler.getClass());
			table.addRowValues("" + cnt, String.valueOf( handler.getSortHint() ), "" + handler.isEnabled(), handler.getClass().getCanonicalName(), ""+b.getBundleId(),b.getSymbolicName() );
			cnt++;
		}

		table.print(System.out);
		return null;
	}
}
