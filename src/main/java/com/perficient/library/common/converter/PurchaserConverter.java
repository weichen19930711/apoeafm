package com.perficient.library.common.converter;

import org.springframework.core.convert.converter.Converter;

import com.perficient.library.core.enums.Purchaser;

public class PurchaserConverter implements Converter<String, Purchaser> {

    @Override
    public Purchaser convert(String source) {
        return Purchaser.getEnum(source);
    }

}
