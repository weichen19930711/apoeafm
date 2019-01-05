package com.perficient.library.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.model.DamagedRecord;
import com.perficient.library.core.model.Employee;

public interface DamagedRecordService extends BaseService<DamagedRecord, Integer> {
    
    List<DamagedRecord> findNotPaidDamagedRecordsByEmployee(Employee employee);
    
    Page<DamagedRecord> getDamagedRecords(Specification<DamagedRecord> spec, Pageable pageable);

    void exportDamagedRecords(Object object);
}
