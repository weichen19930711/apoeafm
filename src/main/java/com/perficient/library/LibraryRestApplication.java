package com.perficient.library;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.perficient.library.common.utils.MailTemplateUtils;
import com.perficient.library.core.model.Configuration;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.mail.model.MailTemplate;
import com.perficient.library.mail.service.MailTemplateService;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class LibraryRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryRestApplication.class, args);
    }

    @Bean
    public Configuration setDefaultConfiguration(ConfigurationService configurationService) {
        Configuration config = configurationService.get();
        if (config == null) {
            config = new Configuration();
            config.setMostPopularAmount(5);
            config.setMaxRenewTimes(2);
            config.setMaxBorrowingAmount(3);
            config.setRecommendedAmount(5);
            config.setReminderDaysBefore(7);
            config.setAvailableBorrowingDays(60);
            config.setRenewDaysBefore(7);
            config.setRenewAddedDays(30);
            configurationService.save(config);
        }
        return config;
    }

    @Bean
    public MailTemplate addTemplates(MailTemplateService templateService) throws IOException {

        MailTemplate advanceReminderMail = MailTemplateUtils.generateAdvanceReminderTemplate();
        if (templateService.findByName(advanceReminderMail.getName()) == null) {
            templateService.save(advanceReminderMail);
        }

        MailTemplate overdueReminderMail = MailTemplateUtils.generateOverdueReminderTemplate();
        if (templateService.findByName(overdueReminderMail.getName()) == null) {
            templateService.save(overdueReminderMail);
        }

        MailTemplate subscriptionReminderMail = MailTemplateUtils.generateSubscriptionReminderTemplate();
        if (templateService.findByName(subscriptionReminderMail.getName()) == null) {
            templateService.save(subscriptionReminderMail);
        }

        return null;
    }

}
