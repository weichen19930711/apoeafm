package com.perficient.library.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.DamagedRecord;
import com.perficient.library.core.model.Employee;

@Repository
public interface DamagedRecordRepository
    extends JpaRepository<DamagedRecord, Integer>, JpaSpecificationExecutor<DamagedRecord> {

    List<DamagedRecord> findByEmployeeAndIsPaid(Employee employee, Boolean isPaid);
}
