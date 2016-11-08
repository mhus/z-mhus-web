package de.mhus.cherry.renderer.jsp.tagext;

import java.util.LinkedList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;

public class PathTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private LinkedList<CaoNode> nodes = new LinkedList<>();
	private String iteratorName;
	private String name;

	public void setResource(CaoNode res) {
		this.res = res;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setIterator(String iteratorName) {
		this.iteratorName = iteratorName;
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		res = Util.findRes(res, name, pageContext);
		
		CaoNode r = res;
		while (r != null) {
			nodes.add(r);
			r = r.getParent();
		}
		
	    if(iterate()) {
	    	return EVAL_BODY_INCLUDE;
	    }
	    return SKIP_BODY;
	}
	
	private boolean iterate() throws JspException {
		if (nodes.size() <= 0) return false;
	    try{
	    	pageContext.setAttribute(iteratorName, nodes.getLast() );
	    } catch(Exception e){
	      throw new JspException(e.toString());
	    }
	    nodes.removeLast();
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
