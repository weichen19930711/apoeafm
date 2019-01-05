package com.perficient.library.common.utils;

import org.apache.commons.validator.routines.EmailValidator;

import com.perficient.library.core.model.Employee;

public class MailUtils {

    public static final String SUFFIX = "@perficient.com";

    private MailUtils() {
    }

    public static boolean isValid(String mail) {
        return EmailValidator.getInstance().isValid(mail) && mail.endsWith(SUFFIX);
    }

    public static String getMail(String screenName) {
        return screenName + MailUtils.SUFFIX;
    }

    public static String getMail(Employee employee) {
        return employee == null ? null : getMail(employee.getScreenName());
    }

}
