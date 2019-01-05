package com.perficient.library.web.domain;

public class Pagination {

    public static final Integer DEFAULT_PAGE = 1;

    public static final Integer DEFAULT_SIZE = 20;

    public static final Integer MAX_SIZE = Integer.MAX_VALUE;

    private Integer page;

    private Integer size;

    private Pagination() {
    }

    private Pagination(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public static Pagination generatePagnation(Integer page, Integer size) {
        if (page == null || page <= 0) {
            page = DEFAULT_PAGE;
        }
        if (size == null || page <= 0 || size > MAX_SIZE) {
            size = DEFAULT_SIZE;
        }
        return new Pagination(page, size);
    }

    public static Pagination generatePagnation(Integer page, Integer size, Integer defaultPage, Integer defaultSize) {
        if (defaultPage == null || defaultPage <= 0) {
            defaultPage = DEFAULT_PAGE;
        }
        if (defaultSize == null || defaultSize <= 0) {
            defaultSize = DEFAULT_SIZE;
        }
        if (defaultSize > MAX_SIZE) {
            defaultSize = MAX_SIZE;
        }
        return generatePagnation(defaultPage, defaultSize);
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

}
