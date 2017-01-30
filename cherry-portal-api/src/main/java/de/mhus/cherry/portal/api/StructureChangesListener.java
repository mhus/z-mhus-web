package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.util.DefaultChangesQueue.Change;

public interface StructureChangesListener {

	void navigationChanges(Change[] changes);
}
