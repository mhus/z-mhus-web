package de.mhus.cherry.portal.impl.renderer;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.cfg.CfgInt;
import de.mhus.osgi.sop.api.rest.JsonResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_json")
public class DefaultGetJsonRenderer extends MLog implements ResourceRenderer {

	private static final CfgInt maxLevel = new CfgInt(DefaultGetJsonRenderer.class, "maxLevel", 100);

	@Override
	public void doRender(CallContext call) throws Exception {
		try {
			IProperties s = call.getSelectors();
			JsonResult result = new JsonResult();
			ObjectNode out = result.createObjectNode();
			CaoNode node = call.getResource();
			
			int level = 0;
			if (s != null) {
				
				if ("infinity".equals(s.getString("0","")))
					level = maxLevel.value();
				else
					level = Math.min( s.getInt("0", 0), maxLevel.value());
				
				if (s != null && "nav".equals(s.getString("resource", "")) )
					node = call.getNavigationResource().getNav();
				
			}
			doFill(node, out, level);
			
			HttpServletResponse response = call.getHttpResponse();
//			response.setHeader("Cache-Control","max-age=0");
			response.setHeader("Cache-Control","no-cache, must-revalidate"); //HTTP 1.1
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Expires","Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
			  
			response.setContentType(result.getContentType());
			result.write(call.getHttpResponse().getWriter());
		} catch (Exception e) {
			log().d(e);
		}

	}

	private void doFill(CaoNode node, ObjectNode out, int level) {
		for (String key : node.getPropertyKeys()) {
			Object value = node.get(key);
			MJson.setValue(out, key, value);
		}
		if (level <= 0) return;
		for (CaoNode child : node.getNodes()) {
			try {
				ObjectNode next = out.objectNode();
				out.put(child.getName(), next);
				doFill(child, next, level-1);
			} catch (Exception e) {
				log().d(child,e);
			}
		}
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
	}

}
