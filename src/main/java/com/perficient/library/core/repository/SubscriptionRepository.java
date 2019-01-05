package com.perficient.library.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByProperty(BookProperty property);

    List<Subscription> findByEmployee(Employee employee);

    Subscription findByPropertyAndEmployee(BookProperty property, Employee employee);

}
