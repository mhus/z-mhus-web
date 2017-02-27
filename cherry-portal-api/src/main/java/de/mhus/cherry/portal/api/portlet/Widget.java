package de.mhus.cherry.portal.api.portlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.portal.api.control.EditorFactory;

@Retention(RetentionPolicy.RUNTIME)
public @interface Widget {

	Class<? extends EditorFactory> editor();
	EditorFactory.TYPE type() default EditorFactory.TYPE.WIDGET;
	String displayName() default "";

}
