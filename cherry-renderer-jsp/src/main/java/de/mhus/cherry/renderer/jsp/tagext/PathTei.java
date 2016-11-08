package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import de.mhus.lib.cao.CaoNode;

public class PathTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData data) {

		String name = data.getAttributeString("iterator");
        return new VariableInfo[] { 
        		new VariableInfo(name, CaoNode.class.getName(), true, VariableInfo.NESTED)
        };
    }

}
