package com.perficient.library.common.converter;

import javax.persistence.AttributeConverter;

import com.perficient.library.core.enums.Purchaser;

public class PurchaserAttributeConverter implements AttributeConverter<Purchaser, String> {

    @Override
    public String convertToDatabaseColumn(Purchaser attribute) {
        return attribute.getValue();
    }

    @Override
    public Purchaser convertToEntityAttribute(String dbData) {
        return Purchaser.getEnum(dbData);
    }

}
