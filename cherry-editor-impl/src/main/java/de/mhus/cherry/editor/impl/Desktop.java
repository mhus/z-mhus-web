package de.mhus.cherry.editor.impl;

import java.util.LinkedList;
import java.util.Map;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.control.GuiSpaceService;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;

public class Desktop extends CssLayout {

	private ControlUi ui;
	private MenuBar menuBar;
	private MenuItem menuSpaces;
	private VerticalLayout contentScreen;
	private MenuItem[] menuSpace = new MenuItem[4];
	private MenuItem menuLeave;
	private MenuItem menuUser;
	private MenuItem menuLogout;
	protected GuiSpaceService currentSpace;
	private MenuItem menuOverview;
	private GridLayout overView;
	private MenuItem menuTrace;
	private MenuItem menuHistory;
	private MenuItem menuBack;
	private static Log log = Log.getLog(Desktop.class);

	public Desktop(ControlUi cherryUi) {
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

		menuHistory = menuBar.addItem("", null);
		menuBack = menuHistory.addItem("Back", new MenuBar.Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				GuiUtil.getApi().navigateBack();
			}
			
		});
		menuHistory.addSeparator();
		for (int i = 0; i < 15; i++)
			menuHistory.addItem("", new MenuBar.Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					String text = selectedItem.getDescription();
					if (MString.isSet(text)) {
						String[] parts = text.split("\\|", 4);
						if (parts.length == 4) {
							if (parts[2].equals("null")) parts[2] = null;
							if (parts[3].equals("null")) parts[3] = null;
							GuiUtil.getApi().openSpace(parts[1], parts[2], parts[3]);
						}
					}
				}
				
			});
		
		menuSpace[0] = menuBar.addItem("", null);
		menuSpace[1] = menuBar.addItem("", null);
		menuSpace[2] = menuBar.addItem("", null);
		menuSpace[3] = menuBar.addItem("", null);
		
		String name = "?";
		try {
			name = ui.getAccessControl().getAccount().getDisplayName();
		} catch (Throwable t) {}
		menuUser = menuBar.addItem( name, null);
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
		menuUser.addSeparator();
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
			
			if (space.isHiddenSpace() || !hasAccess(space) || !space.hasAccess(ui.getAccessControl())) continue;
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
					GuiUtil.getApi().openSpace(space.getName(), null, null); // not directly to support history
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

	protected String showSpace(GuiSpaceService space, String subSpace, String search) {
		AbstractComponent component = ui.getSpaceComponent(space.getName());
		
		contentScreen.removeAllComponents();
		cleanupMenu();
		if (component == null) {
			contentScreen.addComponent(new Label("Der Space ist aktuell nicht erreichbar " + space.getName()));
			addComponent(contentScreen);
			return null;
		}
		
		component.setSizeFull();
		contentScreen.addComponent(component);
		
		menuHistory.setText(space.getDisplayName());
		menuLeave.setEnabled(true);
		currentSpace = space;
		space.createMenu(component,menuSpace);
		
		if (component instanceof Navigable && (MString.isSet(subSpace) || MString.isSet(search)))
			return ((Navigable)component).navigateTo(subSpace, search);
		
		return space.getDisplayName();
	}

	protected void showOverview() {
		if (menuLeave != null) menuLeave.setEnabled(false);
		contentScreen.removeAllComponents();
		cleanupMenu();
		currentSpace = null;
		contentScreen.addComponent(overView);
	}

	private void cleanupMenu() {
		
		for (int i=0; i < menuSpace.length; i++) {
			menuSpace[i].removeChildren();
			menuSpace[i].setText("");
			menuSpace[i].setVisible(false);
		}
	}

	public void doUpdateHistoryMenu(LinkedList<String> history) {
		int cnt = -2;
		for (MenuItem c : menuHistory.getChildren()) {
			if (cnt >= 0) {
				if (history.size() - cnt - 1 < 0) {
					c.setText("");
					c.setDescription("");
					c.setIcon(null);
				} else {
					String x = history.get(history.size() - cnt - 1);
					c.setText(MString.beforeIndex(x, '|'));
					c.setDescription(x);
					c.setIcon(FontAwesome.ARROW_RIGHT);
				}
			}
			cnt++;
		}
	}

}
