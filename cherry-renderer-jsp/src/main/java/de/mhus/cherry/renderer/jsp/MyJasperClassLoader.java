package de.mhus.cherry.renderer.jsp;

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
		try {
			Class<?> ret = super.loadClass(name);
			return ret;
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}

}
