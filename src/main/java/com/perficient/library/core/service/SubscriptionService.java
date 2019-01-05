package com.perficient.library.core.service;

import java.util.List;

import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.Subscription;

public interface SubscriptionService extends BaseService<Subscription, Integer> {

    List<Subscription> findByProperty(BookProperty property);

    List<Subscription> findByEmployee(Employee employee);

    Subscription findByBookAndEmployee(BookProperty book, Employee employee);

    void remindSubscribers(Book book);

}
