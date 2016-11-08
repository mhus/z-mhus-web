package de.mhus.cherry.editor.impl.pages;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.editor.impl.ControlUi;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.SopApi;

public class PagesSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private Panel panel;
	private VerticalLayout contentLayout;
	private HorizontalSplitPanel split;
	private TreeTable tree;

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
		split.setSplitPosition(600, Unit.PIXELS);

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
					next.getItemProperty("name").setValue(n.getString("title", n.getName()) );
					next.getItemProperty("object").setValue(n);
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
		container.addContainerProperty("tecName", String.class, null);
		container.addContainerProperty("object", CaoNode.class, null);
		container.addContainerProperty("theme", Boolean.class, null);
		container.addContainerProperty("acl", Boolean.class, null);
		container.addContainerProperty("pageType", String.class, null);
		
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
		
	}

	@Override
	public void doDestroy() {
	}

	@Override
	public boolean navigateTo(String selection, String filter) {
		return false;
	}

}
