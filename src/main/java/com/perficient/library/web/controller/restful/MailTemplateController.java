package com.perficient.library.web.controller.restful;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.service.MailTemplateService;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/template")
@Api("template")
public class MailTemplateController {

    @Autowired
    private MailTemplateService templateService;

    @GetMapping
    @ApiOperation("(Librarian Only) get all mail templates")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<MailTemplate>> getAllTemplates() {
        return ReturnResultUtils.success(templateService.findAll());
    }

    @PutMapping("/{templateId}")
    @ApiOperation("(Librarian Only) update a mail template by mail template id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<MailTemplate> updateTemplate(@RequestBody MailTemplate template, @PathVariable("templateId") Long templateId) {

        MailTemplate dbTemplate = null;
        if ((dbTemplate = templateService.findOne(templateId)) == null) {
            throw new RestServiceException("the template is not exist");
        }

        String sendFrom = template.getSendFrom();
        if (!EmailValidator.getInstance().isValid(sendFrom)) {
            throw new RestServiceException("send from's value is not a valid email");
        }

        List<String> sendTo = template.getSendTo();
        if (sendTo != null) {
            sendTo.forEach(to -> {
                if (!EmailValidator.getInstance().isValid(to)) {
                    throw new RestServiceException("send to's value contains invalid email");
                }
            });
        }

        List<String> copyTo = template.getCopyTo();
        if (copyTo != null) {
            copyTo.forEach(cc -> {
                if (!EmailValidator.getInstance().isValid(cc)) {
                    throw new RestServiceException("copy to's value contains invalid email");
                }
            });
        }

        // can only update sendFrom, sendTo, copyTo, subject, content
        dbTemplate.setSendFrom(sendFrom);
        dbTemplate.setSendTo(template.getSendTo());
        dbTemplate.setCopyTo(template.getCopyTo());
        dbTemplate.setSubject(template.getSubject());
        dbTemplate.setContent(template.getContent());

        return ReturnResultUtils.success(templateService.save(dbTemplate));
    }

}
