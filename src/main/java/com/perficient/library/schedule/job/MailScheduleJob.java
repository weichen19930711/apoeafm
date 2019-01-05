package com.perficient.library.schedule.job;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailQueueService;
import com.perficient.library.mail.service.MailService;
import com.perficient.library.schedule.DynamicScheduleConfigurer;

@Component
public class MailScheduleJob extends DynamicScheduleConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(OverdueReminderScheduleJob.class);

    private static final String START_LOG_TEMPLATE = "[%s] There are %s mail(s) need to be sent";

    private static final String RESULT_LOG_TEMPLATE = "[%s] Sent mails complete, total: %s, success: %s, failure: %s";

    @Value("${library.schedule.mail.expression}")
    private String expression;

    @Value("${library.schedule.mail.active}")
    private boolean active;

    @Autowired
    private MailQueueService mailQueueService;

    @Autowired
    private MailService mailService;

    @PostConstruct
    private void init() {
        this.setActive(active);
        this.setExpression(expression);
    }

    @Override
    protected void execute() {

        List<MailQueue> queues = mailQueueService.findNotSentQueues();
        int queueSize = queues.size();

        if (queueSize > 0) {
            String startLog = String.format(START_LOG_TEMPLATE, getJobName(), queueSize);
            logger.info(startLog);
        }

        int insertSize = 0;
        for (MailQueue queue : queues) {
            boolean success = mailService.send(queue);
            if (success) {
                queue.setSent(true);
                mailQueueService.save(queue);
                insertSize++;
            }
        }

        if (queueSize > 0) {
            String resultLog = String.format(RESULT_LOG_TEMPLATE, getJobName(), queueSize, insertSize,
                (queueSize - insertSize));
            logger.info(resultLog);

        }

    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

}
