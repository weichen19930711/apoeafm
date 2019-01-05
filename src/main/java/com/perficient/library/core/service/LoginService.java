package com.perficient.library.core.service;

import java.util.Map;

public interface LoginService {

    Map<String, Object> validateTicket(String ticket, String url);
    
}
