package de.mhus.cherry.portal.impl.operation;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;

@Component(properties="tags=control|caonode|modify",provide=Operation.class)
public class CopyNode extends AbstractVaadinOperation {

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return null;
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Copy Node", new DefRoot(
			));
	}

}
