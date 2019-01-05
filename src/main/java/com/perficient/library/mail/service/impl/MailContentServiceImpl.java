package com.perficient.library.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.service.MailContentService;
import com.perficient.library.mail.service.MailTemplateService;

@Service
public class MailContentServiceImpl implements MailContentService {

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public String readFromHTML(String templatePath, String varName, Object varValue) {
        Context context = new Context();
        context.setVariable(varName, varValue);
        return templateEngine.process(templatePath, context);
    }

    @Override
    public String readFromHTML(String templatePath, Map<String, Object> varMap) {
        Context context = new Context();
        context.setVariables(varMap);
        return templateEngine.process(templatePath, context);
    }

    @Autowired
    private MailTemplateService templateService;

    public String readFromDB(String templateName, String varName, Object varValue) {
        MailTemplate template = templateService.findByName(templateName);
        if (template == null) {
            throw new IllegalArgumentException("template is not exist with name: " + templateName);
        }
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(varName, varValue);
        return MailTemplateUtils.parse(template.getContent(), varMap);
    }

    public String readFromDB(String templateName, Map<String, Object> varMap) {
        MailTemplate template = templateService.findByName(templateName);
        if (template == null) {
            throw new IllegalArgumentException("template is not exist with name: " + templateName);
        }
        return MailTemplateUtils.parse(template.getContent(), varMap);
    }

}
