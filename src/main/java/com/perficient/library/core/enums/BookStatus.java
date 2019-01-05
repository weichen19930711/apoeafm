package com.perficient.library.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.perficient.library.core.exception.EnumValueMismatchException;

public enum BookStatus {

    AVAILABLE("available"), CHECKED_OUT("checked out"), DAMAGED("damaged"), LOST("lost");

    private String value;

    BookStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static BookStatus getEnum(String value) {
        for (BookStatus bookStatus : values()) {
            if (bookStatus.getValue().equals(value.toLowerCase())) {
                return bookStatus;
            }
        }
        throw new EnumValueMismatchException("status", BookStatus.class);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.value;
    }

}
