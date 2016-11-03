package de.mhus.cherry.portal.impl.cache;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.portal.impl.CherryApiImpl;

@Command(scope = "cherry", name = "cache", description = "Deploy management")
@Service
public class CmdCache implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command: clearsessions,clear,enable,disable,info", multiValued=false)
    String cmd;

	@Override
	public Object execute() throws Exception {
		if (cmd.equals("clearsessions")) {
			CherryApiImpl.instance.globalSession.clear();
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
			System.out.println("Enabled: " + CacheApiImpl.instance.isEnabled() );
			System.out.println("Size   : " + CacheApiImpl.instance.size() );
			System.out.println("Timeout: " + CacheApiImpl.timeout );
		} else {
			System.out.println("Unknown command");
		}
		return null;
	}

}
