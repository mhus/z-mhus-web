package de.mhus.cherry.portal.impl.operation;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;

@Component(properties="tags=control|caonode|modify",provide=Operation.class)
public class PasteNode extends AbstractVaadinOperation {

	private static PasteAction action;
	
	public static void setAction(PasteAction action) {
		PasteNode.action = action;
	}
	
	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return null;
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		if (action == null) return new NotSuccessful(this, "not selected", -1);
		OperationResult res = action.doExecute(context);
		setAction(null);
		return res;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Paste Node", new DefRoot(
			));
	}

}
