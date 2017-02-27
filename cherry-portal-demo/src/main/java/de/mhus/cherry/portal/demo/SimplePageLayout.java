package de.mhus.cherry.portal.demo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import org.vaadin.addons.portallayout.event.PortletCloseEvent;
import org.vaadin.addons.portallayout.event.PortletCollapseEvent;
import org.vaadin.addons.portallayout.portal.StackPortalLayout;
import org.vaadin.addons.portallayout.portlet.Portlet;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Extension;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.logging.Log;
import de.mhus.osgi.sop.api.Sop;

public class SimplePageLayout extends LayoutPanel implements org.vaadin.addons.portallayout.event.PortletCloseEvent.Listener {

	private static Log log = Log.getLog(SimplePageLayout.class);
	private CaoNode res;
	private Stack[] stack;
	private HorizontalLayout h;
	private Stack unused;
	private Panel v;
	private boolean readOnly;

	public SimplePageLayout(CaoNode res) {
		this.res = res;
		
		v = new Panel();
		
		h = new HorizontalLayout();
		v.setContent(h);
//		setSizeFull();
		h.setWidth("100%");
		v.addStyleName("v-scrollable");
        v.setHeight("100%");
        addComponent(v);
	}

	private static final long serialVersionUID = 1L;
	
	
    public class Stack extends StackPortalLayout {
        
        public Stack() {
            setWidth("100%");
            setHeight("100%");
            addPortletCloseListener(SimplePageLayout.this);
            setMargin(true);
            setSpacing(true);
        }
    }

	@Override
	public void portletClosed(PortletCloseEvent event) {

	}


	@Override
	public void doReload() {
		h.removeAllComponents();
		
		readOnly = false;
		Collection<CaoNode> allNodes = new TreeSet<CaoNode>( (a,b) -> { return a.getId().compareTo(b.getId()); } );
		allNodes.addAll(Sop.getApi(WidgetApi.class).sortWidgets(res).getNodes());
		for (CaoNode child : allNodes) {
			if (!Sop.getApi(CherryApi.class).hasResourceAccess(child, CherryApi.ACL_STRUCTURE) ) {
				readOnly = true;
				break;
			}
		}
		
		stack = new Stack[2];
		for (int i = 0; i < stack.length; i++) {
			stack[i] = new Stack();
			stack[i].setCaption("Column " + (i+1));
			h.addComponent(stack[i]);
			stack[i].setMargin(true);
			if (!readOnly)
				stack[i].addStyleName("stack");
		}
		unused = new Stack();
		unused.setCaption("Not Shown");
		if (!readOnly)
			unused.addStyleName("stack_unused");
		unused.setMargin(true);
		h.addComponent(unused);
		

			
		for (int i = 0; i < stack.length; i++) {
			Collection<CaoNode> nodes = Sop.getApi(WidgetApi.class).sortWidgetsIntoContainers(res, "" + i).getNodes();
			for (CaoNode child : nodes) {
				if (child.getBoolean(CherryApi.NAV_HIDDEN, false)) continue;
				Label l = new Label(child.getName());
				l.setData(child);
				
				VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
				EditorFactory editorFactory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost, child);
				Component preview = null;
				if (editorFactory != null) {
					preview = editorFactory.createPreview(child);
				}
				if (preview == null)
					preview = new Label("-");
				Portlet p = stack[i].portletFor(preview);
				p.setCaption(child.getName());
				p.setHeaderComponent(l);
				p.setCollapsible(false);
				p.setClosable(false);
				if (readOnly)
					p.setLocked(true);
				allNodes.remove(child);
			}
		}
		
		for (CaoNode child : allNodes) {
			
			Label l = new Label(child.getName());
			l.setData(child);
			
			VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
			EditorFactory editorFactory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost, child);
			Component preview = null;
			if (editorFactory != null) {
				preview = editorFactory.createPreview(child);
			}
			if (preview == null)
				preview = new Label("-");

			l.setData(child);
			Portlet p = unused.portletFor(preview);
			p.setCaption(child.getName());
			p.setCollapsible(false);
			p.setHeaderComponent(l);
			p.setClosable(false);
			if (readOnly)
				p.setLocked(true);
			
		}

		
	}


	@Override
	public String doSave() {
		for (int i = 0; i < stack.length; i++) {
			int cnt = 0;
			for (Component child : stack[i]) {
				try {
					if (!(child instanceof Label) || ((Label)child).getData() == null || ! (((Label)child).getData() instanceof CaoNode)  )
						continue;
					CaoNode node = (CaoNode) ((Label)child).getData();
					if (!node.getString(WidgetApi.CONTAINER,"").equals(""+i) ||
						node.getInt(WidgetApi.SORT, cnt) != cnt
							) {
						CaoWritableElement writable = node.getWritableNode();
						writable.setString(WidgetApi.CONTAINER, "" + i);
						writable.setInt(WidgetApi.SORT, cnt);
						writable.getUpdateAction().doExecute(null);
					}
				} catch (Throwable t) {
					log.e("save",i,cnt,child,t);
				}
				cnt++;
			}
		}
		{
			int cnt = 0;
			for (Component child : unused) {
				try {
					if (!(child instanceof Label) || ((Label)child).getData() == null || ! (((Label)child).getData() instanceof CaoNode)  )
						continue;
					CaoNode node = (CaoNode) ((Label)child).getData();
					if (!node.getString(WidgetApi.CONTAINER,"").equals("no") ||
							node.getInt(WidgetApi.SORT, cnt) != cnt
								) {
						CaoWritableElement writable = node.getWritableNode();
						writable.setString(WidgetApi.CONTAINER, "no");
						writable.setInt(WidgetApi.SORT, cnt);
						writable.getUpdateAction().doExecute(null);
					}
				} catch (Throwable t) {
					log.e("save","no",cnt,child,t);
				}
				cnt++;
			}

		}
		
		return null;
	}
	
}
