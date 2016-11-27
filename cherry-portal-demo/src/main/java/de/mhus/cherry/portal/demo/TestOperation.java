package de.mhus.cherry.portal.demo;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.AbstractOperation;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmText;

@Component(properties="tags=test|demo")
public class TestOperation extends AbstractOperation implements Operation {

	@Override
	public OperationResult doExecute2(TaskContext context) throws Exception {
		return new Successful(this, "ok", "comment", context.getParameters().getString("comment", ""));
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Test", new DefRoot(
				new FmText("comment","Comment","Return This Comment"))
			);
	}

}
