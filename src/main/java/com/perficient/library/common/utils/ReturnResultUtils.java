package com.perficient.library.common.utils;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;
import com.perficient.library.web.domain.PagedReturnResult;
import com.perficient.library.web.domain.ReturnResult;

public class ReturnResultUtils {

    private ReturnResultUtils() {
        throw new RuntimeException("ReturnResultUtils cannot be initialized");
    }

    private static <T> ReturnResult<T> generateSuccessResult(T data) {
        return new ReturnResult<T>().success(true).message(Lists.newArrayList(HttpStatus.OK.getReasonPhrase()))
            .data(data);
    }

    private static <T> ReturnResult<T> generateSuccessPagedResult(T data, long count) {
        return new PagedReturnResult<T>().total(count).success(true)
            .message(Lists.newArrayList(HttpStatus.OK.getReasonPhrase())).data(data);
    }

    public static <T> ReturnResult<T> success(T data) {
        return generateSuccessResult(data);
    }

    public static <T> ReturnResult<T> success(String message, T data) {
        return success(Lists.newArrayList(message), data);
    }

    public static <T> ReturnResult<T> success(List<String> message, T data) {
        return generateSuccessResult(data).message(message);
    }

    public static <T> ReturnResult<T> successPaged(T data, long total) {
        return generateSuccessPagedResult(data, total);
    }

    public static <T> ReturnResult<T> successPaged(String message, T data, long total) {
        return generateSuccessPagedResult(data, total).message(Lists.newArrayList(message));
    }

    public static <T> ReturnResult<T> successPaged(List<String> message, T data, long total) {
        return generateSuccessPagedResult(data, total).message(Lists.newArrayList(message));
    }

    public static <T> ReturnResult<T> error(String message) {
        return error(Lists.newArrayList(message));
    }

    public static <T> ReturnResult<T> error(List<String> message) {
        return new ReturnResult<T>().success(false).message(message);
    }

}
