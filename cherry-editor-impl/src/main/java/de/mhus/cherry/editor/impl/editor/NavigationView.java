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
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.TreeTargetDetails;

import de.mhus.cherry.editor.impl.ControlUi;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.NavNode.TYPE;
import de.mhus.cherry.portal.api.StructureChangesListener;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.Changes;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;
import de.mhus.lib.core.MEventHandler;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;

public abstract class NavigationView extends VerticalLayout implements ControlParent, StructureChangesListener {

	private static Log log = Log.getLog(NavigationView.class);
	private static final long serialVersionUID = 1L;
	private TreeTable tree;
	private VirtualHost vHost;

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
		tree.setVisibleColumns("name","tecName","hidden","acl","theme","pageType","sort");
		tree.setColumnHeaders("Navigation","Name","Hidden","ACL","Theme","Type","Sort");
		
		tree.setDragMode(TableDragMode.ROW);
		tree.setDropHandler(new MoveDropHandler());
		
		this.addComponent(tree);
		this.setSizeFull();
		
		vHost.getStructureRegistry().registerWeak(this);
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
			log.d("doRefreshNode",id,t);
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
					log.i(t);
				}
			}
			
			tree.markAsDirty();
		} catch (Throwable t) {
			log.i(t);
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
		container.addContainerProperty("sort", String.class, "");
		
		String host = ((ControlUi)GuiUtil.getApi()).getHost();
		vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		NavNode navRoot = vHost.getNavigationProvider().getNode("/");
		
		try {
			Item item = container.addItem(navRoot.getId());
			fillItem(item, navRoot);
			container.setParent(navRoot.getId(), null);
			container.setChildrenAllowed(navRoot.getId(), true);
		} catch (Throwable t) {
			log.i(t);
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
		item.getItemProperty("sort").setValue( itemRes.getString(WidgetApi.SORT) );
		
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

	@Override
	public void navigationChanges(Change[] changes) {
//		if (tree == null || tree.getUI() == null) return;
//		tree.getUI().access(new Runnable() {
//			@Override
//			public void run() {
				for (Change c : changes)
					switch (c.getEvent()) {
					case BIG_CHANGE:
						log.i("Big Change");
		//				doRefreshNode( ); all
						break;
					case CREATED:
						doRefreshNode(c.getParent());
						break;
					case DELETED:
						doRefreshNode(c.getParent());
						break;
					case LINK:
						doRefreshNode(c.getParent());
						break;
					case MODIFIED:
					case RENAMED:
						doRefreshNode(c.getNode());
						break;
					case MOVED:
						doRefreshNode(c.getParent());
						break;
					case UNLINK:
						doRefreshNode(c.getParent());
						break;
					case RENDITION_DELETED:
					case RENDITION_MODIFIED:
						break;
					default:
						MLogUtil.log().e("Unknown Change Event",c.getEvent());
						break;
					}
//			}
//			});
	}
	
	private class MoveDropHandler implements DropHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public void drop(DragAndDropEvent event) {
			final Transferable t = event.getTransferable();
			AbstractSelect.AbstractSelectTargetDetails dropData = ((AbstractSelect.AbstractSelectTargetDetails) event.getTargetDetails());
			Object sourceItemId = ((DataBoundTransferable) t).getItemId();
			Object targetItemId = dropData.getItemIdOver();
			VerticalDropLocation location = dropData.getDropLocation();
			
			Item sourceItem = tree.getItem(sourceItemId);
			NavNode sourceNode = (NavNode) sourceItem.getItemProperty("object").getValue();

			Item targetItem = tree.getItem(targetItemId);
			NavNode targetNode = (NavNode) targetItem.getItemProperty("object").getValue();
			
			NavigationView.this.move(sourceNode, targetNode, location);
			// Sorting goes as
            // - If dropped ON (MIDDLE) a node, we preppend it as a child
            // - If dropped on the TOP part of a node, we move/add it before the node
            // - If dropped on the BOTTOM part of a node, we move/add it after the node if it has no children, or prepend it as a child if it has children
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		} 
		
	}

	public abstract void move(NavNode source, NavNode target, VerticalDropLocation location);
}
