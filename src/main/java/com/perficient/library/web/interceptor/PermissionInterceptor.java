package com.perficient.library.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.NoPermissionException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav)
        throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PermissionRequired permission = handlerMethod.getMethodAnnotation(PermissionRequired.class);
            if (permission != null) {
                // need permissions to access
                Role annotationRole = permission.role();
                // PermissionInterceptor is after LoginIntercetor, so employee is certainty existing
                Role employeeRole = EmployeeContextUtils.getEmpInSession().getRole();
                if (employeeRole.getPoints() < annotationRole.getPoints()) {
                    // employee's permission is lower than the permission defined on this method
                    // so this request is forbidden
                    throw new NoPermissionException();
                }
            }
        }
        return true;
    }

}
