package com.perficient.library.core.service;

import com.perficient.library.core.model.Configuration;

public interface ConfigurationService {

    Configuration save(Configuration configuration);
    
    Configuration get();
    
}
