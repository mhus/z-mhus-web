package de.mhus.cherry.portal.demo;

import java.util.UUID;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoConst;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoUtil;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.CreateConfiguration;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = EditorFactory.class, name="cherry_editor_de.mhus.cherry.portal.demo.simpleeditorfactory")
public class SimpleEditorFactory extends MLog implements EditorFactory {

	@Override
	public EditorPanel createEditor(CaoWritableElement data) {
		return new SimpleEditor(data);
	}

	@Override
	public AbstractComponent createPreview(CaoNode res) {
		return null;
	}

	@Override
	public boolean deletePage(CaoNode nav) {
		
		CaoNode parent = nav.getParent();
		if (parent == null) return false; // can't delete root
		try {
			String resId = nav.getString(CherryApi.RESOURCE_ID, null);
			VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
			CaoNode res = vHost.getResourceResolver().getResource(vHost, resId);
			CaoUtil.deleteRecursive(res, 100);
		} catch (Throwable t) {
			log().d(t);
		}

		try {
			CaoUtil.deleteRecursive(nav, 100);
			return true;
		} catch (Throwable t) {
			log().d(t);
		}

		return false;
	}

	@Override
	public CaoNode createWidget(CaoNode parent, String title) {
		return null;
		
	}
	
	@Override
	public CaoNode createPage(CaoNode parent, String title) {
		
		try {
			String name = MFile.normalize(title);
			CaoNode newNav = null;
			CaoNode newRes = null;
			CaoNode newContent = null;
			{	// Create Res Node
				ResourceProvider provider = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getResourceProvider(CherryApi.DEFAULT_RESOURCE_PROVIDER);
				CaoNode root = provider.getResource("/");
				// Maybe create sub folder
				CaoAction action = root.getConnection().getAction(CaoAction.CREATE);
				CaoConfiguration config = action.createConfiguration(root, null);
				config.getProperties().setString(CreateConfiguration.NAME, name + "_" + UUID.randomUUID() + ".page");
				config.getProperties().setString(WidgetApi.RES_TITLE, title); //not needed
				
				OperationResult result = action.doExecute(config, null);
				if (!result.isSuccessful()) return null;
 				newRes = result.getResultAs(CaoNode.class);
			}
			
			{	// Create Content
				CaoAction action = newRes.getConnection().getAction(CaoAction.CREATE);
				CaoConfiguration config = action.createConfiguration(newRes, null);
				config.getProperties().setString(CreateConfiguration.NAME, "content");
				config.getProperties().setString(WidgetApi.RES_TITLE, title);
				config.getProperties().setString(WidgetApi.RENDERER, "de.mhus.cherry.portal.impl.page.SimplePage");
				config.getProperties().setString(WidgetApi.EDITOR, "de.mhus.cherry.portal.demo.simpleeditorfactory");
				OperationResult result = action.doExecute(config, null);
				if (!result.isSuccessful()) return null;
				newContent = result.getResultAs(CaoNode.class);
			}
			
			{	// Create Nav Node
				CaoAction action = parent.getConnection().getAction(CaoAction.CREATE);
				CaoConfiguration config = action.createConfiguration(parent, null);
				config.getProperties().setString(CreateConfiguration.NAME, name);
				config.getProperties().setString(CherryApi.NAV_TITLE, title);
				config.getProperties().setBoolean(CherryApi.NAV_HIDDEN, true);
				config.getProperties().setString(CherryApi.RESOURCE_ID, newRes.getId() );
				OperationResult result = action.doExecute(config, null);
				if (!result.isSuccessful()) return null;
				newNav = result.getResultAs(CaoNode.class);
			}
			
			
			return newNav;
		} catch (Throwable t) {
			log().d(t);
		}
		return null;
	}

	@Override
	public boolean deleteWidget(CaoNode res) {
		return false;
	}

	@Override
	public boolean isPage() {
		return true;
	}

	@Override
	public boolean isWidget() {
		return false;
	}

	@Override
	public LayoutPanel createLayoutPanel(CaoNode res) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "simplePage";
	}

}
