package de.mhus.osgi.cherry.processor.jsp;

import java.net.URLClassLoader;

import org.ops4j.pax.web.jsp.JasperClassLoader;
import org.osgi.framework.Bundle;

public class MyJasperClassLoader extends JasperClassLoader {

	public MyJasperClassLoader(Bundle bundle, ClassLoader hostClassLoader) {
		super(bundle, hostClassLoader);
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
//		if (name.startsWith("javax.servelt.jsp."))
//			return getParent().loadClass(name);
		return super.loadClass(name);
	}

}
