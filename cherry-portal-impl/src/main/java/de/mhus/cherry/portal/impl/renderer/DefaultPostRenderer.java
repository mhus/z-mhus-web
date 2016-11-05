package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoOperation;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.strategy.DefaultTaskContext;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.form.MForm;
import de.mhus.osgi.sop.api.rest.JsonResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post")
public class DefaultPostRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Create = POST to a base URI returning a newly created URI
	

	@Override
	public OperationResult doAction(CallContext call) throws IOException, CaoException {
	
		CaoNode res = call.getResource();
		
		CaoAction action = res.getConnection().getActions().getAction(CaoAction.CREATE);
		CaoList list = new CaoList(null);
		list.add(res);
		
		// payload
		MProperties co = new MProperties();
		HttpServletRequest req = call.getHttpRequest();
		for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
			String[] v = entry.getValue();
			if (v.length > 0)
				co.setString(entry.getKey(), entry.getValue()[0]);
			
		}
		
		CaoOperation oper = action.createOperation(list, co);
		OperationResult operRes = oper.doExecute(co);
		return operRes;
	}

}
