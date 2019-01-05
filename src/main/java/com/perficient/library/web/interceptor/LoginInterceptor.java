package com.perficient.library.web.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.perficient.library.common.annotation.LoginRequired;
import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.core.exception.UnauthorizedException;
import com.perficient.library.core.model.Employee;

public class LoginInterceptor implements HandlerInterceptor {

    @Value("${library.sso.front-end.base-url}")
    private String frontEndBaseURL;

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
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(LoginRequired.class)
                || method.isAnnotationPresent(PermissionRequired.class)) {
                Employee employee = EmployeeContextUtils.getEmpInSession();
                if (employee == null) {
                    // not logged in
                    response.setHeader("Access-Control-Allow-Origin", frontEndBaseURL);
                    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    throw new UnauthorizedException();
                }
            }
        }
        return true;
    }

}
