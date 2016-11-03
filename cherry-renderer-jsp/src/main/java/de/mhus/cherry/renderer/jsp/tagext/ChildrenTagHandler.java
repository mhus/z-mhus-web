package de.mhus.cherry.renderer.jsp.tagext;

import java.awt.List;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;

public class ChildrenTagHandler extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private Collection<CaoNode> nodes;
	private String iteratorName;
	private Iterator<CaoNode> iterator;
	private boolean showHidden = false;
	private String order = null;

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setIterator(String iteratorName) {
		this.iteratorName = iteratorName;
	}
	
	public void setShowHidden(boolean showHidden) {
		this.showHidden  = showHidden;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	@Override
	public int doStartTag() throws JspException {
		nodes = res.getNodes();
		
		if (!showHidden) {
			// remove hidden elements
			for (Iterator<CaoNode> iter = nodes.iterator(); iter.hasNext();) {
				CaoNode n = iter.next();
				if (n.getBoolean("hidden", false))
					iter.remove();
			}
		}
		
		if (order != null) {
			LinkedList<CaoNode> list = new LinkedList<>( nodes );
			list.sort(new Comparator<CaoNode>() {

				@Override
				public int compare(CaoNode o1, CaoNode o2) {
					
					String s1 = o1.getString(order, null);
					String s2 = o2.getString(order, null);
					if (s1 == null && s2 == null) return 0;
					if (s1 == null) return -1;
					return s1.compareTo(s2);
				}
			});
			nodes = list;
		}

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
