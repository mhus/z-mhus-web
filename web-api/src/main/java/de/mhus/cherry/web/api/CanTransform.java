package de.mhus.cherry.web.api;

import java.io.File;

public interface CanTransform {

	void doTransform(CallContext context, String template) throws Exception;

	File getTemplateRoot();

}
