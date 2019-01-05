package com.perficient.library.web.domain;

public class PagedReturnResult<T> extends ReturnResult<T> {

    private long total;

    public long getTotal() {
        return total;
    }

    public PagedReturnResult<T> total(long total) {
        this.total = total;
        return this;
    }

}
