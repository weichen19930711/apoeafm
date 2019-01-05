package com.perficient.library.mail.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.service.MailService;

public class MailServiceImpl implements MailService {

    @Override
    public boolean send(MailQueue queue) {
        // default send should be overrode
        return false;
    }

    protected MailQueue validateQueue(MailQueue queue) {
        if (queue == null) {
            throw new IllegalArgumentException("queue cannot be null");
        }

        List<String> sendTo = queue.getSendTo();
        if (sendTo == null || sendTo.isEmpty()) {
            throw new IllegalArgumentException("sendTo in queue cannot be null or empty");
        }

        String subject = queue.getSubject();
        if (subject == null) {
            throw new IllegalArgumentException("subject in queue cannot be null");
        }

        String content = queue.getContent();
        if (content == null) {
            throw new IllegalArgumentException("content in queue cannot be null");
        }

        List<String> copyTo = queue.getCopyTo() == null ? new ArrayList<>() : queue.getCopyTo();
        queue.setCopyTo(removeBlank(copyTo));

        List<String> blindCopyTo = queue.getBlindCopyTo() == null ? new ArrayList<>() : queue.getBlindCopyTo();
        queue.setBlindCopyTo(removeBlank(blindCopyTo));

        List<String> attachments = queue.getAttachments() == null ? new ArrayList<>() : queue.getAttachments();
        queue.setAttachments(removeBlank(attachments));

        return queue;
    }

    private List<String> removeBlank(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if (StringUtils.isBlank(str)) {
                it.remove();
            }
        }
        return list;
    }

}
