package de.mhus.cherry.portal.demo;

import java.util.Collection;

import org.vaadin.addons.portallayout.event.PortletCloseEvent;
import org.vaadin.addons.portallayout.event.PortletCollapseEvent;
import org.vaadin.addons.portallayout.portal.StackPortalLayout;
import org.vaadin.addons.portallayout.portlet.Portlet;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

public class SimplePageLayout extends LayoutPanel implements org.vaadin.addons.portallayout.event.PortletCloseEvent.Listener {

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
		Collection<CaoNode> allNodes = Sop.getApi(WidgetApi.class).sortWidgets(res).getNodes();
		for (CaoNode child : allNodes) {
			if (!Sop.getApi(CherryApi.class).hasResourceAccess(child, CherryApi.ACL_STRUCTURE) ) {
				readOnly = true;
				break;
			}
		}
		
		stack = new Stack[3];
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
				Portlet p = stack[i].portletFor(l);
				p.setCaption(child.getName());
				p.setCollapsible(false);
				if (readOnly)
					p.setLocked(true);
				allNodes.remove(child);
			}
		}
		
		for (CaoNode child : allNodes) {
			Label l = new Label(child.getName());
			l.setData(child);
			Portlet p = unused.portletFor(l);
			p.setCaption(child.getName());
			p.setCollapsible(false);
			if (readOnly)
				p.setLocked(true);
			
		}

		
	}
	
}
