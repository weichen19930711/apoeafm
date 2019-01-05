package com.perficient.library.mail.service;

import java.util.List;

import com.perficient.library.core.service.BaseService;
import com.perficient.library.mail.model.MailQueue;

public interface MailQueueService extends BaseService<MailQueue, Integer> {

    List<MailQueue> findBySent(boolean sent);

    List<MailQueue> findNotSentQueues();

}
