package com.perficient.library.common.converter;

import javax.persistence.AttributeConverter;

import com.perficient.library.core.enums.Role;

public class RoleAttributeConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute.getRoleName();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return Role.getEnum(dbData);
    }

}
