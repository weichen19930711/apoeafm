package com.perficient.library.web.controller.restful;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.DateFormatUtil;
import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.exception.UnauthorizedException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Configuration;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.core.service.SubscriptionService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * only Librarian can do operations for borrow records.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/borrow_record")
@Api("borrow_record")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private BookService bookService;

    @Autowired
    private OverdueRecordService overdueRecordService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/checkout_book/{bookId}")
    @ApiOperation("checkout a book by book id")
    public ReturnResult<BorrowRecord> checkOutBook(@PathVariable("bookId") Integer bookId) {
        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }

        // If the employee still has overdue borrow records, he/she cannot checkout book until his/her overdue books were all checked in.
        List<OverdueRecord> overdueRecords = overdueRecordService.findNotCheckedInOverdueRecordsByEmployee(employee);
        if (!overdueRecords.isEmpty()) {
            throw new RestServiceException("there are overdue books that has not been returned");
        }

        // Make sure only one person can borrow book in many requests
        synchronized (this) {
            return ReturnResultUtils.success("check out succeeded", borrowRecordService.checkOut(bookId, employee));
        }
    }

    @Transactional
    @PutMapping("/renew_book/{recordId}")
    @ApiOperation("renew a book by borrow record id")
    public ReturnResult<BorrowRecord> renewBook(@PathVariable("recordId") Integer recordId) {

        BorrowRecord dbRecord = null;

        if (recordId == null || (dbRecord = borrowRecordService.findOne(recordId)) == null) {
            throw new RestServiceException("the borrow record is not exist");
        }

        Employee dbEmployee = dbRecord.getEmployee();

        // Only checked out book can renew.
        if (dbRecord.getBook().getStatus() != BookStatus.CHECKED_OUT) {
            throw new RestServiceException("the book is not checked out to renew");
        }

        // If the employee still has overdue borrow records, he/she cannot checkout book until his/her overdue books were all checked in.
        List<OverdueRecord> overdueRecords = overdueRecordService.findNotCheckedInOverdueRecordsByEmployee(dbEmployee);
        if (!overdueRecords.isEmpty()) {
            throw new RestServiceException("there are overdue books that has not been returned");
        }

        Date dueDate = dbRecord.getDueDate();
        Date today = new Date();
        if (DateUtils.truncatedCompareTo(dueDate, today, Calendar.DATE) == -1) {
            // already overdue
            throw new RestServiceException("the book is already overdue and cannot be renewed");
        }

        Configuration config = configurationService.get();
        Integer renewTime = 1 + dbRecord.getRenewTime();
        Integer renewTimeOnConfig = config.getMaxRenewTimes();
        // If renew time greater than renew time on configuration.
        if (renewTime > renewTimeOnConfig) {
            throw new RestServiceException("you can only renew a book for " + renewTimeOnConfig + " times at most");
        }

        Integer renewDay = DateFormatUtil.daysBetween(new Date(), dbRecord.getDueDate());
        Integer renewDaysBefore = config.getRenewDaysBefore();
        if (renewDay > renewDaysBefore) {
            throw new RestServiceException("you can only renew a book  " + renewDaysBefore + " days before due date");
        }

        dbRecord.setId(recordId);
        dbRecord.setRenewTime(renewTime);
        dbRecord.setDueDate(DateUtils.addDays(dbRecord.getCheckoutDate(),
            config.getAvailableBorrowingDays() + config.getRenewAddedDays() * (renewTime)));
        dbRecord = borrowRecordService.save(dbRecord);
        return ReturnResultUtils.success("renew succeeded", dbRecord);
    }

    @Transactional
    @RequestMapping(value = "/checkin_book/{bookId}", method = { RequestMethod.PUT, RequestMethod.POST })
    @ApiOperation("(Librarian Only) checkin a book by book id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<BorrowRecord> checkInBook(@PathVariable("bookId") Integer bookId) {

        Book dbBook = null;
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }

        if (dbBook.getStatus() != BookStatus.CHECKED_OUT) {
            throw new RestServiceException("the book is not checked out");
        }

        dbBook.setStatus(BookStatus.AVAILABLE);
        final Book savedBook = bookService.save(dbBook);

        BorrowRecord dbBorrowRecord = borrowRecordService.findNotCheckedInBorrowRecordsByBook(savedBook);
        dbBorrowRecord.setCheckinDate(new Date());
        dbBorrowRecord = borrowRecordService.save(dbBorrowRecord);

        // If exist associated overdue record, the overdue record's returned value should be changed to true
        OverdueRecord overdueRecord = overdueRecordService.findByBorrowRecord(dbBorrowRecord);
        if (overdueRecord != null) {
            overdueRecord.setReturned(true);
            overdueRecordService.save(overdueRecord);
        }

        // Send mails to notify the subscribers and delete all associated subscriptions
        new Thread(new Runnable() {

            @Override
            public void run() {
                subscriptionService.remindSubscribers(savedBook);
            }
        }).start();

        return ReturnResultUtils.success("check in succeeded", dbBorrowRecord);
    }

    @Transactional
    @DeleteMapping("/{recordId}")
    @ApiOperation("(Librarian Only) delete a borrow record by borrow record id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> deleteBorrowRecord(@PathVariable("recordId") Integer recordId) {

        BorrowRecord dbBorrowRecord = null;

        // Borrow Record's id must exist
        if ((dbBorrowRecord = borrowRecordService.findOne(recordId)) == null) {
            throw new RestServiceException("the borrow record is not exist");
        }

        // If the associated book has not been checked in, the borrow record cannot be deleted
        if (dbBorrowRecord.getCheckinDate() == null) {
            throw new RestServiceException("the associated book has not been checked in");
        }

        // If the associated overdue records are not empty, the borrow record cannot be deleted
        OverdueRecord overdueRecord = overdueRecordService.findByBorrowRecord(dbBorrowRecord);
        if (overdueRecord != null) {
            throw new RestServiceException("there exists associated overdue record");
        }

        borrowRecordService.delete(recordId);
        return ReturnResultUtils.success("delete succeeded", null);
    }

    @GetMapping
    @ApiOperation("(Librarian Only) get all borrow records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<BorrowRecord>> getAllBorrowRecords(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<BorrowRecord> records = borrowRecordService.findAll(pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/no_return")
    @ApiOperation("(Librarian Only) get not checked in borrow records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<BorrowRecord>> getNotCheckedInBorrowRecords(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<BorrowRecord> records = borrowRecordService.findNotCheckedInBorrowRecords(pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/checkedout")
    @ApiOperation("(Librarian Only) get borrow records that book status is checked out")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<BorrowRecord>> getCheckedOutBorrowRecords(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Pagination pagnation = Pagination.generatePagnation(page, size);
        PageRequest pageable = null;
        if ("asc".equalsIgnoreCase(order)) {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.ASC, key);
        } else {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.DESC, key);
        }
        Specification<BorrowRecord> spec = PageUtils.buildSpecificationForBorrowRecord(searchValue);
        Page<BorrowRecord> records = borrowRecordService.findCheckedOutBorrowRecords(spec, pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/export")
    @ApiOperation("(Librarian Only) export borrow records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public void exportCheckedOutBorrowRecord(@RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Specification<BorrowRecord> spec = PageUtils.buildSpecificationForBorrowRecord(searchValue);
        Page<BorrowRecord> records = borrowRecordService.findCheckedOutBorrowRecords(spec, null);
        Integer size = (int) records.getTotalElements();
        if (size == 0) {
            borrowRecordService.exportBorrowRecord(records.getContent());
        } else {
            ReturnResult<List<BorrowRecord>> result = getCheckedOutBorrowRecords(1, size, searchValue, key, order);
            borrowRecordService.exportBorrowRecord(result.getData());
        }
    }

}
