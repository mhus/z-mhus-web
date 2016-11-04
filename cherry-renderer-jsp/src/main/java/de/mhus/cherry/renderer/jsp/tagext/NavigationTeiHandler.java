package de.mhus.cherry.renderer.jsp.tagext;

import java.util.Collection;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class NavigationTeiHandler extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData data) {

		String name = data.getAttributeString("navigation");
        return new VariableInfo[] { 
        		new VariableInfo(name, Collection.class.getName(), true, VariableInfo.NESTED)
        };
    }

}
