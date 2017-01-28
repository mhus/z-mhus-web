package de.mhus.cherry.editor.impl.editor;

import java.util.Collection;
import java.util.LinkedList;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;

import de.mhus.cherry.editor.impl.ControlUi;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.NavNode.TYPE;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MEventHandler;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;

public class NavigationView extends VerticalLayout implements ControlParent {

	private static final long serialVersionUID = 1L;
	private TreeTable tree;

	public NavigationView() {
		tree = new TreeTable("Navigation");

		tree.setImmediate(true);
		tree.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		tree.setItemCaptionPropertyId("name");
		tree.setContainerDataSource(createNavigationContainer());
		tree.addExpandListener(new Tree.ExpandListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void nodeExpand(ExpandEvent event) {
				doExpand(event.getItemId());
			}

		});		
		tree.addCollapseListener(new Tree.CollapseListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void nodeCollapse(CollapseEvent event) {
				doCollapse(event.getItemId());
			}
		});
		tree.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				doUpdateControl();
			}
		});
		
		tree.setSizeFull();
		tree.setSelectable(true);
		tree.setMultiSelect(true);
		tree.setItemIconPropertyId("icon");
		tree.setVisibleColumns("name","tecName","hidden","acl","theme","pageType");
		tree.setColumnHeaders("Navigation","Name","Hidden","ACL","Theme","Type");
		
		this.addComponent(tree);
		this.setSizeFull();
	}
	
	@Override
	public void doRefreshNode(CaoNode node) {
		doRefreshNode(node.getId());
	}
	
	public void doRefreshSelection() {
		Object v = tree.getValue();
		if (v == null) return;
		if (v instanceof String) {
			String id = (String)v;
			if (id != null) {
    			doRefreshNode(id);
			}
		} else
		if (v instanceof Collection) {
			Collection<String> list = (Collection<String>)v;
			if (list == null || list.size() == 0) return;
			for (String id : list) {
    			doRefreshNode(id);
			}
		}
	}

	public void doRefreshNode(String id) {
		try {
			Item item = tree.getItem(id);
			if (item == null) return;
			boolean collapsed = tree.isCollapsed(id);
			HierarchicalContainer container = (HierarchicalContainer)tree.getContainerDataSource();
			for (Object child : new LinkedList<>( container.getChildren(id)) ) {
				container.removeItemRecursively(child);
			}
			if (!collapsed) {
				doExpand(id);
			}
			tree.markAsDirty();
		} catch (Throwable t) {
			MLogUtil.log().d("doRefreshNode",id,t);
		}
	}

	protected void doUpdateControl() {
		// TODO Auto-generated method stub
		
	}

	protected void doCollapse(Object itemId) {
//		HierarchicalContainer container = (HierarchicalContainer)tree.getContainerDataSource();
//		Item item = container.getItem(itemId);
//		for (Object c : container.getChildren(itemId))
//			container.removeItemRecursively(c);
	}

	protected void doExpand(Object itemId) {
		try {
			HierarchicalContainer container = (HierarchicalContainer)tree.getContainerDataSource();
			Item item = container.getItem(itemId);
			NavNode node = (NavNode) item.getItemProperty("object").getValue();
			Collection<?> children = tree.getChildren(itemId);
			if (children != null && children.size() != 0) return;
					
			Collection<NavNode> nodeChildren = node.getAllNodes();
			if (nodeChildren.size() == 0) return;
			
			// sort nav nodes
			for (NavNode n : nodeChildren) {
				try {
					Item next = container.addItem(n.getId());
					container.setParent(n.getId(), itemId);
					fillItem(next, n);
					container.setChildrenAllowed(n.getId(), true);
					tree.setCollapsed(n.getId(), true);
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
		container.addContainerProperty("object", NavNode.class, null);
		container.addContainerProperty("theme", String.class, false);
		container.addContainerProperty("acl", Boolean.class, false);
		container.addContainerProperty("pageType", String.class, "");
		container.addContainerProperty("hidden", Boolean.class, false);
		container.addContainerProperty("icon", FontAwesome.class, null);
		
		String host = ((ControlUi)GuiUtil.getApi()).getHost();
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		NavNode navRoot = vHost.getNavigationProvider().getNode("/");
		
		try {
			Item item = container.addItem(navRoot.getId());
			fillItem(item, navRoot);
			container.setParent(navRoot.getId(), null);
			container.setChildrenAllowed(navRoot.getId(), true);
		} catch (Throwable t) {
			MLogUtil.log().i(t);
		}
		return container;
	}

	@SuppressWarnings("unchecked")
	private void fillItem(Item item, NavNode node) throws ReadOnlyException, MException {
		
		String renderer = node.getRes() == null ? null : node.getRes().getString(WidgetApi.RENDERER, null);

		CaoNode itemRes = null;
		item.getItemProperty("object").setValue(node);
		if (node.getType() == TYPE.NAVIGATION) {
			itemRes = node.getNav();
			item.getItemProperty("icon").setValue( FontAwesome.FOLDER );
		} else 
		if (node.getType() == TYPE.PAGE) {
			itemRes = node.getRes();
			item.getItemProperty("icon").setValue( FontAwesome.FOLDER_O );
		} else 
		if (node.getType() == TYPE.RESOURCE) {
			itemRes = node.getRes();
			if (MString.isSet(renderer))
				item.getItemProperty("icon").setValue( FontAwesome.FILE );
			else
				item.getItemProperty("icon").setValue( FontAwesome.FILE_O );
		}
		
		boolean hasAcl = false;
		for (String key : itemRes.getPropertyKeys())
			if (key.startsWith("acl:")) {
				hasAcl = true;
				break;
			}
		item.getItemProperty("name").setValue("  " + itemRes.getString("title", itemRes.getName()) );
		item.getItemProperty("tecName").setValue(itemRes.getName());
		item.getItemProperty("hidden").setValue(itemRes.getBoolean(CherryApi.NAV_HIDDEN, false));
		item.getItemProperty("acl").setValue( hasAcl );
		String theme = node.getNav().getString(WidgetApi.THEME, null); 
		if (theme != null && MString.isIndex(theme, '.')) theme = MString.afterLastIndex(theme, '.');
		item.getItemProperty("theme").setValue( theme );
		
		String pageType = "";
		if (renderer != null) {
			if (MString.isIndex(renderer, '.')) renderer = MString.afterLastIndex(renderer, '.');
			pageType = renderer;
		}
		item.getItemProperty("pageType").setValue( pageType );
			
	}

	@SuppressWarnings("unchecked")
	public NavNode[] getSelectedNode() {
		Object v = tree.getValue();
		if (v == null) return null;
		if (v instanceof String) {
			String id = (String)v;
			if (id != null) {
				return new NavNode[] { (NavNode) tree.getContainerProperty(id, "object").getValue() };
			}
		} else
		if (v instanceof Collection) {
			Collection<String> list = (Collection<String>)v;
			if (list == null || list.size() == 0) return null;
			NavNode[] out = new NavNode[list.size()];
			int cnt = 0;
			for (String id : list) {
				out[cnt] = (NavNode) tree.getContainerProperty(id, "object").getValue();
				cnt++;
			}
			return out;
		}
		
		return null;
	}

	public void addValueChangedListener(Property.ValueChangeListener listener) {
		tree.addValueChangeListener(listener);
	}

    public void addItemClickListener(ItemClickListener listener) {
    	tree.addItemClickListener(listener);
    }
	
	public void setSelected(CaoNode resource) {
		LinkedList<CaoNode> path = new LinkedList<>();
		CaoNode p = resource;
		while (p != null || path.size() > 100) {
			path.addFirst(p);
			p = p.getParent();
		}
		for (CaoNode n : path) {
			tree.setCollapsed(n.getId(), false);
		}
		tree.select(resource.getId());
	}

	public TreeTable getTree() {
		return tree;
	}
	
}
