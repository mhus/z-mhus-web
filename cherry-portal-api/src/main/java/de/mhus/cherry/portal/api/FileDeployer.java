package de.mhus.cherry.portal.api;

import java.io.File;
import java.net.URL;

import de.mhus.lib.core.MProperties;

/**
 * Handler to deploy files into the local file store. e.g. unpack zip files
 * 
 * @author mikehummel
 *
 */
public interface FileDeployer {

	void doDeploy(File root, String path, URL entry, MProperties config);

}
