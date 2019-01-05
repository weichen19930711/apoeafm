package com.perficient.library.common.converter;

import org.springframework.core.convert.converter.Converter;

import com.perficient.library.core.enums.BookStatus;

public class BookStatusConverter implements Converter<String, BookStatus> {

    @Override
    public BookStatus convert(String source) {
        return BookStatus.getEnum(source);
    }

}
