package com.perficient.library.web.domain;

import java.util.List;

public class ReturnResult<T> {

    private List<String> message;

    private T data;

    private boolean success;

    public List<String> getMessage() {
        return message;
    }

    public ReturnResult<T> message(List<String> message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ReturnResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public ReturnResult<T> success(boolean success) {
        this.success = success;
        return this;
    }

}
