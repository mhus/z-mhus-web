package de.mhus.cherry.editor.impl.data;

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
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
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
import de.mhus.cherry.editor.impl.editor.EditorSpace;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.NavNode.TYPE;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoActionList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.errors.MException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.SopApi;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class DataSpace extends VerticalLayout implements Navigable, GuiLifecycle, ControlParent {

	private static final long serialVersionUID = 1L;

	@Override
	public void doInitialize() {
		
	}

	@Override
	public void doRefreshNode(CaoNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String navigateTo(String selection, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

}
