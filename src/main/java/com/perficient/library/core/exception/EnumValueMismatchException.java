package com.perficient.library.core.exception;

public class EnumValueMismatchException extends RuntimeException {

    private static final long serialVersionUID = 4018655108325667453L;

    private String field;

    private Class<?> enumClass;

    public EnumValueMismatchException() {
        super();
    }

    public EnumValueMismatchException(String field, Class<?> enumClass) {
        this.field = field;
        this.enumClass = enumClass;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

}
