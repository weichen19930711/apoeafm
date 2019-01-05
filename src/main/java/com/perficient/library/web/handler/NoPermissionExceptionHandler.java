package com.perficient.library.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.exception.NoPermissionException;
import com.perficient.library.web.domain.ReturnResult;

@ControllerAdvice
public class NoPermissionExceptionHandler {

    @ExceptionHandler(value = NoPermissionException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ReturnResult<List<String>> handle(NoPermissionException e) {
        return ReturnResultUtils.error(e.getMessage());
    }

}
