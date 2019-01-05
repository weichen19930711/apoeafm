package com.perficient.library.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perficient.library.core.model.Configuration;
import com.perficient.library.core.repository.ConfigurationRepository;
import com.perficient.library.core.service.ConfigurationService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Override
    public Configuration save(Configuration entity) {
        return configurationRepository.save(entity);
    }

    @Override
    public Configuration get() {
        List<Configuration> all = configurationRepository.findAll();
        if (all == null || all.isEmpty()) {
            return null;
        }
        return all.get(0);
    }

}
