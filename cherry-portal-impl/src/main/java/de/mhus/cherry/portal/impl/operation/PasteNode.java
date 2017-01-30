package de.mhus.cherry.portal.impl.operation;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|modify",provide=Operation.class)
public class PasteNode extends AbstractVaadinOperation {

	private static final String SESSION_KEY = "de.mhus.cherry.portal.impl.operation.PasteNode";

//	private static PasteAction action;
	
	public static void setAction(PasteAction action) {
		IProperties session = Sop.getApi(CherryApi.class).getCurrentCall().getSession();
		session.put(SESSION_KEY, action);
	}
	
	public static PasteAction getAction() {
		IProperties session = Sop.getApi(CherryApi.class).getCurrentCall().getSession();
		return (PasteAction) session.get(SESSION_KEY);
	}
	
	@Override
	public boolean canExecute(TaskContext context) {
		if (getAction() == null) return false;
		return super.canExecute(context);
	}
	
	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return null;
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		if (getAction() == null) return new NotSuccessful(this, "not selected", -1);
		OperationResult res = getAction().doExecute(context);
		setAction(null);
		return res;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Paste Node", new DefRoot(
			));
	}

}
