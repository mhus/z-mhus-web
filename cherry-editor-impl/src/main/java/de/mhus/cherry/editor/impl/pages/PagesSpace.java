package de.mhus.cherry.editor.impl.pages;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.editor.impl.ControlUi;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoActionList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.SopApi;

public class PagesSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private Panel panel;
	private VerticalLayout contentLayout;
	private HorizontalSplitPanel split;
	private TreeTable tree;
	private Accordion controlAcc;
	private VerticalLayout controlActions;
	private VerticalLayout controlWidgets;

	@Override
	public void doInitialize() {
		
		panel = new Panel();
		setMargin(true);
		addComponent(panel);
		panel.setCaption("Pages");
		panel.setSizeFull();
		split = new HorizontalSplitPanel();
		split.setSizeFull();
		panel.setContent(split);
		
		tree = new TreeTable("Navigation");
		
		split.setFirstComponent(tree);
		split.setSecondComponent(contentLayout);
		split.setSplitPosition(75, Unit.PERCENTAGE);

		tree.setImmediate(true);
		tree.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		tree.setItemCaptionPropertyId("name");
		tree.setContainerDataSource(createNavigationContainer());
		tree.addExpandListener(new Tree.ExpandListener() {

			@Override
			public void nodeExpand(ExpandEvent event) {
				doCheck(event.getItemId());
			}

		});		
		tree.addCollapseListener(new Tree.CollapseListener() {
			
			@Override
			public void nodeCollapse(CollapseEvent event) {
				doCollapse(event.getItemId());
			}
		});
		tree.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				doUpdateControl();
			}
		});
		
		tree.setSizeFull();
		tree.setSelectable(true);
		tree.setVisibleColumns("name","tecName","acl","theme","pageType");
		tree.setColumnHeaders("Navigation","Name","ACL","Theme","Page");
		
		
		controlAcc = new Accordion();
		controlAcc.setSizeFull();
		split.setSecondComponent(controlAcc);
		controlAcc.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				doUpdateControl();
			}
		});
		
		controlActions = new VerticalLayout();
		controlWidgets = new VerticalLayout();
		
		
		controlAcc.addTab(controlWidgets, "Widgets");
		controlAcc.addTab(controlActions, "Actions");
		
	}

	protected void doUpdateControl() {
		Component selected = controlAcc.getSelectedTab();
		if (selected == controlActions)
			doUpdateActions();
		else
		if (selected == controlWidgets)
			doUpdateWidgets();
		
	}

	private void doUpdateWidgets() {
		controlWidgets.removeAllComponents();
		String selId = (String)tree.getValue();
		if (selId == null) return;
		Item sel = (Item)tree.getItem(selId);
		if (sel == null) return;
		
		CaoNode res = (CaoNode)sel.getItemProperty("resource").getValue();
		if (res == null) return;
		
		CaoNode content = res.getNode(WidgetApi.CONTENT_NODE);
		if (content == null) return;
		
		for (CaoNode c : content.getNodes()) {
			try {
				Button b = new Button();
				b.setHtmlContentAllowed(true);
				
				String type = c.getString(WidgetApi.RENDERER, null);
				if (MString.isIndex(type, '.')) type = MString.afterLastIndex(type, '.');
				b.setCaption("<b>" + c.getString("title", MXml.encode(c.getName())) + "</b>" + (type == null ? "" : "<br/>" + MXml.encode(type) ) );
				b.setWidth("100%");
				controlWidgets.addComponent(b);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
	}

	private void doUpdateActions() {
		controlActions.removeAllComponents();
		String selId = (String)tree.getValue();
		if (selId == null) return;
		Item sel = (Item)tree.getItem(selId);
		if (sel == null) return;
		
		CaoNode nav = (CaoNode)sel.getItemProperty("object").getValue();
		if (nav != null) {
			boolean first = true;
			for (CaoAction action : nav.getConnection().getActions()) {
				if (first) {
					Label label = new Label("Navigation");
					controlActions.addComponent(label);
					first = false;
				}
				Button b = new Button(action.getName());
				b.setWidth("100%");
				controlActions.addComponent(b);
			}
		}
		
		CaoNode res = (CaoNode)sel.getItemProperty("resource").getValue();
		if (res != null) {
			boolean first = true;
			for (CaoAction action : res.getConnection().getActions()) {
				if (first) {
					Label label = new Label("Resource");
					controlActions.addComponent(label);
					first = false;
				}
				Button b = new Button(action.getName());
				b.setWidth("100%");
				controlActions.addComponent(b);
			}
		}
		

	}

	protected void doCollapse(Object itemId) {
//		HierarchicalContainer container = (HierarchicalContainer)tree.getContainerDataSource();
//		Item item = container.getItem(itemId);
//		for (Object c : container.getChildren(itemId))
//			container.removeItemRecursively(c);
	}

	protected void doCheck(Object itemId) {
		try {
			HierarchicalContainer container = (HierarchicalContainer)tree.getContainerDataSource();
			Item item = container.getItem(itemId);
			CaoNode node = (CaoNode) item.getItemProperty("object").getValue();
			Collection<?> children = tree.getChildren(itemId);
			if (children != null && children.size() != 0) return;
		
			Collection<CaoNode> nodeChildren = node.getNodes();
			if (nodeChildren.size() == 0) return;
			
			// sort nav nodes
			for (CaoNode n : nodeChildren) {
				try {
					Item next = container.addItem(n.getId());
					container.setParent(n.getId(), itemId);
					fillItem(next, n);
					container.setChildrenAllowed(n.getName(), true);
				} catch (Throwable t) {
					MLogUtil.log().i(t);
				}
			}
			
			tree.markAsDirty();
		} catch (Throwable t) {
			MLogUtil.log().i(t);
		}
	}

	private HierarchicalContainer createNavigationContainer() {
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("name", String.class, "?");
		container.addContainerProperty("tecName", String.class, "");
		container.addContainerProperty("object", CaoNode.class, null);
		container.addContainerProperty("resource", CaoNode.class, null);
		container.addContainerProperty("theme", String.class, false);
		container.addContainerProperty("acl", Boolean.class, false);
		container.addContainerProperty("pageType", String.class, "");
		
		String host = ((ControlUi)GuiUtil.getApi()).getHost();
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		CaoNode navRoot = vHost.getNavigationProvider().getNode("/");
		
		try {
			Item item = container.addItem(navRoot.getId());
			fillItem(item, navRoot);
			container.setParent(navRoot.getId(), null);
			container.setChildrenAllowed(navRoot.getName(), true);
		} catch (Throwable t) {
			MLogUtil.log().i(t);
		}
		return container;
	}

	@SuppressWarnings("unchecked")
	private void fillItem(Item item, CaoNode node) throws ReadOnlyException, MException {
		
		boolean hasAcl = false;
		for (String key : node.getPropertyKeys())
			if (key.startsWith("acl:")) {
				hasAcl = true;
				break;
			}
		
		item.getItemProperty("name").setValue(node.getString("title", node.getName()) );
		item.getItemProperty("object").setValue(node);
		item.getItemProperty("tecName").setValue(node.getName());
		item.getItemProperty("acl").setValue( hasAcl );
		String theme = node.getString(WidgetApi.THEME, null); 
		if (theme != null && MString.isIndex(theme, '.')) theme = MString.afterLastIndex(theme, '.');
		item.getItemProperty("theme").setValue( theme );
		
		String pageType = "";
		CaoNode res = null;
		String resId = node.getString(CherryApi.RESOURCE_ID);
		if (resId != null) {
			VirtualHost vHost = GuiUtil.getVirtualHost();
			res = vHost.getResourceResolver().getResource(vHost, resId);
			if (res != null) {
				CaoNode content = res.getNode(WidgetApi.CONTENT_NODE);
				if (content != null) {
					String renderer = content.getString(WidgetApi.RENDERER, null);
					if (renderer != null) {
						if (MString.isIndex(renderer, '.')) renderer = MString.afterLastIndex(renderer, '.');
						pageType = renderer;
					}
				}
			}
		}
		item.getItemProperty("resource").setValue(res);
		item.getItemProperty("pageType").setValue( pageType );
			
	}

	@Override
	public void doDestroy() {
	}

	@Override
	public boolean navigateTo(String selection, String filter) {
		return false;
	}

}
