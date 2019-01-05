package com.perficient.library.mail.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.perficient.library.common.annotation.QA;
import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.common.utils.MailUtils;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailContentService;

@QA
@Service
public class QAMailServiceImpl extends MailServiceImpl {

    private static final String QA_MOCK_MAIL_SUBJECT = "Library QA Mock Mail";

    private static final Logger logger = LoggerFactory.getLogger(QAMailServiceImpl.class);

    private static final String MOCK_MAIL_TEMPLATE = "mail/mockMail";

    @Value("${library.mock.mail.recipients}")
    private String[] recipients;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private MailContentService mailContentService;

    @Override
    public boolean send(MailQueue queue) {

        this.validateQueue(queue);

        MimeMessage message = mailSender.createMimeMessage();

        List<String> recipientList = null;
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            recipientList = addSuffix(recipients);

            helper.setTo(recipientList.toArray(new String[0]));
            helper.setSubject(QA_MOCK_MAIL_SUBJECT);
            helper.setText(buildMockContent(queue), true);
            helper.setFrom(mailProperties.getUsername());

            mailSender.send(helper.getMimeMessage());
            logger.info("QA mock mail send to " + recipientList + " is successful");
            return true;
        } catch (Exception e) {
            logger.error("QA mock mail send to " + recipientList + " is failed, message: " + e.getMessage());
            return false;
        }
    }

    private String buildMockContent(MailQueue queue) {

        Map<String, Object> varMap = new HashMap<>();

        varMap.put("sendFrom", queue.getSendFrom());
        varMap.put("sendTo", queue.getSendTo());
        varMap.put("copyTo", queue.getCopyTo());
        varMap.put("subject", queue.getSubject());
        varMap.put("content", MailTemplateUtils.TEMPLATE_FORMAT_STYLE + queue.getContent());

        return mailContentService.readFromHTML(MOCK_MAIL_TEMPLATE, varMap);
    }

    private List<String> addSuffix(String[] recipients) {
        List<String> newList = new ArrayList<>();
        for (String recipient : recipients) {
            newList.add(MailUtils.getMail(recipient));
        }
        return newList;
    }

}
