package de.mhus.cherry.portal.api;

import java.io.File;
import java.net.URL;

import de.mhus.lib.core.MProperties;

public interface FileDeployer {

	void doDeploy(File root, String path, URL entry, MProperties config);

}
