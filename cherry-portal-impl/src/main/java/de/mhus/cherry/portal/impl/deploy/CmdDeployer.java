package de.mhus.cherry.portal.impl.deploy;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "cherry", name = "deploy", description = "Deploy management")
@Service
public class CmdDeployer implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command: list,CHECK,WRITE,UPDATE,OVERWRITE,CLEANUP,RESET", multiValued=false)
    String cmd;

	@Override
	public Object execute() throws Exception {
		if (cmd.equals("list")) {
			ConsoleTable table = new ConsoleTable();
			table.setHeaderValues("Bundle","Private","Public");
			for (DeployDescriptor item : CherryDeployServlet.instance.getDescriptors())
				table.addRowValues(item.getName(),item.getPath(SPACE.PRIVATE), item.getPath(SPACE.PUBLIC));
			table.print(System.out);
		} else {
			CherryDeployServlet.instance.refreshAll(CherryDeployServlet.SENSIVITY.valueOf(cmd.toUpperCase()));
		}
		return null;
	}
	
}
