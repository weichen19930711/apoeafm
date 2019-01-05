package com.perficient.library.mail.service;

import com.perficient.library.core.service.BaseService;
import com.perficient.library.mail.model.MailTemplate;

public interface MailTemplateService extends BaseService<MailTemplate, Long> {

    MailTemplate findByName(String name);

}
