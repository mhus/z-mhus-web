package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;

public class NavigationLoopTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData data) {

        return new VariableInfo[] { 
        		new VariableInfo("resource", CaoNode.class.getName(), true, VariableInfo.NESTED)
        };
    }
	
}
