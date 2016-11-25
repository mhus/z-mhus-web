package de.mhus.cherry.portal.impl.operation;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;

public class CreateChildNavigation extends AbstractVaadinOperation {

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationEditor() {
			
			@Override
			protected void initUI() {
				
			}

			@Override
			public void fillOperationParameters(MProperties param) {
				
			}
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		return null;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this.getClass(), "name=Create Child Navigation", new DefRoot(
					new FmText("name", "name.caption=Name", "name.description=name of the new navigation node")
				));
	}

}
