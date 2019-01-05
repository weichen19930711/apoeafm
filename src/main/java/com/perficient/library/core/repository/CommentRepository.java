package com.perficient.library.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Comment;
import com.perficient.library.core.model.Employee;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findByProperty(BookProperty property, Pageable pageable);

    Page<Comment> findByPropertyAndEmployee(BookProperty property, Employee employee, Pageable pageable);

}
