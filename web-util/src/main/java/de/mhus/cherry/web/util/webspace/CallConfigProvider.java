package de.mhus.cherry.web.util.webspace;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.lib.core.IReadProperties;

public interface CallConfigProvider {
    IReadProperties findConfig(CallContext call);
}
