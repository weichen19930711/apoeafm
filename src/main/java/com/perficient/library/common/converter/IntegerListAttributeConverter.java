package com.perficient.library.common.converter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class IntegerListAttributeConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (Integer integer : attribute) {
            buffer.append(integer).append(",");
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> integers = new ArrayList<Integer>();
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        if (!dbData.contains(",")) {
            List<String> strs = Lists.newArrayList(dbData);
            integers.add(Integer.parseInt(strs.get(0)));
            return integers;
        }
        String[] strs = dbData.split(",");
        for(String str:strs){
            integers.add(Integer.parseInt(str));
        }
        return integers;
    }

}
