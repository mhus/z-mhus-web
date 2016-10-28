package de.mhus.cherry.portal.impl.deploy;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "cherry", name = "deploy", description = "Deploy management")
@Service
public class CmdDeployer implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command", multiValued=false)
    String cmd;

	@Option(name="-c", aliases="--cleanup", description="Cleanup before deploy",required=false)
	boolean cleanup = false;

	@Override
	public Object execute() throws Exception {
		if (cmd.equals("refresh")) {
			CherryDeployServlet.instance.refreshAll(cleanup);
		} else
			System.out.println("Cmd not found");
		return null;
	}
	
}
