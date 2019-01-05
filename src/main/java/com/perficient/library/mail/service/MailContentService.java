package com.perficient.library.mail.service;

import java.util.Map;

public interface MailContentService {

    String readFromHTML(String templatePath, String varName, Object varValue);

    String readFromHTML(String templatePath, Map<String, Object> varMap);
    
    String readFromDB(String templateName, String varName, Object varValue);

    String readFromDB(String templateName, Map<String, Object> varMap);

}
