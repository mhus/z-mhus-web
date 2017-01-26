package de.mhus.cherry.portal.impl.operation;

import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.TaskContext;

public interface PasteAction {

	OperationResult doExecute(TaskContext context) throws Exception;
	
}
