package com.perficient.library.core.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.perficient.library.common.utils.HttpUtils;
import com.perficient.library.common.utils.JacksonUtils;
import com.perficient.library.core.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String SSO_VALIDATE_URL = "%s?service=%s&ticket=%s&format=json";

    @Value("${library.sso.validate-url}")
    private String validateURL;

    @Value("${library.sso.back-end.base-url}")
    private String backEndBaseURL;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> validateTicket(String ticket, String serviceUrl) {
        if (StringUtils.isBlank(serviceUrl)) {
            serviceUrl = backEndBaseURL;
        }
        String url = String.format(SSO_VALIDATE_URL, validateURL, serviceUrl, ticket);

        String json = HttpUtils.doGet(url);
        Map<String, Object> responseMap = JacksonUtils.readJsonToMap(json);

        Map<String, Object> serviceResponseMap = (Map<String, Object>) responseMap.get("serviceResponse");

        return (Map<String, Object>) serviceResponseMap.get("authenticationSuccess");
    }

}
