package com.perficient.library.mail.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.repository.MailTemplateRepository;
import com.perficient.library.mail.service.MailTemplateService;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {

    @Autowired
    private MailTemplateRepository templateRepository;

    @Override
    public MailTemplate save(MailTemplate entity) {
        return templateRepository.save(entity);
    }

    @Override
    public List<MailTemplate> findAll() {
        return templateRepository.findAll();
    }

    @Override
    public MailTemplate findOne(Long id) {
        return templateRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        templateRepository.delete(id);
    }

    @Override
    public boolean exists(Long id) {
        return templateRepository.exists(id);
    }

    @Override
    public MailTemplate findByName(String name) {
        return templateRepository.findByName(name);
    }

}
