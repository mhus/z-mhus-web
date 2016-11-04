package de.mhus.cherry.renderer.jsp.tagext;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.cao.CaoNode;

public class NavigationTagHandler extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private Collection<CaoNode> nodes;
	private String iteratorName;
	private boolean showHidden = false;
	private String order = null;

	public void setResource(CaoNode res) {
		this.res = res;
	}

	public void setNavigation(String iteratorName) {
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
    	pageContext.setAttribute(iteratorName, "" );
    	return EVAL_BODY_INCLUDE;
	}
}
