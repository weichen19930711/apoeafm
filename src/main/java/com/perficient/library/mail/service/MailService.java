package com.perficient.library.mail.service;

import com.perficient.library.mail.model.MailQueue;

public interface MailService {

    boolean send(MailQueue queue);

}
