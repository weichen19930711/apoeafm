package com.perficient.library.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;

@Repository
public interface OverdueRecordRepository
    extends JpaRepository<OverdueRecord, Integer>, JpaSpecificationExecutor<OverdueRecord> {

    OverdueRecord findByBorrowRecord(BorrowRecord borrowRecord);

    Page<OverdueRecord> findByBorrowRecordEmployee(Employee employee, Pageable pageable);

    List<OverdueRecord> findByReturned(boolean returned);

    Page<OverdueRecord> findByReturned(boolean returned, Pageable pageable);

    List<OverdueRecord> findByBorrowRecordEmployeeAndReturned(Employee employee, boolean returned);

    Page<OverdueRecord> findByBorrowRecordEmployeeAndReturned(Employee employee, boolean returned, Pageable pageable);

}
