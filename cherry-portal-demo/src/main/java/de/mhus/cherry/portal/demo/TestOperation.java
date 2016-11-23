package de.mhus.cherry.portal.demo;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmText;

@Component(properties="tags=test|demo")
public class TestOperation implements Operation {

	private OperationDescription description = new OperationDescription(TestOperation.class, "Test", new DefRoot(
				new FmText("comment","Comment","Return This Comment"))
			);

	@Override
	public boolean hasAccess() {
		return true;
	}

	@Override
	public boolean canExecute(TaskContext context) {
		return true;
	}

	@Override
	public OperationDescription getDescription() {
		return description;
	}

	@Override
	public OperationResult doExecute(TaskContext context) throws Exception {
		return new Successful(this, "ok", "comment", context.getParameters().getString("comment", ""));
	}

	@Override
	public boolean isBusy() {
		return false;
	}

	@Override
	public boolean setBusy(Object owner) {
		return false;
	}

	@Override
	public boolean releaseBusy(Object owner) {
		return false;
	}

}
