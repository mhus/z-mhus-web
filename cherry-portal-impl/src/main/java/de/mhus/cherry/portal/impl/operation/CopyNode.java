package de.mhus.cherry.portal.impl.operation;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|modify",provide=Operation.class)
public class CopyNode extends AbstractVaadinOperation {

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return null;
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		PasteNode.setAction(new CopyAction(navArray));
		return new Successful(this);
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Copy Node", new DefRoot(
			));
	}

	private static class CopyAction implements PasteAction {

		private CaoNode[] selected;

		public CopyAction(CaoNode[] nav) {
			this.selected = nav;
		}

		@Override
		public OperationResult doExecute(TaskContext context) throws Exception {
			System.out.println("Copy Action");
			CaoNode[] targetArray = CherryUtil.getNodeFromProperties(context.getParameters());
			if (targetArray == null || targetArray.length != 1) return new NotSuccessful("", "Target not set", -2);
			CaoNode target = targetArray[0];
			boolean res = true;
			for (CaoNode sel : selected) {
				StructureControl control = sel.adaptTo(StructureControl.class);
				if (control != null) {
					CaoNode newNode = control.copyTo(target, true);
					if (newNode == null)
						res = false;
				}
			}
			
			MApi.lookup(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();
			
			return res ? new Successful("") : new NotSuccessful("", "", -4);
		}
		
	}
}
