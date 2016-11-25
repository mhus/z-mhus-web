package de.mhus.cherry.portal.impl;

import java.lang.reflect.Field;

import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.core.lang.MObject;

public abstract class AbstractEditorFactory extends MObject implements EditorFactory{

	private String ident;

	@Override
	public String getCaption() {
		return nls("caption=" + getName());
	}
	
	@Override
	public String getIdent() {
		if (ident == null) {
			try {
				Field field = getClass().getDeclaredField("NAME");
				ident = (String)field.get(null);
			} catch (Exception e) {
				log().t("Static field NAME not found in class",getClass(),e.toString());
				ident = getClass().getCanonicalName().toLowerCase();
			}
		}
		return ident;
	}

}
