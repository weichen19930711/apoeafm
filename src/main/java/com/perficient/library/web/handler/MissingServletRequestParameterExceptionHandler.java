package com.perficient.library.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.web.domain.ReturnResult;

/**
 * handler the request parameters missing exception
 * 
 * @author bin.zhou
 *
 */
@ControllerAdvice
public class MissingServletRequestParameterExceptionHandler {

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ReturnResult<List<String>> handle(MissingServletRequestParameterException e) {
        return ReturnResultUtils.error(e.getMessage());
    }

}
