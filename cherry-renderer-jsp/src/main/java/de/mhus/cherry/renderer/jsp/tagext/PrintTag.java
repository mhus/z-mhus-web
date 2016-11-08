package de.mhus.cherry.renderer.jsp.tagext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.MLogUtil;

public class PrintTag extends TagSupport {

	private CaoNode res;
	private String attribute;
	private String rendition;
	private String name;

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setAttribute(String name) {
		attribute = name;
	}
	
	public void setRendition(String name) {
		rendition = name;
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		res = Util.findRes(res, name, pageContext);
		
		if (rendition != null) {
			try {
				InputStream is = res.getInputStream(rendition.equals("") ? null : rendition);
				if (is != null) {
					MFile.copyFile(new InputStreamReader(is), pageContext.getOut());
					is.close();
					return SKIP_BODY;
				}
			} catch (Throwable t) {
				MLogUtil.log().d(res,rendition,t);
			}
			return EVAL_BODY_INCLUDE;
		}
		
		if (attribute != null) {
			
			try {
				String value = null;
				if (attribute.equals("#name"))
					value = res.getName();
				else
				if (attribute.equals("#id"))
					value = res.getId();
				else
				if (attribute.equals("#navlink")) {
					StringBuffer link = new StringBuffer();
					CaoNode cur = res;
					int max = 20;
					while (true) {
						max--;
						if (max < 0 || cur == null) break;
						if (cur.getParent() == null) break;
						link.insert(0, cur.getName() );
						link.insert(0, '/');
						
						cur = cur.getParent();
					}
					value = link.toString();
					
				} else
					value = res.getString(attribute, null);
				if (value != null) {
					pageContext.getOut().print( doEscape( value ) );
				}
			} catch (Exception e) {
				MLogUtil.log().d(res,rendition,e);
			}
			return EVAL_BODY_INCLUDE;
			
		}
		
		
		return EVAL_BODY_INCLUDE;
	}

	private String doEscape(String value) {
		// TODO implement it
		return value;
	}
}
