package de.mhus.cherry.portal.impl.operation;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;

@Component(properties="tags=control|caonode|delete",provide=Operation.class)
public class DeleteNode extends AbstractVaadinOperation {

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationEditor() {
			
			private CheckBox cbRecursive;

			@Override
			protected void initUI() {
				cbRecursive = new CheckBox("Recursive?");
				cbRecursive.setValue(false);
				addComponent(cbRecursive);
				
				Label dummy = new Label(" ");
				addComponent(dummy);
				setExpandRatio(dummy, 1);
			}
			
			@Override
			public void fillOperationParameters(MProperties param) {
				param.setBoolean("recursive", cbRecursive.getValue());
			}
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Delete Node", new DefRoot(
				new FmCheckbox("recursive", "Recursive", "Delete recursive structures")
			));
	}

}
