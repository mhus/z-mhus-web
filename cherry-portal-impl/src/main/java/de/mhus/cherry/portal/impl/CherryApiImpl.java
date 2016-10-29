package de.mhus.cherry.portal.impl;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.impl.deploy.CherryDeployServlet;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;

@Component
public class CherryApiImpl extends MLog implements CherryApi {

	@Override
	public VirtualHost findVirtualHost(String host) {
		VirtualHost provider = null;
		try {
			provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_" + host));
		} catch (NotFoundException e) {}
		if (provider == null) {
			try {
				provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_default"));
			} catch (NotFoundException e) {}
		}
		
		return provider;
	}

	@Override
	public FileDeployer findFileDeployer(String suffix) {
		if (suffix == null) return null;
		suffix = suffix.toLowerCase();
		FileDeployer deployer = null;
		try {
			deployer = MOsgi.getService(FileDeployer.class, MOsgi.filterServiceName("cherry_file_deployer_" + suffix));
		} catch (NotFoundException e) {}
		return deployer;
	}

	@Override
	public String getMimeType(String file) {
		return CherryDeployServlet.instance.getServletContext().getMimeType(file);
	}

	@Override
	public DeployDescriptor getDeployDescritor(String symbolicName) {
		return CherryDeployServlet.instance.getDescriptor(symbolicName);
	}

}
