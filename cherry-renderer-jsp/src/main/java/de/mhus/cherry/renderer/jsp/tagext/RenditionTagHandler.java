package de.mhus.cherry.renderer.jsp.tagext;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.MLogUtil;

public class RenditionTagHandler extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private String rendition;

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setRendition(String rendition) {
		this.rendition = rendition;
	}

	@Override
	public int doStartTag() throws JspException {
		
		try {
			InputStream is = res.getInputStream(rendition);
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
		
}
