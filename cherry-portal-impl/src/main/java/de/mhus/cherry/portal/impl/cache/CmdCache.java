package de.mhus.cherry.portal.impl.cache;

import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.portal.api.Container;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.impl.InternalCherryApiImpl;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.sop.api.aaa.AaaContext;

@Command(scope = "cherry", name = "cache", description = "Deploy management")
@Service
public class CmdCache implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command: clearsessions,clear,enable,disable,info", multiValued=false)
    String cmd;

	@Override
	public Object execute() throws Exception {
		if (cmd.equals("clearsessions")) {
			InternalCherryApiImpl.instance.globalSession.clear();
		} else
		if (cmd.equals("clear")) {
			CacheApiImpl.instance.clear();
		} else
		if (cmd.equals("enable")) {
			CacheApiImpl.instance.setEnabled(true);
		} else
		if (cmd.equals("disable")) {
			CacheApiImpl.instance.setEnabled(false);
		} else
		if (cmd.equals("info")) {
			System.out.println("Enabled : " + CacheApiImpl.instance.isEnabled() );
			System.out.println("Size    : " + CacheApiImpl.instance.size() );
			System.out.println("Timeout : " + Container.timeout );
			System.out.println("Sessions: " + InternalCherryApiImpl.instance.globalSession.size() );
			System.out.println("Bundles : " + InternalCherryApiImpl.instance.bundleStore.size() );
		} else
		if (cmd.equals("sessioninfo")) {
			ConsoleTable out = new ConsoleTable();
			out.setHeaderValues("Id", "Access Count", "Size", "User" );
			for (Entry<String, MProperties> entry : InternalCherryApiImpl.instance.globalSession.entrySet()) {
				String user = "?";
				AaaContext context = (AaaContext)entry.getValue().get(InternalCherryApi.SESSION_ACCESS_NAME);
				if (context != null) user = context.getAccount().getName();
				out.addRowValues(
						entry.getKey(), 
						InternalCherryApiImpl.instance.globalSession.getAccessCount(entry.getKey()), 
						entry.getValue().size(), 
						user 
						);
			}
			out.print(System.out);
		} else {
			System.out.println("Unknown command");
		}
		return null;
	}

}
