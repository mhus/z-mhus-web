package de.mhus.cherry.editor.impl.editor;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.Editor;
import de.mhus.cherry.portal.api.EditorFactory;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;

public class EditorSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private static Log log = Log.getLog(EditorSpace.class);
	private static final long serialVersionUID = 1L;
	private String nav;
	private String lnk;
	private Panel panel;
	private VerticalLayout contentLayout;
	private CaoNode resource;
	private Editor editor;
	private Button bSave;
	private Button bCancel;
	private String resId;

	@Override
	public boolean navigateTo(String selection, String filter) {
		return false;
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
		contentLayout = new VerticalLayout();
		panel.setContent(contentLayout);
		
//		contentLayout.addComponent(l);
		
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost( GuiUtil.getApi().getHost() );
		resource = vHost.getResourceResolver().getResource(vHost, resId);
		if (resource == null) {
			// resource not found
			return;
		}
		EditorFactory factory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost,resource);
		if (factory == null) {
			// editor not found
			return;
		}
		
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
			return;
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
	}

	protected void doCancel() {
		UI.getCurrent().getPage().setLocation( getNavLink() );
	}

	protected void doSave() {
		String error = editor.doSave();
		if (error != null) {
			UI.getCurrent().showNotification(error);
			return;
		}
		UI.getCurrent().getPage().setLocation( getNavLink() );
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}

	public String getNavLink() {
		return lnk;
	}
	
	

}
