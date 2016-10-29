package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.directory.ResourceNode;

public class ChildrenTagHandler extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private ResourceNode[] nodes;
	  private int counter = 0;
	private String iteratorName;

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setIterator(String iteratorName) {
		this.iteratorName = iteratorName;
	}
	
	@Override
	public int doStartTag() throws JspException {
		nodes = res.getNodes();
	    if(iterate()) {
	    	return EVAL_BODY_INCLUDE;
	    }
	    return SKIP_BODY;
	}
	
	private boolean iterate() throws JspException {
		if (counter >= nodes.length) return false;
	    try{
	    	pageContext.setAttribute(iteratorName, nodes[counter] );
	    } catch(Exception e){
	      throw new JspException(e.toString());
	    }
	    counter++;
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
