package com.perficient.library.common.converter;

import javax.persistence.AttributeConverter;

import com.perficient.library.core.enums.BookStatus;

public class BookStatusAttributeConverter implements AttributeConverter<BookStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookStatus attribute) {
        return attribute.getValue();
    }

    @Override
    public BookStatus convertToEntityAttribute(String dbData) {
        return BookStatus.getEnum(dbData);
    }

}
