package de.mhus.cherry.renderer.jsp.tagext;

import java.awt.List;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

public class WidgetsTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private Collection<CaoNode> nodes;
	private String iteratorName;
	private Iterator<CaoNode> iterator;
	private String name;
	private String container;

	public void setName(String name) {
		this.name = name;
	}

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setIterator(String iteratorName) {
		this.iteratorName = iteratorName;
	}
	
	public void setContainer(String container) {
		this.container = container;
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		res = Util.findRes(res, name, pageContext);

		if (container != null) {
			nodes = Sop.getApi(WidgetApi.class).sortWidgetsIntoContainers(res, container).getNodes();
		} else {
			nodes = Sop.getApi(WidgetApi.class).sortWidgets(res).getNodes();
		}
		nodes = res.getNodes();
		
		iterator = nodes.iterator();

	    if(iterate()) {
	    	return EVAL_BODY_INCLUDE;
	    }
	    return SKIP_BODY;
	}
	
	private boolean iterate() throws JspException {
		if (!iterator.hasNext()) return false;
	    try{
	    	pageContext.setAttribute(iteratorName, iterator.next() );
	    } catch(Exception e){
	      throw new JspException(e.toString());
	    }
	    return true;
	}

	@Override
	public int doAfterBody() throws JspException {
		
	    if(iterate()) {
	    	return EVAL_BODY_AGAIN;
	    }
	    return SKIP_BODY;
	  }
	
}
