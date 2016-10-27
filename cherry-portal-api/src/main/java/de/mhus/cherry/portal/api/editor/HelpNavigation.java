package de.mhus.cherry.portal.api.editor;

import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Button.ClickEvent;

import de.mhus.lib.core.MString;

public class HelpNavigation extends HorizontalLayout {
	
	private static final long serialVersionUID = 1L;

	public HelpNavigation(Map<String, Command> commands, String helpUrl) {
		
		if (commands != null && !commands.isEmpty()) {
			MenuBar menuBar = new MenuBar();
			MenuItem menu = menuBar.addItem("Menü", null);
			menu.setIcon(FontAwesome.NAVICON);
			for (Entry<String, Command> entry : commands.entrySet()) {
				menu.addItem(entry.getKey(), entry.getValue());
			}
			addComponent(menuBar);
			setComponentAlignment(menuBar, Alignment.MIDDLE_CENTER);
		}
		
		if (MString.isSetTrim(helpUrl)) {
			Button helpBtn = new Button("Hilfe");
			helpBtn.setIcon(FontAwesome.QUESTION);
			helpBtn.setDescription("Hier klicken, um Hilfe zu erhalten");
			helpBtn.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().getPage().open(helpUrl, "_blank", false);
				}
			});
			addComponent(helpBtn);
			setComponentAlignment(helpBtn, Alignment.MIDDLE_CENTER);
		}
		
		setHeight(100, Unit.PERCENTAGE);
		setSpacing(true);
	}
}
