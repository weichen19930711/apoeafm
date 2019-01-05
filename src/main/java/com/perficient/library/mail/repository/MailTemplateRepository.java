package com.perficient.library.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perficient.library.mail.model.MailTemplate;

@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long> {

    MailTemplate findByName(String name);

}
