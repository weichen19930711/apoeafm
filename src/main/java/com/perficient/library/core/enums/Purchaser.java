package com.perficient.library.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.perficient.library.core.exception.EnumValueMismatchException;

public enum Purchaser {

    COMPANY("Company"), LABOR_UNION("Labor Union");

    private String value;

    Purchaser(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Purchaser getEnum(String value) {
        for (Purchaser purchaser : values()) {
            if (purchaser.getValue().equalsIgnoreCase(value.toLowerCase())) {
                return purchaser;
            }
        }
        throw new EnumValueMismatchException("purchaser", Purchaser.class);
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
