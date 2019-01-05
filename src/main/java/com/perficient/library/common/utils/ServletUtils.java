package com.perficient.library.common.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtils {

    private static final int DEFAULT_SESSION_TIMEOUT = 1800;

    /**
     * Get current request in ThreadLocal
     * 
     * @return
     */
    public static HttpServletRequest getCurrentRequest() {
        return ServletUtils.getRequestAttributes().getRequest();
    }

    /**
     * Get current response in ThreadLocal
     * 
     * @return
     */
    public static HttpServletResponse getCurrentResponse() {
        return ServletUtils.getRequestAttributes().getResponse();
    }

    /**
     * Get session in current request
     * 
     * @return
     */
    public static HttpSession getCurrentSession() {
        HttpSession session = ServletUtils.getCurrentRequest().getSession();
        session.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT);
        return session;
    }

    /**
     * Set session attribute by name in current request
     * 
     * @param name
     * @param value
     */
    public static void setSessionAttribute(String name, Object value) {
        ServletUtils.getCurrentSession().setAttribute(name, value);
    }

    /**
     * Get session attribute by name in current request
     * 
     * @param name
     * @return
     */
    public static Object getSessionAttribute(String name) {
        return ServletUtils.getCurrentSession().getAttribute(name);
    }

    /**
     * Remove session attribute by name in current request
     * 
     * @param name
     */
    public static void removeSessionAttribute(String name) {
        ServletUtils.getCurrentSession().removeAttribute(name);
    }

    private static ServletRequestAttributes getRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    }

}
