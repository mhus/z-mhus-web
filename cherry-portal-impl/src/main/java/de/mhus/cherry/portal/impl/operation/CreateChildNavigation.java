package de.mhus.cherry.portal.impl.operation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.client.WidgetUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.util.CherryUtil;
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
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.Item;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.form.definition.FmCombobox;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationForm;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|create",provide=Operation.class)
public class CreateChildNavigation extends AbstractVaadinOperation {

	@Override
	public boolean canExecute(TaskContext context) {
		if (!super.canExecute(context)) return false;
		
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		if (navArray == null || navArray.length < 1) return false; 
		CaoNode nav = navArray[0];
		return CherryUtil.isNavigationNode(null, nav);
	}
	
	@Override
	protected AbstractVaadinOperationEditor createEditor() {
		return new AbstractVaadinOperationForm(this) {
			
			@Override
			protected void initDataSource(PropertiesDataSource ds) {
				MProperties properties = new MProperties();
				ds.setProperties(properties);
				
				// Page Items
				VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
				{
					Map<String, String> list = vHost.getContentNodeResolver().getDefaultPages();
					LinkedList<Item> pageTypes = new LinkedList<>();
					for (Map.Entry<String,String> item : list.entrySet())
						pageTypes.add(new Item(item.getKey(), item.getValue()));
					Item[] pageTypesArray = pageTypes.toArray(new Item[pageTypes.size()]);
					properties.put("page." + DataSource.ITEMS, pageTypesArray);
				}				
				// Page Type Items
				{
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

			}
			
		};
	}

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		
		// data
		CaoNode[] navArray = CherryUtil.getNodeFromProperties(context.getParameters());
		CaoNode nav = navArray[0];
		String title = context.getParameters().getString("title");
		String name = context.getParameters().getString("name", MFile.normalize(title));
		boolean hidden = context.getParameters().getBoolean("hidden", true);
		String pageType = context.getParameters().getString("pageType");
		String page = context.getParameters().getString("page");
		
		VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
		
		// create navigation node
		CaoNode newNavigation = null;
		{
			StructureControl control = getParentControl(nav);
			MProperties properties = new MProperties();
			properties.setString(WidgetApi.RES_TITLE, title);
			properties.setBoolean(CherryApi.NAV_HIDDEN, hidden);
			
			newNavigation = control.createChildNode(name, properties);
			if (newNavigation == null) return new NotSuccessful(this, "not created", "error=Can't create node", -1);
			
			StructureControl controlNew = newNavigation.adaptTo(StructureControl.class);
			controlNew.moveToBottom();
		}
		
		// create page node
		CaoNode newPage = null;
		{
			
			Map<String, String> list = vHost.getContentNodeResolver().getDefaultPages();
			if (list.containsKey(page)) {
				StructureControl control = newNavigation.adaptTo(StructureControl.class);
				MProperties properties = new MProperties();
				properties.setString(WidgetApi.RES_TITLE, title);
				properties.setString(WidgetApi.RENDERER, pageType);
				
				newPage = control.createChildNode(page, properties);
				if (newPage == null) return new NotSuccessful(this, "not created", "error=Can't create node", -1);
				
//				StructureControl controlNew = newPage.adaptTo(StructureControl.class);
//				controlNew.moveToBottom();
			}
		}
				
		EditorFactory factory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost,newNavigation);
		vHost.doPrepareCreatedWidget(newNavigation, factory);
		
		return new Successful(this, "ok", newNavigation);
	}

	protected StructureControl getParentControl(CaoNode nav) {
		return  nav.adaptTo(StructureControl.class);
	}

	@Override
	protected OperationDescription createDescription() {
		return new OperationDescription(this, getCaption(), new DefRoot(
					new FmCombobox("page", "Page", "Page"),
					new FmCombobox("pageType", "Page Type", "Type of the new page"),
					new FmText("title", "Page Title", "Title of the new page"),
					new FmText("name", "Node Name", "Technical node name shown in path, leave blank for default"),
					new FmCheckbox("hidden", "Hidden", "Set node to hidden")
				));
	}

	protected String getCaption() {
		return "Create Child Navigation";
	}

}
