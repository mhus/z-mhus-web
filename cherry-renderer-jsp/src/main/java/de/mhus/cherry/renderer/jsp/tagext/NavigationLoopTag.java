package de.mhus.cherry.renderer.jsp.tagext;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;

public class NavigationLoopTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private Iterator<CaoNode> iterator;
	private Navigation nav;
	private String iteratorName;

	public void setIterator(String iteratorName) {
		this.iteratorName = iteratorName;
	}

	@Override
	public int doStartTag() throws JspException {
		
		nav = NavigationStack.getStack(pageContext).getCurrent();
		
		iterator = nav.getNodes().iterator();
	    if(iterate()) {
	    	return EVAL_BODY_INCLUDE;
	    }
	    return SKIP_BODY;

	}

	private boolean iterate() throws JspException {
		if (!iterator.hasNext()) return false;
	    try{
	    	nav.setCurrent(iterator.next());
	    	if (iteratorName != null)
	    		pageContext.setAttribute(iteratorName, nav.getCurrent());
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