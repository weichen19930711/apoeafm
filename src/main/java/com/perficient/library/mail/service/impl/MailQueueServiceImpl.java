package com.perficient.library.mail.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.repository.MailQueueRepository;
import com.perficient.library.mail.service.MailQueueService;

@Service
public class MailQueueServiceImpl implements MailQueueService {

    @Autowired
    private MailQueueRepository mailQueueRepository;

    @Override
    public MailQueue save(MailQueue entity) {
        return mailQueueRepository.save(entity);
    }

    @Override
    public List<MailQueue> findAll() {
        return mailQueueRepository.findAll();
    }

    @Override
    public MailQueue findOne(Integer id) {
        return mailQueueRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        mailQueueRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return mailQueueRepository.exists(id);
    }

    @Override
    public List<MailQueue> findBySent(boolean sent) {
        return mailQueueRepository.findBySent(sent);
    }

    @Override
    public List<MailQueue> findNotSentQueues() {
        return this.findBySent(false);
    }

}
