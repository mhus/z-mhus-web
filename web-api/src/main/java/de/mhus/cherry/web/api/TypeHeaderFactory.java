package de.mhus.cherry.web.api;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface TypeHeaderFactory {

    /**
     * Return a header object if the factory is able to handle the header type.
     * 
     * @param header 
     * @return a typeheader if the config is accepted
     * @throws MException 
     */
    TypeHeader create(IConfig header) throws MException;
    
}
