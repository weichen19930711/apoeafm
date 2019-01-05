package com.perficient.library.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.common.utils.MailUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.Subscription;
import com.perficient.library.core.repository.SubscriptionRepository;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.core.service.SubscriptionService;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.service.MailQueueService;
import com.perficient.library.mail.service.MailTemplateService;
import com.perficient.library.web.domain.Pagination;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Value("${library.sso.front-end.base-url}")
    private String frontEndURL;

    @Autowired
    private MailTemplateService mailTemplateService;

    @Autowired
    private MailQueueService mailQueueService;

    @Autowired
    private SubscriptionRepository bookSubscriptionRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Subscription save(Subscription entity) {
        return bookSubscriptionRepository.save(entity);
    }

    @Override
    public List<Subscription> findAll() {
        return bookSubscriptionRepository.findAll();
    }

    @Override
    public Subscription findOne(Integer id) {
        return bookSubscriptionRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        bookSubscriptionRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return bookSubscriptionRepository.exists(id);
    }

    @Override
    public List<Subscription> findByProperty(BookProperty property) {
        return bookSubscriptionRepository.findByProperty(property);
    }

    @Override
    public List<Subscription> findByEmployee(Employee employee) {
        return bookSubscriptionRepository.findByEmployee(employee);
    }

    @Override
    public Subscription findByBookAndEmployee(BookProperty property, Employee employee) {
        return bookSubscriptionRepository.findByPropertyAndEmployee(property, employee);
    }

    @Override
    public void remindSubscribers(Book book) {
        BookProperty property = book.getProperty();

        List<Subscription> subscriptions = this.findByProperty(property);

        for (Subscription subscription : subscriptions) {

            // generate librarianNames and librarianMails
            Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(null, null));
            Page<Employee> librarianResult = employeeService.findByRole(Role.LIBRARIAN, pageable);
            List<Employee> librarianList = librarianResult.getContent();

            if (librarianList.size() > 3) {
                librarianList = librarianList.subList(0, 3);
            }

            String librarianMails = MailTemplateUtils.buildLibrarianMails(librarianList);
            String librarianNames = MailTemplateUtils.buildLibrarianNames(librarianList);

            String screenName = subscription.getEmployee().getScreenName();

            // build variable map
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("employeeName", screenName);
            varMap.put("bookDetailURL", frontEndURL + "/book/" + property.getId());
            varMap.put("bookTitle", property.getTitle());
            varMap.put("librarianNames", librarianNames);
            varMap.put("librarianMails", librarianMails);

            MailTemplate template = mailTemplateService.findByName(MailTemplateUtils.SUBSCRIPTION_REMINDER_MAIL_NAME);

            MailQueue queue = MailTemplateUtils.buildQueue(template, varMap);
            // add stakeholder to sendTo list
            queue.getSendTo().add(MailUtils.getMail(screenName));

            mailQueueService.save(queue);

            // after send mail, delete the subscription
            this.delete(subscription.getId());
        }
    }

}
