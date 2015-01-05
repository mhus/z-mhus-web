package de.mhus.osgi.cherry.cmd;

import java.util.Properties;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.cherry.api.central.CentralRequestHandlerAdmin;

@Command(scope = "http", name = "centralupdate", description = "Update central handlers list with properties")
public class CmdCentralHandlerUpdate implements Action {

	@Argument(index=0, name="property", required=false, description="Property key=value", multiValued=true)
    String[] prop;

	public Object execute(CommandSession session) throws Exception {
		
		BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();

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
		
		Properties p = null;
		if (prop != null) {
			p = new Properties();
			for (String l : prop) {
				String k = MString.beforeIndex(l, '=');
				String v = MString.afterIndex(l, '=');
				p.put(k, v);
			}
		}
		
		admin.updateCentralHandlers(p);
		
		p = admin.getCentralHandlerProperties();
		System.out.println("OK " + p);
		return null;
	}
}
