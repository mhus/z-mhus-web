package de.mhus.cherry.portal.demo;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.AbstractEditorFactory;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = EditorFactory.class, name="cherry_editor_" + SimpleEditorFactory.NAME)
public class SimpleEditorFactory extends AbstractEditorFactory implements EditorFactory {

	public final static String NAME = "de.mhus.cherry.portal.demo.simpleeditorfactory";
	
	@Override
	public EditorPanel createEditor(CaoWritableElement data) {
		return new SimpleEditor(data);
	}

	@Override
	public AbstractComponent createPreview(CaoNode res) {
		String url = CherryUtil.getPublicDeployUrl(this, "/img/widget.png");
		return new Image("",new ExternalResource( url ));
	}

	@Override
	public boolean doPrepareCreatedWidget(CaoNode content) {
		try {
			CaoWritableElement w = content.getWritableNode();
			w.setString(CherryApi.RES_RET_TYPE, CherryApi.RET_TYPE_PAGE);
			w.setString(WidgetApi.RENDERER, "de.mhus.cherry.portal.impl.page.SimplePage");
			w.setString(WidgetApi.EDITOR, "de.mhus.cherry.portal.demo.simpleeditorfactory");
			OperationResult res = w.getUpdateAction().doExecute(null);
			return res != null && res.isSuccessful();
		} catch (Throwable t) {
			log().d(t);
		}
		return false;
/*		
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
*/		
	}

	@Override
	public LayoutPanel createLayoutPanel(CaoNode res) {
		if (res.getName().startsWith("_")) {
			return new SimplePageLayout(res);
		}
		return null;
	}

	@Override
	public String getName() {
		return "SimplePage";
	}

	@Override
	public boolean doDeleteWidget(CaoNode res) {
		return false;
	}

	@Override
	public TYPE getType() {
		return TYPE.PAGE;
	}

}
