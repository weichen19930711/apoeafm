package com.perficient.library.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.LostRecord;

@Repository
public interface LostRecordRepository extends JpaRepository<LostRecord, Integer>, JpaSpecificationExecutor<LostRecord> {

    List<LostRecord> findByEmployeeAndIsPaid(Employee employee, Boolean isPaid);
}
