package com.perficient.library.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    ADMIN("admin", 1000), LIBRARIAN("librarian", 999), EMPLOYEE("employee", 1);

    private String roleName;

    private int points;

    Role(String roleName, int points) {
        this.roleName = roleName;
        this.points = points;
    }

    @JsonCreator
    public static Role getEnum(String roleName) {
        for (Role role : values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.roleName;
    }

}
