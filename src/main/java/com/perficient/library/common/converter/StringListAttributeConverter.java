package com.perficient.library.common.converter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class StringListAttributeConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (String string : attribute) {
            buffer.append(string).append(",");
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        if (!dbData.contains(",")) {
            return Lists.newArrayList(dbData);
        }
        String[] strs = dbData.split(",");
        List<String> resultList = new ArrayList<String>();
        for (String str : strs) {
            resultList.add(str);
        }
        return resultList;
    }

}
