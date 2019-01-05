package com.perficient.library.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.perficient.library.common.annotation.Dev;
import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.common.utils.MailUtils;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailContentService;

@Dev
@Service
public class DevMailServiceImpl extends MailServiceImpl {

    private static final String DEV_MOCK_MAIL_SUBJECT = "Library Dev Mock Mail";

    private static final Logger logger = LoggerFactory.getLogger(DevMailServiceImpl.class);

    private static final String MOCK_MAIL_TEMPLATE = "mail/mockMail";

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

        String recipient = null;

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (SystemUtils.IS_OS_MAC) {
                recipient = "2066473614@qq.com";
            } else {
                recipient = System.getenv("USERNAME") + MailUtils.SUFFIX;
            }

            helper.setTo(recipient);
            helper.setSubject(DEV_MOCK_MAIL_SUBJECT);
            helper.setText(buildMockContent(queue), true);
            helper.setFrom(mailProperties.getUsername());

            mailSender.send(helper.getMimeMessage());
            logger.info("Dev mock mail send to " + recipient + " is successful");
            return true;
        } catch (Exception e) {
            logger.error("Dev mock mail send to " + recipient + " is failed, message: " + e.getMessage());
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

}
