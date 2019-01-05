package com.perficient.library.web.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.exception.EnumValueMismatchException;
import com.perficient.library.web.domain.ReturnResult;

@ControllerAdvice
public class JsonMappingExceptionHandler {

    @ExceptionHandler(value = JsonMappingException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ReturnResult<List<String>> handle(JsonMappingException e) {
        //        StringBuffer buffer = new StringBuffer();
        //        Arrays.asList(StringUtils.substringsBetween(e.getPathReference(), "[\"", "\"]")).forEach(name -> {
        //            buffer.append(name).append(".");
        //        });
        //        String field = buffer.substring(0, buffer.length() - 1);
        if (EnumValueMismatchException.class.equals(e.getCause().getClass())) {
            EnumValueMismatchException exp = (EnumValueMismatchException) e.getCause();
            List<String> fieldValues = new ArrayList<String>();
            Lists.newArrayList(exp.getEnumClass().getEnumConstants()).forEach(item -> {
                fieldValues.add(item.toString());
            });
            return ReturnResultUtils.error(String.format("%s -> type mismatch, valid types: %s", exp.getField(),
                StringUtils.join(fieldValues, ", ")));
        }
        return ReturnResultUtils.error("type mismatch");
    }

}
