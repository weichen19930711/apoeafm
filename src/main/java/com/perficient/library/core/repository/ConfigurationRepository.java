package com.perficient.library.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

}
