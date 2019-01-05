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
import com.perficient.library.core.model.DamagedRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.DamagedRecordService;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailContentService;
import com.perficient.library.mail.service.MailQueueService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only Librarian can do operations for damaged records.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/damaged_record")
@Api("damaged_record")
public class DamagedRecordController {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private DamagedRecordService damagedRecordService;

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
    @ApiOperation("(Librarian Only) set a book to damaged status and create a damaged record by book id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<DamagedRecord> damageBook(@PathVariable("bookId") Integer bookId) {

        Book dbBook = null;
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book not exist");
        }

        BookStatus status = dbBook.getStatus();
        if (BookStatus.DAMAGED.equals(status)) {
            throw new RestServiceException("the book is already damaged");
        }

        if (BookStatus.LOST.equals(status)) {
            throw new RestServiceException("the book is already lost");
        }

        DamagedRecord record = new DamagedRecord();
        record.setId(null);
        dbBook.setStatus(BookStatus.DAMAGED);
        record.setBook(dbBook);
        record.setIsPaid(false);
        record.setCreateDate(new Date());

        // if book is not checked out, there is no borrower.
        if (!BookStatus.CHECKED_OUT.equals(status)) {
            record.setEmployee(null);
            record = damagedRecordService.save(record);
            return ReturnResultUtils.success("set to damaged succeeded", record);
        }
        BorrowRecord dbBorrowRecord = borrowRecordService.findNotCheckedInBorrowRecordsByBook(dbBook);
        // Set book checkInDate when damaged book
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
        record = damagedRecordService.save(record);

        return ReturnResultUtils.success("set to damaged succeeded", record);
    }

    @RequestMapping(value = "/pay/{id}", method = { RequestMethod.PUT, RequestMethod.POST })
    @ApiOperation("(Librarian Only) set paid status to a damaged record by damaged record id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> PayDamagedRecord(@PathVariable("id") Integer id,
        @RequestParam(value = "paidTime") @DateTimeFormat(pattern = DATE_PATTERN) Date paidTime,
        @RequestParam(value = "comment", required = false) String comment) {

        if (paidTime == null) {
            throw new RestServiceException("the paid time not null");
        }
        DamagedRecord dbDamagedRecord = null;
        if ((dbDamagedRecord = damagedRecordService.findOne(id)) == null) {
            throw new RestServiceException("damaged record not exist");
        }
        dbDamagedRecord.setIsPaid(true);
        dbDamagedRecord.setPaidTime(paidTime);
        dbDamagedRecord.setDescription(comment);
        dbDamagedRecord = damagedRecordService.save(dbDamagedRecord);
        return ReturnResultUtils.success("set to paid succeeded");
    }

    @GetMapping("/search")
    @ApiOperation("(Librarian Only) get damaged records by search value")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<DamagedRecord>> getDamagedRecordsBySearchValue(
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
        Specification<DamagedRecord> spec = PageUtils.buildSpecificationForDamagedRecord(searchValue);
        Page<DamagedRecord> records = damagedRecordService.getDamagedRecords(spec, pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/export")
    @ApiOperation("(Librarian Only) export damaged records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public void exportDamagedRecord(@RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Specification<DamagedRecord> spec = PageUtils.buildSpecificationForDamagedRecord(searchValue);
        Page<DamagedRecord> records = damagedRecordService.getDamagedRecords(spec, null);
        Integer size = (int) records.getTotalElements();
        if (size == 0) {
            damagedRecordService.exportDamagedRecords(records.getContent());
        } else {
            ReturnResult<List<DamagedRecord>> a = getDamagedRecordsBySearchValue(1, size, searchValue, key, order);
            damagedRecordService.exportDamagedRecords(a.getData());
        }
    }

    @PostMapping("/mail")
    @ApiOperation("(Librarian Only) trigger selected damaged record list by mail to current operating librarian")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> mailToLibrarian(@RequestParam(value = "recordIds") Integer[] recordIds) {
        List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
        // check if there is a record in database.
        for (Integer recordId : recordIds) {
            DamagedRecord record = damagedRecordService.findOne(recordId);

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
        queue.setSubject("Your Selected Damaged Record List");
        queue.setContent(content);

        mailQueueService.save(queue);

        return ReturnResultUtils.success("send mail succeeded");
    }
}
