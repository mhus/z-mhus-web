package de.mhus.cherry.web.api;

import java.io.OutputStream;

public interface InternalCallContext extends CallContext {

	/**
	 * Insert a outputs tream in the chain. This stream will become the next current os.
	 * @param os
	 */
	void setOutputStream(OutputStream os);

}
