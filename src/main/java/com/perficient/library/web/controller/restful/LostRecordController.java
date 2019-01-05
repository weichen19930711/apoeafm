package com.perficient.library.web.controller.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.MailUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.LostRecord;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.LostRecordService;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailContentService;
import com.perficient.library.mail.service.MailQueueService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only Librarian can do operations for lost records.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/lost_record")
@Api("lost_record")
public class LostRecordController {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private LostRecordService lostRecordService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private OverdueRecordService overdueRecordService;

    @Autowired
    private MailContentService mailContentService;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private MailQueueService mailQueueService;

    @PostMapping("/{bookId}")
    @ApiOperation("(Librarian Only) set a book to lost status by book id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<LostRecord> lostBook(@PathVariable("bookId") Integer bookId) {

        Book dbBook = null;
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }

        BookStatus status = dbBook.getStatus();
        if (BookStatus.LOST.equals(status)) {
            throw new RestServiceException("the book is already lost");
        }

        LostRecord record = new LostRecord();
        record.setId(null);
        dbBook.setStatus(BookStatus.LOST);
        record.setBook(dbBook);
        record.setIsPaid(false);
        record.setCreateDate(new Date());

        // if book is not checked out, there is no borrower.
        if (!BookStatus.CHECKED_OUT.equals(status)) {
            record.setEmployee(null);
            record = lostRecordService.save(record);
            return ReturnResultUtils.success("set to lost succeeded", record);
        }
        BorrowRecord dbBorrowRecord = borrowRecordService.findNotCheckedInBorrowRecordsByBook(dbBook);
        // Set book checkInDate when lost book
        dbBorrowRecord.setCheckinDate(new Date());
        dbBorrowRecord = borrowRecordService.save(dbBorrowRecord);

        // If exist associated overdue record, the overdue record's returned
        // value should be changed to true
        OverdueRecord overdueRecord = overdueRecordService.findByBorrowRecord(dbBorrowRecord);
        if (overdueRecord != null) {
            overdueRecord.setReturned(true);
            overdueRecordService.save(overdueRecord);
        }

        record.setEmployee(dbBorrowRecord.getEmployee());
        record = lostRecordService.save(record);

        return ReturnResultUtils.success("set to lost succeeded", record);
    }

    @RequestMapping(value = "/pay/{id}", method = { RequestMethod.PUT, RequestMethod.POST })
    @ApiOperation("(Librarian Only) set paid status to a lost record by lost record id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> PayLostRecord(@PathVariable("id") Integer id,
        @RequestParam(value = "paidTime") @DateTimeFormat(pattern = DATE_PATTERN) Date paidTime,
        @RequestParam(value = "comment", required = false) String comment) {

        if (paidTime == null) {
            throw new RestServiceException("the paid time not null");
        }
        LostRecord dbLostRecord = null;
        if ((dbLostRecord = lostRecordService.findOne(id)) == null) {
            throw new RestServiceException("the lost record is not exist");
        }
        dbLostRecord.setIsPaid(true);
        dbLostRecord.setPaidTime(paidTime);
        dbLostRecord.setDescription(comment);
        dbLostRecord = lostRecordService.save(dbLostRecord);
        return ReturnResultUtils.success("set to paid succeeded");
    }

    @GetMapping("/search")
    @ApiOperation("(Librarian Only) get lost records by searchValue")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<LostRecord>> getLostRecordsBySearchValue(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Pagination pagnation = Pagination.generatePagnation(page, size);
        PageRequest pageable = null;
        if (Direction.ASC.toString().equalsIgnoreCase(order)) {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.ASC, key);
        } else {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.DESC, key);
        }
        Specification<LostRecord> spec = PageUtils.buildSpecificationForLostRecord(searchValue);
        Page<LostRecord> records = lostRecordService.getLostRecords(spec, pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/export")
    @ApiOperation("(Librarian Only) export lost records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public void exportLostRecord(@RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Specification<LostRecord> spec = PageUtils.buildSpecificationForLostRecord(searchValue);
        Page<LostRecord> records = lostRecordService.getLostRecords(spec, null);
        Integer size = (int) records.getTotalElements();
        if (size == 0) {
            lostRecordService.exportLostRecords(records.getContent());
        } else {
            ReturnResult<List<LostRecord>> a = getLostRecordsBySearchValue(1, size, searchValue, key, order);
            lostRecordService.exportLostRecords(a.getData());
        }
    }

    @PostMapping("/mail")
    @ApiOperation("(Librarian Only) trigger selected lost record list by mail to librarian")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> mailToLibrarian(@RequestParam(value = "recordIds") Integer[] recordIds) {
        List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
        // check if there is a record in database.
        for (Integer recordId : recordIds) {
            LostRecord record = lostRecordService.findOne(recordId);

            if (record == null) {
                // ignore nonexistent record id
                continue;
            }
            Map<String, String> tr = new HashMap<String, String>();
            Employee employee = record.getEmployee();
            String emid = (employee == null) ? "" : employee.getEmid();
            String englishName = (employee == null) ? "" : employee.getScreenName();
            String isPaid = (record.getIsPaid()) ? "Y" : "N";
            String paidTime = (record.getIsPaid()) ? DateFormatUtils.format(record.getPaidTime(), DATE_PATTERN) : "";
            Book book = record.getBook();
            BookProperty property = book.getProperty();
            String Deduction = property.getPrice();
            String tagNumber = book.getTagNumber();
            String title = property.getTitle();
            String purchaser = book.getPurchaser().getValue();
            tr.put("AssignmentNo", emid);
            tr.put("EnglishName", englishName);
            tr.put("Deduction", Deduction);
            tr.put("TagNumber", tagNumber);
            tr.put("Title", title);
            tr.put("Purchaser", purchaser);
            tr.put("IsPaid", isPaid);
            tr.put("PaidTime", paidTime);
            tableData.add(tr);
        }

        String content = mailContentService.readFromHTML("/mail/sendRecords", "tableData", tableData);
        Employee currentUser = EmployeeContextUtils.getEmpInSession();

        MailQueue queue = new MailQueue();
        queue.setSendFrom(mailProperties.getUsername());
        queue.setSendTo(Lists.newArrayList(MailUtils.getMail(currentUser)));
        queue.setSubject("Your Selected Lost Record List");
        queue.setContent(content);

        mailQueueService.save(queue);

        return ReturnResultUtils.success("send mail succeeded");
    }
}
