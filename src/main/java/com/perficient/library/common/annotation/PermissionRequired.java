package com.perficient.library.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.perficient.library.core.enums.Role;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@LoginRequired
public @interface PermissionRequired {

    Role role() default Role.EMPLOYEE;

}
