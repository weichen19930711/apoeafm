package com.perficient.library.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perficient.library.mail.model.MailQueue;

@Repository
public interface MailQueueRepository extends JpaRepository<MailQueue, Integer> {

    List<MailQueue> findBySent(boolean sent);

}
