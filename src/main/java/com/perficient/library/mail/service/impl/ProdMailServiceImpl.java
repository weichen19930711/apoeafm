package com.perficient.library.mail.service.impl;

import java.io.File;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.perficient.library.common.annotation.Prod;
import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.mail.model.MailQueue;

@Prod
@Service
public class ProdMailServiceImpl extends MailServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ProdMailServiceImpl.class);

    private static final long MAX_ATTACHMENT_SIZE = 20971520L; // max size: 20MB

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean send(MailQueue queue) {

        this.validateQueue(queue);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(queue.getSendTo().toArray(new String[0]));
            helper.setCc(queue.getCopyTo().toArray(new String[0]));
            helper.setBcc(queue.getBlindCopyTo().toArray(new String[0]));
            helper.setSubject(queue.getSubject());
            helper.setText(MailTemplateUtils.TEMPLATE_FORMAT_STYLE + queue.getContent(), true);
            helper.setFrom(queue.getSendFrom());

            List<String> attachments = queue.getAttachments();
            for (String attachment : attachments) {
                File file = null;
                if ((file = new File(attachment)).exists() && file.length() <= MAX_ATTACHMENT_SIZE) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(helper.getMimeMessage());
            logger.info("Mail send to " + queue.getSendTo() + " is successful");
            return true;
        } catch (Exception e) {
            logger.error("Mail send to " + queue.getSendTo() + " is failed, message: " + e.getMessage());
            return false;
        }
    }
}
