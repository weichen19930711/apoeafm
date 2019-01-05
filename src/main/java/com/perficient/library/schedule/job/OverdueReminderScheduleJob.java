package com.perficient.library.schedule.job;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.common.utils.MailUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.service.MailQueueService;
import com.perficient.library.mail.service.MailTemplateService;
import com.perficient.library.schedule.DynamicScheduleConfigurer;
import com.perficient.library.web.domain.Pagination;

@Component
public class OverdueReminderScheduleJob extends DynamicScheduleConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(OverdueReminderScheduleJob.class);

    private static final String RESULT_LOG_TEMPLATE = "[%s] %s records added to mail queues";

    @Value("${library.schedule.overdue-reminder.expression}")
    private String expression;

    @Value("${library.schedule.overdue-reminder.active}")
    private boolean active;

    @Value("${library.sso.front-end.base-url}")
    private String frontEndURL;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private OverdueRecordService overdueRecordService;

    @Autowired
    private MailQueueService mailQueueService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MailTemplateService mailTemplateService;

    @PostConstruct
    private void init() {
        this.setActive(active);
        this.setExpression(expression);
    }

    @Override
    @Transactional
    protected void execute() {

        List<BorrowRecord> notCheckedInList = borrowRecordService.findNotCheckedInBorrowRecords();
        int insertSize = 0;
        for (BorrowRecord borrowRecord : notCheckedInList) {
            Date dueDate = borrowRecord.getDueDate();
            Date today = new Date();
            if (DateUtils.truncatedCompareTo(dueDate, today, Calendar.DATE) >= 0
                || borrowRecord.isCreatedOverdueRecord()) {
                // not overdue or overdue record already created
                continue;
            }

            // create overdue record
            OverdueRecord overdueRecord = new OverdueRecord();
            overdueRecord.setBorrowRecord(borrowRecord);
            overdueRecordService.save(overdueRecord);

            // set createdOverdueRecord field in BorrowRecord to true
            borrowRecord.setCreatedOverdueRecord(true);
            borrowRecordService.save(borrowRecord);

            BookProperty property = borrowRecord.getBook().getProperty();
            String screenName = borrowRecord.getEmployee().getScreenName();
            String bookDetailURL = frontEndURL + "/book/" + property.getId();
            String bookTitle = property.getTitle();

            // generate librarianNames and librarianMails
            Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(null, null));
            Page<Employee> librarianResult = employeeService.findByRole(Role.LIBRARIAN, pageable);
            List<Employee> librarianList = librarianResult.getContent();

            if (librarianList.size() > 3) {
                librarianList = librarianList.subList(0, 3);
            }

            String librarianMails = MailTemplateUtils.buildLibrarianMails(librarianList);
            String librarianNames = MailTemplateUtils.buildLibrarianNames(librarianList);

            // build variable map
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("employeeName", screenName);
            varMap.put("bookDetailURL", bookDetailURL);
            varMap.put("bookTitle", bookTitle);
            varMap.put("dueDate", DateFormatUtils.ISO_DATE_FORMAT.format(dueDate));
            varMap.put("librarianNames", librarianNames);
            varMap.put("librarianMails", librarianMails);

            MailTemplate template = mailTemplateService.findByName(MailTemplateUtils.OVERDUE_REMINDER_MAIL_NAME);

            MailQueue queue = MailTemplateUtils.buildQueue(template, varMap);
            // add stakeholder to sendTo list
            queue.getSendTo().add(MailUtils.getMail(screenName));

            mailQueueService.save(queue);
            insertSize++;
        }

        if (insertSize > 0) {
            String resultLog = String.format(RESULT_LOG_TEMPLATE, getJobName(), insertSize);
            logger.info(resultLog);
        }
    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

}
