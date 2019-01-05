package com.perficient.library.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Comment;
import com.perficient.library.core.model.Employee;

public interface CommentService extends BaseService<Comment, Integer> {

    Page<Comment> findByProperty(BookProperty property, Pageable pageable);

    Page<Comment> findByPropertyAndEmployee(BookProperty property, Employee employee, Pageable pageable);

}
