package de.mhus.cherry.editor.impl;

import java.util.LinkedList;
import java.util.Map;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.editor.GuiSpaceService;
import de.mhus.cherry.portal.api.editor.GuiUtil;
import de.mhus.cherry.portal.api.editor.Navigatable;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.util.Rfc1738;

public class Desktop extends CssLayout {

	private CherryUi ui;
	private MenuBar menuBar;
	private MenuItem menuSpaces;
	private VerticalLayout contentScreen;
	private MenuItem menuCurrent;
	private MenuItem menuLeave;
	private MenuItem menuUser;
	private MenuItem menuLogout;
	protected GuiSpaceService currentSpace;
	private MenuItem menuOverview;
	private GridLayout overView;
	private MenuItem menuTrace;
	private static Log log = Log.getLog(Desktop.class);

	public Desktop(CherryUi cherryUi) {
		ui = cherryUi;
		initGui();
	}

	private void initGui() {
		
		overView = new GridLayout();
		overView.setSizeFull();
		overView.setMargin(true);
		overView.setSpacing(true);
		overView.setStyleName("overview");
		
		menuBar = new MenuBar();
		menuSpaces = menuBar.addItem("Bereiche", null);

		menuCurrent = menuBar.addItem("", null);
		
		menuUser = menuBar.addItem( ui.getAccessControl().getPrincipalName(), null);
		menuUser.setStyleName("right");
		menuLogout = menuUser.addItem("Logout", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				try {
					ui.getAccessControl().signOut();
				} catch (Throwable t) {
					log.d(t);
				}
				try {
					UI.getCurrent().close();
				} catch (Throwable t) {
					log.d(t);
				}
				UI.getCurrent().getPage().reload();
			}
		});
		
		menuTrace = menuUser.addItem("Trace An", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				if (ui.getTrailConfig() == null) {
					ui.setTrailConfig("MAP");
					menuTrace.setText("Trace Aus (" + MLogUtil.getTrailConfig() + ")");
				} else {
					ui.setTrailConfig(null);
					menuTrace.setText("Trace An");
				}
			}
		});
		
		setStyleName("desktop-screen");
		menuBar.setStyleName("menubar");
		
		addComponent(menuBar);
		
		contentScreen = new VerticalLayout();
		contentScreen.addStyleName("content");
		contentScreen.setSizeFull();
		addComponent(contentScreen);
		setSizeFull();
		
		showOverview();
	}

	public void refreshSpaceList(Map<String, GuiSpaceService> spaceList) {
		menuSpaces.removeChildren();
		overView.removeAllComponents();
		
		menuOverview = menuSpaces.addItem("Übersicht", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				showOverview();
			}
		});

		menuSpaces.addSeparator();
		
		LinkedList<GuiSpaceService> componentList = new LinkedList<>();
		for (GuiSpaceService space : spaceList.values()) {
			
			if (!hasAccess(space) || !space.hasAccess(ui.getAccessControl())) continue;
			componentList.add(space);
		}
		

		overView.setColumns(Math.max(1, (int)Math.sqrt( componentList.size() ) ) );
		overView.setRows( overView.getColumns() );

		for (final GuiSpaceService space : componentList ) {
			NativeButton button = new NativeButton();
			button.setHtmlContentAllowed(false);
			button.setCaption( space.getDisplayName());
			button.setStyleName("thumbnail");
			overView.addComponent(button);
			button.addClickListener(new NativeButton.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					showSpace(space, null, null);
				}
			});
			
			MenuItem item = menuSpaces.addItem(space.getDisplayName(), new MenuBar.Command() {
				
				@Override
				public void menuSelected(MenuItem selectedItem) {
					showSpace(space, null, null);
				}
			});
			item.setEnabled(true);
		}
		
		if (componentList.size() > 0)
			menuSpaces.addSeparator();
		
		menuLeave = menuSpaces.addItem("Verlassen", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				if (currentSpace == null) return;
				ui.removeSpaceComponent(currentSpace.getName());
				currentSpace = null;
				showOverview();
			}
		});
		menuLeave.setEnabled(false);

	}

	protected boolean hasAccess(GuiSpaceService space) {
		return GuiUtil.getApi().hasAccess(space.getName());
	}

	protected void showSpace(GuiSpaceService space, String subSpace, String search) {
		AbstractComponent component = ui.getSpaceComponent(space.getName());
		
		contentScreen.removeAllComponents();
		menuCurrent.removeChildren();
		
		if (component == null) {
			contentScreen.addComponent(new Label("Der Space ist aktuell nicht erreichbar " + space.getName()));
			addComponent(contentScreen);
			return;
		}
		
		component.setSizeFull();
		contentScreen.addComponent(component);
		
		menuCurrent.setText(space.getDisplayName());
		menuLeave.setEnabled(true);
		currentSpace = space;
		space.createMenu(menuCurrent);
		
		if (component instanceof Navigatable && (MString.isSet(subSpace) || MString.isSet(search)))
			((Navigatable)component).navigateTo(subSpace, search);
	}

	protected void showOverview() {
		if (menuLeave != null) menuLeave.setEnabled(false);
		contentScreen.removeAllComponents();
		menuCurrent.setText("Übersicht");
		currentSpace = null;
		contentScreen.addComponent(overView);
		
		//UI.getCurrent().getPage().setUriFragment("moin",false);
		String nav = UI.getCurrent().getPage().getUriFragment();
		String lnk = nav;
		if (MString.isIndex(lnk, ':')) lnk = MString.beforeIndex(lnk, ':');
		Label l = new Label();
		l.setCaptionAsHtml(true);
		l.setCaption("<a href='" + lnk + "'>" + MXml.encode(nav) + "</a>" );
		
		contentScreen.addComponent(l);
	}

}
