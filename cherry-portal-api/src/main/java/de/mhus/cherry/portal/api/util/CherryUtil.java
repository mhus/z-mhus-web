package de.mhus.cherry.portal.api.util;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.basics.Named;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.sop.api.Sop;

public class CherryUtil {

	private static Log log = Log.getLog(CherryUtil.class);
	
	public static ScriptRenderer getScriptRenderer(CallContext call, File file) {
		return  call.getVirtualHost().getScriptRenderer(MFile.getFileSuffix(file));
	}

	public static <T extends Named> List<T> orderServices(Class<?> where, Class<T> what) {
		return order(where, what, MOsgi.getServices(what, null));
	}
	public static <T extends Named> List<T> order(Class<?> where, Class<?> what, List<T> list) {
		return order(where.getCanonicalName() + ":" + what.getCanonicalName(), list);
	}
	public static <T extends Named> List<T> order(String configListName, List<T> list) {
		CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
		if (call == null) return list;
		VirtualHost vHost = call.getVirtualHost();
		if (vHost == null) return list;
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

}
