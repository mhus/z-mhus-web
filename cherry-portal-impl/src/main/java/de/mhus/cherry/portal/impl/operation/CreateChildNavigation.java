package de.mhus.cherry.portal.impl.operation;

import java.util.Collection;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.core.util.Pair;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.osgi.sop.api.Sop;

public class CreateChildNavigation extends AbstractVaadinOperation {

	public static final Object NAVIGATION_NODE = "navigationNode";

	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationEditor() {
			
			private ComboBox pageType;

			@Override
			protected void initUI() {
				NavNode nav = (NavNode) editorProperties.get(NAVIGATION_NODE);
				// page type
				pageType = new ComboBox(nls("pageType.caption=Page Type"));
				CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
				Collection<EditorFactory> list = call.getVirtualHost().getAvailablePageTypes(nav.getNav());
				Object first = null;
				for (EditorFactory editor : list) {
					Pair<String, String> pair = new Pair<String,String>(editor.getIdent(), editor.getCaption() );
					if (first != null) first = pair;
					pageType.addItem(pair);
				}
				
				VerticalLayout vert = new VerticalLayout();
				vert.setWidth("100%");
				vert.addComponent(pageType);
				setContent(vert);
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
					param.setString("pageTpe", ((Pair<String,String>)v).getValue() );
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
