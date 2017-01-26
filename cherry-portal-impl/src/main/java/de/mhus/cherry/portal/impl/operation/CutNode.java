package de.mhus.cherry.portal.impl.operation;

import com.vaadin.ui.Label;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;

@Component(properties="tags=control|caonode|modify",provide=Operation.class)
public class CutNode extends AbstractVaadinOperation {

	public static CaoNode remember;

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return null;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Cut Node", new DefRoot(
			));
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		CaoNode nav = navArray[0];
		remember = nav;
		return new Successful(this);
	}

}
