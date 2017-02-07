package de.mhus.cherry.editor.impl.data;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.ComponentContext;

import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.lib.core.MSoftTimerTask;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.security.AccessControl;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;

@Component(immediate=true,provide=GuiSpaceService.class)
public class DataSpaceService extends GuiSpace {

	private Timer timer;

    @Activate
    public void activate(ComponentContext ctx) {
    	timer = new Timer(true);
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	if (timer != null) timer.cancel();
    	timer = null;
    }

	@Override
	public String getName() {
		return "data";
	}

	@Override
	public String getDisplayName() {
		return "Data";
	}

	@Override
	public AbstractComponent createSpace() {
		return new DataSpace();
	}

	@Override
	public boolean hasAccess(AccessControl control) {
		return true;
	}

	@Override
	public void createMenu(AbstractComponent space, MenuItem[] menu) {
		
	}

	@Override
	public boolean isHiddenSpace() {
		return false;
	}

	@Override
	public AbstractComponent createTile() {
			VerticalLayout tile = new VerticalLayout() {
				private Label mem;
				{
					addComponent(new Label("Data") );
					mem = new Label();
					addComponent(mem);
					
			    	timer.schedule(new MSoftTimerTask(new TimerTask() {

						@Override
						public void run() {
							mem.setCaption("Memory: " + MSystem.memDisplayString() );
						}
			    		
			    	}), 1000, 5000);

				}
			};
//			tile.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
//				
//				@Override
//				public void layoutClick(LayoutClickEvent event) {
//					GuiUtil.getApi().openSpace(getName(), null, null);
//				}
//			});
		return tile;
	}

	@Override
	public int getTileSize() {
		return 2;
	}

	@Override
	public boolean isHiddenInMenu() {
		return true;
	}

}
