package de.mhus.cherry.portal.impl.operation;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
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
public class CutNode extends AbstractVaadinOperation {
	
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
		PasteNode.setAction(new CutAction(navArray));
		return new Successful(this);
	}

	private static class CutAction implements PasteAction {

		private CaoNode[] selected;

		public CutAction(CaoNode[] nav) {
			this.selected = nav;
		}

		@Override
		public OperationResult doExecute(TaskContext context) throws Exception {
			System.out.println("Cut Action");
			CaoNode[] targetArray = CherryUtil.getNodeFromProperties(context.getParameters());
			if (targetArray == null || targetArray.length != 1) return new NotSuccessful("", "Target not set", -2);
			CaoNode target = targetArray[0];
			boolean res = true;
			for (CaoNode sel : selected) {
				StructureControl control = sel.adaptTo(StructureControl.class);
				if (control != null) {
					if (!control.moveTo(target))
						res = false;
				}
			}
			
			Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();

			return res ? new Successful("") : new NotSuccessful("", "", -4);
		}
		
	}
}
