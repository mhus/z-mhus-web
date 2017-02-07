package de.mhus.cherry.renderer.jsp.tagext;

import java.util.LinkedList;

import javax.servlet.jsp.PageContext;

public class NavigationStack {

	private static final String NAVIGATION_STACK = "navigation_stack";
	private LinkedList<Navigation> list = new LinkedList<>();
	
	public static synchronized NavigationStack getStack(PageContext pageContext) {
		NavigationStack stack = (NavigationStack)pageContext.getAttribute(NAVIGATION_STACK);
		if (stack == null) {
			stack = new NavigationStack();
			pageContext.setAttribute(NAVIGATION_STACK, stack);
		}
		return stack;
	}
	
	public void push(Navigation nav) {
		list.add(nav);
	}
	
	public Navigation pop() {
		if (list.size() > 0)
			return list.removeLast();
		return null;
	}
	
	public Navigation getCurrent() {
		if (list.size() > 0)
			return list.getLast();
		return null;
	}

}
