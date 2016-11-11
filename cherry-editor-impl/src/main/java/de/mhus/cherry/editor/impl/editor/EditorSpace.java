package de.mhus.cherry.editor.impl.editor;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.EditorControl;
import de.mhus.cherry.portal.api.control.EditorControlFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.errors.MException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class EditorSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private static Log log = Log.getLog(EditorSpace.class);
	private static final long serialVersionUID = 1L;
	private String nav;
	private String lnk;
	private Panel panel;
	private VerticalLayout contentLayout;
	private CaoNode resource;
	private EditorPanel editor;
	private Button bSave;
	private Button bCancel;
	private String resId;
	private TabSheet tabs;

	@Override
	public String navigateTo(String selection, String filter) {
		
		return doShow(filter);
		
	}

	@Override
	public void doInitialize() {
		//UI.getCurrent().getPage().setUriFragment("moin",false);
		nav = UI.getCurrent().getPage().getUriFragment();
		lnk = nav;
		if (MString.isIndex(lnk, ':')) {
			lnk = MString.beforeIndex(nav, ':');
			resId = MString.afterIndex(nav, ':');
		}
//		Label l = new Label();
//		l.setCaptionAsHtml(true);
//		l.setCaption("<a href='" + lnk + "'>" + MXml.encode(nav) + "</a>" );
		
		panel = new Panel();
		setMargin(true);
		addComponent(panel);
		panel.setCaption("Editor");
		panel.setSizeFull();
		tabs = new TabSheet();
		
		panel.setContent(tabs);
		
		contentLayout = new VerticalLayout();
//		contentLayout.addComponent(l);
		if (resId != null)
			doShow(resId);

	}

	private synchronized String doShow(String resId) {
		log.d("show resource", resId);
		
		tabs.removeAllComponents();
		tabs.addTab(contentLayout, "Content");
		
		contentLayout.removeAllComponents();
		
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost( GuiUtil.getApi().getHost() );
		resource = vHost.getResourceResolver().getResource(vHost, resId);
		if (resource == null) {
			// resource not found
			return null;
		}
		
		EditorFactory factory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost,resource);
		if (factory == null) {
			// editor not found
			return null;
		}
		
		doFillTabs(resource, factory);
		
		
		Panel editorPanel = new Panel();
		editorPanel.setSizeFull();
		contentLayout.addComponent(editorPanel);

		try {
			editor = factory.createEditor(resource.getWritableNode());
			editor.setSizeFull();
			editor.setMargin(true);
			editorPanel.setContent(editor);
			editor.initUi();
			panel.setCaption( editor.getTitle() );
		} catch (MException e) {
			log.e(e);
			return null;
		}
		
		HorizontalLayout buttonPanel = new HorizontalLayout();
		bCancel = new Button("Cancel", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doCancel();
			}
		});
		buttonPanel.addComponent(bCancel);
		Label dummy = new Label(" ");
		buttonPanel.addComponent(dummy);
		buttonPanel.setExpandRatio(dummy, 1f);
		bSave = new Button("Save", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doSave();
			}
		});
		buttonPanel.addComponent(bSave);
		buttonPanel.setSizeFull();
		contentLayout.addComponent(buttonPanel);
		contentLayout.setExpandRatio(editorPanel, 1f);
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		//contentLayout.setSizeFull();		
		
		return "Edit " + resource.getString("title", resource.getName());
	}

	private void doFillTabs(CaoNode res, EditorFactory editorFactory) {
		AccessApi aaa = Sop.getApi(AccessApi.class);
		Account account = aaa.getCurrentOrGuest().getAccount();
		for (EditorControlFactory factory : CherryUtil.orderServices(EditorSpace.class, EditorControlFactory.class)) {
			if (aaa.hasGroupAccess(account, EditorSpace.class, factory.getName(), "create")) {
				EditorControl c = factory.createEditorControl(res, editorFactory);
				if (c != null) {
					tabs.addTab(c, factory.getName());
				}
			}
		}
		
		
	}

	protected void doCancel() {
		doBack();
	}

	protected void doSave() {
		String error = editor.doSave();
		if (error != null) {
			UI.getCurrent().showNotification(error);
			return;
		}
		doBack();
	}

	private void doBack() {
		if (lnk != null)
			UI.getCurrent().getPage().setLocation( getNavLink() );
		else
			GuiUtil.getApi().navigateBack();
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}

	public String getNavLink() {
		return lnk;
	}
	
	

}
