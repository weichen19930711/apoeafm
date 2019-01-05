package com.perficient.library.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.ObjectError;

public class ErrorConvertUtils {

    public static final String SEPARATOR = "|";

    public static String convertToString(List<ObjectError> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (ObjectError item : errors) {
            buffer.append(item.getDefaultMessage() + SEPARATOR);
        }
        return buffer.toString();
    }

    public static List<String> convertToList(List<ObjectError> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        List<String> resultList = new ArrayList<String>();
        for (ObjectError objectError : errors) {
            resultList.add(objectError.getDefaultMessage());
        }
        return resultList;
    }

    public static List<String> convertToList(String errorString) {
        if (StringUtils.isBlank(errorString)) {
            return null;
        }
        String[] errors = errorString.split("\\" + SEPARATOR);
        return Arrays.asList(errors);
    }

}
