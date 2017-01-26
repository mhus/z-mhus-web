package de.mhus.cherry.portal.impl.operation;

import java.util.Collection;
import java.util.LinkedList;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoAspect;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.core.util.Pair;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.Item;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.form.definition.FmCombobox;
import de.mhus.lib.form.definition.FmOptions;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.form.definition.FmValue;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationForm;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|create",provide=Operation.class)
public class CreateSiblingNavigation extends AbstractVaadinOperation {


	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationForm(this) {

			@Override
			protected void initDataSource(PropertiesDataSource ds) {
				MProperties properties = new MProperties();
				ds.setProperties(properties);
				
				CaoNode[] navArray = CherryUtil.getNodeFromProperties(editorProperties);
				CaoNode nav = navArray[0];
				CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
				Collection<EditorFactory> list = call.getVirtualHost().getAvailablePageTypes(nav);
				LinkedList<Item> pageTypeTypes = new LinkedList<>();
				for (EditorFactory editor : list) {
					pageTypeTypes.add(new Item(editor.getIdent(), editor.getCaption() ));
				}

				Item[] pageTypeTypesArray = pageTypeTypes.toArray(new Item[pageTypeTypes.size()]);
				properties.put("pageType." + DataSource.ITEMS, pageTypeTypesArray);
				
			}
			
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		CaoNode nav = navArray[0];
		String title = context.getParameters().getString("title");
		String name = context.getParameters().getString("name", MFile.normalize(title));
		boolean hidden = context.getParameters().getBoolean("hidden", true);
		StructureControl control = nav.getParent().adaptTo(StructureControl.class);
		MProperties properties = new MProperties();
		properties.setString(WidgetApi.RES_TITLE, title);
		properties.setBoolean(CherryApi.NAV_HIDDEN, hidden);
		CaoNode res = control.createChildNode(name, properties);
		if (res == null) return new NotSuccessful(this, "not created", "error=Can't create node", -1);
		return new Successful(this, "ok", res);
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, "Create Sibling Navigation", new DefRoot(
					new FmCombobox("pageType", "Page Type", "Type of the new page"),
					new FmText("title", "Page Title", "Title of the new page"),
					new FmText("name", "Node Name", "Technical node name shown in path, leave blank for default"),
					new FmCheckbox("hidden", "Hidden", "Set node to hidden")
				));
	}

}
