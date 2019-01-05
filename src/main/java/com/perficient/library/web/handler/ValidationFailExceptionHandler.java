package com.perficient.library.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.exception.ValidationFailedException;
import com.perficient.library.web.domain.ReturnResult;

@ControllerAdvice
public class ValidationFailExceptionHandler {

    @ExceptionHandler(value = ValidationFailedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ReturnResult<List<String>> handle(ValidationFailedException e) {
        return ReturnResultUtils.error(ErrorConvertUtils.convertToList(e.getMessage()));
    }
}
