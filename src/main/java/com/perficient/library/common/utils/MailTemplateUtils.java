package com.perficient.library.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import com.perficient.library.core.model.Employee;
import com.perficient.library.mail.model.MailQueue;
import com.perficient.library.mail.model.MailTemplate;

public class MailTemplateUtils {

    public static String VARIABLE_SUFFIX = "${";

    public static String VARIABLE_PREFIX = "}";

    public static final String SYSTEM_MAIL = "No-Reply@perficient.com";

    public static final String ADVANCE_REMINDER_MAIL_NAME = "Advance Reminder Mail";

    public static final String OVERDUE_REMINDER_MAIL_NAME = "Overdue Reminder Mail";

    public static final String SUBSCRIPTION_REMINDER_MAIL_NAME = "Subscription Reminder Mail";

    public static final String TEMPLATE_FORMAT_STYLE = "<style>p{margin: 0;}</style>";

    private static final String ADVANCE_REMINDER_MAIL_TEMPLATE = "templates/mail/advanceReminder.html";

    private static final String OVERDUE_REMINDER_MAIL_TEMPLATE = "templates/mail/overdueReminder.html";

    private static final String SUBSCRIPTION_REMINDER_MAIL_TEMPLATE = "templates/mail/subscriptionReminder.html";

    public static String parse(String template, Map<String, Object> varMap) {
        if (template == null || !template.contains(VARIABLE_SUFFIX) || !template.contains(VARIABLE_PREFIX)) {
            return null;
        }
        varMap = varMap == null ? new HashMap<>() : varMap;
        while (template.contains(VARIABLE_SUFFIX) && template.contains(VARIABLE_PREFIX)) {
            String variable = StringUtils.substringBetween(template, VARIABLE_SUFFIX, VARIABLE_PREFIX);
            Object replacement = varMap.get(variable);
            replacement = replacement == null ? "" : replacement;
            template = template.replace(VARIABLE_SUFFIX + variable + VARIABLE_PREFIX, Objects.toString(replacement));
        }
        return template;
    }

    public static MailTemplate generateAdvanceReminderTemplate() throws IOException {
        List<String> variables = new ArrayList<>();
        variables.add("employeeName");
        variables.add("bookDetailURL");
        variables.add("bookTitle");
        variables.add("dueDate");
        variables.add("myBooksURL");
        variables.add("librarianNames");
        variables.add("librarianMails");

        MailTemplate template = new MailTemplate();
        template.setName(ADVANCE_REMINDER_MAIL_NAME);
        template.setSendFrom(SYSTEM_MAIL);
        template.setSubject("Book will overdue");
        template.setVariables(variables);

        File file = new ClassPathResource(ADVANCE_REMINDER_MAIL_TEMPLATE).getFile();
        template.setContent(FileUtils.readFileToString(file, StandardCharsets.UTF_8));

        return template;
    }

    public static MailTemplate generateOverdueReminderTemplate() throws IOException {
        List<String> variables = new ArrayList<>();
        variables.add("employeeName");
        variables.add("bookDetailURL");
        variables.add("bookTitle");
        variables.add("dueDate");
        variables.add("librarianNames");
        variables.add("librarianMails");

        MailTemplate template = new MailTemplate();
        template.setName(OVERDUE_REMINDER_MAIL_NAME);
        template.setSendFrom(SYSTEM_MAIL);
        template.setSubject("Book is overdue");
        template.setVariables(variables);

        File file = new ClassPathResource(OVERDUE_REMINDER_MAIL_TEMPLATE).getFile();
        template.setContent(FileUtils.readFileToString(file, StandardCharsets.UTF_8));

        return template;
    }

    public static MailTemplate generateSubscriptionReminderTemplate() throws IOException {
        List<String> variables = new ArrayList<>();
        variables.add("employeeName");
        variables.add("bookDetailURL");
        variables.add("bookTitle");
        variables.add("librarianNames");
        variables.add("librarianMails");

        MailTemplate template = new MailTemplate();
        template.setName(SUBSCRIPTION_REMINDER_MAIL_NAME);
        template.setSendFrom(SYSTEM_MAIL);
        template.setSubject("Subscribed book is available");
        template.setVariables(variables);

        File file = new ClassPathResource(SUBSCRIPTION_REMINDER_MAIL_TEMPLATE).getFile();
        template.setContent(FileUtils.readFileToString(file, StandardCharsets.UTF_8));

        return template;
    }

    public static String buildLibrarianNames(List<Employee> librarianList) {
        StringBuilder librarianNames = new StringBuilder();

        int librarianSize = librarianList.size();
        for (int i = 0; i < librarianSize; i++) {
            Employee librarian = librarianList.get(i);
            librarianNames.append(librarian.getScreenName());

            if (i != librarianSize - 1) {
                librarianNames.append(", ");
            }
        }

        return librarianNames.toString();
    }

    public static String buildLibrarianMails(List<Employee> librarianList) {
        StringBuilder librarianMails = new StringBuilder();

        int librarianSize = librarianList.size();
        for (int i = 0; i < librarianSize; i++) {
            Employee librarian = librarianList.get(i);
            librarianMails.append(MailUtils.getMail(librarian.getScreenName()));

            if (i != librarianSize - 1) {
                librarianMails.append(";");
            }
        }

        return librarianMails.toString();
    }

    public static MailQueue buildQueue(MailTemplate template, Map<String, Object> varMap) {
        List<String> sendTo = template.getSendTo() == null ? new ArrayList<>() : template.getSendTo();
        List<String> copyTo = template.getCopyTo() == null ? new ArrayList<>() : template.getCopyTo();

        // build mail queue
        MailQueue queue = new MailQueue();
        queue.setSendFrom(template.getSendFrom());
        queue.setSendTo(sendTo);
        queue.setCopyTo(copyTo);
        queue.setSubject(template.getSubject());
        queue.setContent(MailTemplateUtils.parse(template.getContent(), varMap));

        return queue;
    }

}
