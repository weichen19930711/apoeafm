package com.perficient.library.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.web.domain.ReturnResult;

/**
 * handle the MethodArgumentNotValidException which is thrown when validation on an argument annotated with @Valid
 * fails.
 * 
 * @author bin.zhou
 *
 */
@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ReturnResult<List<String>> handle(MethodArgumentNotValidException e) {
        return ReturnResultUtils.error(ErrorConvertUtils.convertToList(e.getBindingResult().getAllErrors()));
    }

}
