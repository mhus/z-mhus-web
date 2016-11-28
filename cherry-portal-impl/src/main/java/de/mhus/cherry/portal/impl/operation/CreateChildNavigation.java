package de.mhus.cherry.portal.impl.operation;

import java.util.Collection;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.core.util.Pair;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|create",provide=Operation.class)
public class CreateChildNavigation extends AbstractVaadinOperation {

	public static final Object NODE = "caonode";

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationEditor() {
			
			private ComboBox pageType;

			@Override
			protected void initUI() {
				CaoNode nav = (CaoNode) editorProperties.get(NODE);
				// page type
				pageType = new ComboBox(nls("pageType.caption=Page Type"));
				pageType.setNullSelectionAllowed(false);
				pageType.setTextInputAllowed(false);
				CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
				Collection<EditorFactory> list = call.getVirtualHost().getAvailablePageTypes(nav);
				Object first = null;
				for (EditorFactory editor : list) {
					Pair<String, String> pair = new Pair<String,String>(editor.getCaption(), editor.getIdent() );
					if (first != null) first = pair;
					pageType.addItem(pair);
				}
				
				pageType.setWidth("100%");
				addComponent(pageType);
				if (first != null) {
					pageType.select(first);
					control.canSave(true);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void fillOperationParameters(MProperties param) {
				Object v = pageType.getValue();
				if (v != null)
					param.setString("pageType", ((Pair<String,String>)v).getValue() );
			}
			
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		return null;
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Create Child Navigation", new DefRoot(
					new FmText("pageType", "Page Type", "Type of the new page")
				));
	}

}
