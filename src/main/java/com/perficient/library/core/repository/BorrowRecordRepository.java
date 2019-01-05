package com.perficient.library.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;

@Repository
public interface BorrowRecordRepository
    extends JpaRepository<BorrowRecord, Integer>, JpaSpecificationExecutor<BorrowRecord> {

    Page<BorrowRecord> findByEmployeeOrderByCreateDateDesc(Employee employee, Pageable pageable);

    Page<BorrowRecord> findByCheckinDate(Date checkinDate, Pageable pageable);

    List<BorrowRecord> findByCheckinDateAndEmployeeOrderByCreateDateDesc(Date checkinDate, Employee employee);

    BorrowRecord findByCheckinDateAndBook(Date checkinDate, Book book);

    List<BorrowRecord> findByBook(Book book);

    List<BorrowRecord> findByBookProperty(BookProperty property);

    List<BookRepository> findByCheckoutDateBetween(Date start, Date end);

    List<BorrowRecord> findByCheckinDate(Date checkinDate);

    @Query(value = "SELECT * FROM borrow_record br WHERE date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d')", nativeQuery = true)
    List<BorrowRecord> findDayBorrowRecordAmount(Date startDate, Date endDate);

}
