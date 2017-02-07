package de.mhus.cherry.portal.impl.operation;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.osgi.sop.api.Sop;

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
			public void fillOperationParameters(IProperties param) {
				param.setBoolean("recursive", cbRecursive.getValue());
			}
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		boolean recursive = context.getParameters().getBoolean("recursive");
		boolean res = true;
		for (CaoNode nav : navArray) {
			StructureControl control = nav.adaptTo(StructureControl.class);
			if (! control.delete(recursive) )
				res = false; // TODO collect not successful
		}
		
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();

		return res ? new Successful(this) : new NotSuccessful(this, "Not", -1);
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Delete Node", new DefRoot(
				new FmCheckbox("recursive", "Recursive", "Delete recursive structures")
			));
	}

}
