package com.perficient.library.core.service;

import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public interface BorrowRecordService extends BaseService<BorrowRecord, Integer> {

    Page<BorrowRecord> findByEmployee(Employee employee, Pageable pageable);

    List<BorrowRecord> findByBook(Book book);

    List<BorrowRecord> findByBookProperty(BookProperty property);

    Page<BorrowRecord> findAll(Pageable pageable);

    Page<BorrowRecord> findNotCheckedInBorrowRecords(Pageable pageable);

    Page<BorrowRecord> findCheckedOutBorrowRecords(Specification<BorrowRecord> spec, Pageable pageable);

    List<BorrowRecord> findNotCheckedInBorrowRecordsByEmployee(Employee employee);

    BorrowRecord findNotCheckedInBorrowRecordsByBook(Book book);

    void exportBorrowRecord(Object object);

    List<BorrowRecord> findNotCheckedInBorrowRecords();

    List<BorrowRecord> findDayBorrowRecordAmount(Date startDate, Date endDate);

    BorrowRecord checkOut(Integer bookId, Employee employee);
}
