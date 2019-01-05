package com.perficient.library.core.service.impl;

import com.perficient.library.common.utils.ServletUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.repository.BookRepository;
import com.perficient.library.core.repository.BorrowRecordRepository;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.export.ExportXLS;
import org.apache.commons.lang3.time.DateUtils;
import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BorrowRecordServiceImpl extends ExportXLS implements BorrowRecordService {

    public static final String BORROWRECORDLIST_TEMPLATE_PATH = "/xls-templates/borrowRecordListTemplate.xls";

    public static final String BORROWRECORDLIST_REPORT_BASE_NAME = "BorrowRecordReport";

    public static final String BORROWRECORDLIST_CONTEXT_KEY = "borrowRecords";

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public BorrowRecord save(BorrowRecord entity) {
        return borrowRecordRepository.save(entity);
    }

    @Override
    public List<BorrowRecord> findAll() {
        return borrowRecordRepository.findAll();
    }

    @Override
    public BorrowRecord findOne(Integer id) {
        return borrowRecordRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        borrowRecordRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return borrowRecordRepository.exists(id);
    }

    @Override
    public Page<BorrowRecord> findByEmployee(Employee employee, Pageable pageable) {
        return borrowRecordRepository.findByEmployeeOrderByCreateDateDesc(employee, pageable);
    }

    @Override
    public List<BorrowRecord> findByBook(Book book) {
        return borrowRecordRepository.findByBook(book);
    }

    @Override
    public List<BorrowRecord> findByBookProperty(BookProperty property) {
        return borrowRecordRepository.findByBookProperty(property);
    }

    @Override
    public Page<BorrowRecord> findAll(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }

    @Override
    public Page<BorrowRecord> findNotCheckedInBorrowRecords(Pageable pageable) {
        // checkin date is null means has not been checked in
        return borrowRecordRepository.findByCheckinDate(null, pageable);
    }

    @Override
    public List<BorrowRecord> findNotCheckedInBorrowRecordsByEmployee(Employee employee) {
        return borrowRecordRepository.findByCheckinDateAndEmployeeOrderByCreateDateDesc(null, employee);
    }

    @Override
    public BorrowRecord findNotCheckedInBorrowRecordsByBook(Book book) {
        return borrowRecordRepository.findByCheckinDateAndBook(null, book);
    }

    @Override
    public Page<BorrowRecord> findCheckedOutBorrowRecords(Specification<BorrowRecord> spec, Pageable pageable) {
        return borrowRecordRepository.findAll(spec, pageable);
    }

    @Override
    public void exportBorrowRecord(Object records) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(BORROWRECORDLIST_CONTEXT_KEY, records);
        try {
            export(response, BORROWRECORDLIST_REPORT_BASE_NAME, BORROWRECORDLIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BorrowRecord> findNotCheckedInBorrowRecords() {
        return borrowRecordRepository.findByCheckinDate(null);
    }

    @Override
    public List<BorrowRecord> findDayBorrowRecordAmount(Date startDate, Date endDate) {
        return borrowRecordRepository.findDayBorrowRecordAmount(startDate, endDate);
    }

    @Override
    @Transactional
    public BorrowRecord checkOut(Integer bookId, Employee employee) {

        Book dbBook = null;

        // Book's id must exist
        if (bookId == null || (dbBook = bookRepository.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }
        // Only available book can be checked out
        if (dbBook.getStatus() != BookStatus.AVAILABLE) {
            throw new RestServiceException("the book is not available to check out");
        }

        // One employee cannot only borrow N books(N will be set in configuration)
        List<BorrowRecord> borrowingRecords = findNotCheckedInBorrowRecordsByEmployee(employee);
        Integer maxBorrowBooks = configurationService.get().getMaxBorrowingAmount();
        if (borrowingRecords.size() >= maxBorrowBooks) {
            throw new RestServiceException("you can only borrow " + maxBorrowBooks + " books at most");
        }

        dbBook.setStatus(BookStatus.CHECKED_OUT);
        dbBook = bookRepository.save(dbBook);

        BorrowRecord borrowRecord = new BorrowRecord();

        borrowRecord.setId(null);
        borrowRecord.setBook(dbBook);
        borrowRecord.setEmployee(employee);
        borrowRecord.setCheckoutDate(new Date());
        borrowRecord.setCheckinDate(null);
        borrowRecord.setDueDate(
                DateUtils.addDays(borrowRecord.getCheckoutDate(), configurationService.get().getAvailableBorrowingDays()));
        borrowRecord.setRenewTime(0);
        borrowRecord = save(borrowRecord);
        return borrowRecord;
    }
}
