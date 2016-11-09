package de.mhus.cherry.editor.impl.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

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
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoActionList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.SopApi;

public class PagesSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private static final long serialVersionUID = 1L;
	private Panel panel;
	private VerticalLayout contentLayout;
	private HorizontalSplitPanel split;
	private TreeTable tree;
	private Accordion controlAcc;
	private HashMap<PageControl,String> controls;

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
		
		controls = new HashMap<>();
		for (PageControlFactory factory : MOsgi.getServices(PageControlFactory.class, null)) {
			String name = factory.getName();
			PageControl control = factory.createPageControl();
			controls.put(control, name);
			controlAcc.addTab(control, name);
		}
				
	}

	protected void doUpdateControl() {
		Component selected = controlAcc.getSelectedTab();
		
		String name = controls.get(selected);
		if (name == null) return;
		
		((PageControl)selected).doClean();
		
		String selId = (String)tree.getValue();
		if (selId == null) return;
		Item sel = (Item)tree.getItem(selId);
		if (sel == null) return;

		CaoNode nav = (CaoNode)sel.getItemProperty("object").getValue();
		CaoNode res = (CaoNode)sel.getItemProperty("resource").getValue();

		((PageControl)selected).doUpdate(nav, res);

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
	public String navigateTo(String selection, String filter) {
		return null;
	}

}
