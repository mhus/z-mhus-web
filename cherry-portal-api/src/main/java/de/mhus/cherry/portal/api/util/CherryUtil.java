package de.mhus.cherry.portal.api.util;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.sop.api.Sop;

public class CherryUtil {

	private static Log log = Log.getLog(CherryUtil.class);

	public static final String NODE = "caonode";
	
	public static CaoNode[] getNodeFromProperties(IProperties prop) {
		return (CaoNode[])prop.get(NODE);
	}
	
	public static ScriptRenderer getScriptRenderer(CallContext call, File file) {
		return  call.getVirtualHost().getScriptRenderer(MFile.getFileSuffix(file));
	}
	
	public static <T extends Named> List<T> orderServices(Class<?> where, Class<T> what) {
		return order(where, what, MOsgi.getServices(what, null));
	}
	
	public static <T extends Named> List<T> order(Class<?> where, Class<?> what, List<T> list) {
		return order(where.getCanonicalName() + ":" + what.getCanonicalName(), list);
	}

	public static <T extends Named> List<T> orderServices(Class<?> where, Class<T> what, VirtualHost vHost) {
		return order(where, what, MOsgi.getServices(what, null), vHost);
	}
	
	public static <T extends Named> List<T> order(Class<?> where, Class<?> what, List<T> list, VirtualHost vHost) {
		return order(where.getCanonicalName() + ":" + what.getCanonicalName(), list, vHost);
	}
	
	public static <T extends Named> List<T> order(String configListName, List<T> list) {
		CallContext call = MApi.lookup(CherryApi.class).getCurrentCall();
		if (call == null) return list;
		VirtualHost vHost = call.getVirtualHost();
		if (vHost == null) return list;
		return order(configListName, list, vHost);
	}
	
	public static <T extends Named> List<T> order(String configListName, List<T> list, VirtualHost vHost) {

		List<String> conf = vHost.getConfigurationList(configListName);
		if (conf == null) {
			log.d("Configuration list not found", configListName, vHost);
			// order !!
			list.sort(new Comparator<T>() {

				@Override
				public int compare(T o1, T o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			return list;
		}
		
		// to Map
		TreeMap<String, T> map = new TreeMap<>();
		for (T item : list)
			map.put(item.getName(), item);
		
		// order
		list = new LinkedList<>();
		for (String name : conf) {
			if (name.equals("*")) {
				for (T item : map.values())
					list.add(item);
				break;
			} else
			if (name.startsWith("!")) {
				map.remove(name.substring(1));
			} else {
				T item = map.get(name);
				if (item != null) {
					list.add(item);
					map.remove(name);
				}
			}
		}

		return list;
	}

	public static CallContext prepareHttpRequest(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {

		try {
		
			CallContext call = MApi.lookup(InternalCherryApi.class).createCall(servlet, request, response);
			if (response != null && response.isCommitted()) return null;
			
			return call;
			
		} catch (Throwable t) {
			MLogUtil.log().w(t);
			return null;
		}
		
	}

	public static CaoNode[] getCurrent(NavNode[] navNode) {
		if (navNode == null) return null;
		CaoNode[] out = new CaoNode[navNode.length];
		for (int i = 0; i < out.length; i++)
			out[i] = navNode[i].getCurrent();
		return out;
	}

	public static boolean isNavigationNode(VirtualHost vHost, CaoNode resource) {
		return resource != null && resource.getPath().indexOf("/_") < 0;
	}

	public static boolean isPageNode(Object object, CaoNode resource) {
		return resource != null && resource.getName().startsWith("_");
	}

	public static String getPublicDeployUrl(Object owner, String path) {
		if (owner == null || path == null) return null;
		Bundle bundle = FrameworkUtil.getBundle(owner.getClass());
		DeployDescriptor desc = MApi.lookup(CherryApi.class ).getDeployDescritor( bundle ); 
		if (desc == null) return null;
		return desc.getWebPath(SPACE.PUBLIC) + path;
	}

}
