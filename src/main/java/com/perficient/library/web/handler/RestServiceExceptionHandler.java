package com.perficient.library.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.web.domain.ReturnResult;

@ControllerAdvice
public class RestServiceExceptionHandler {

    @ExceptionHandler(value = RestServiceException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ReturnResult<List<String>> handle(RestServiceException e) {
        return ReturnResultUtils.error(ErrorConvertUtils.convertToList(e.getMessage()));
    }

}
