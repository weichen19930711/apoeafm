package com.perficient.library.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Comment;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.repository.CommentRepository;
import com.perficient.library.core.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment save(Comment bookComment) {
        return commentRepository.save(bookComment);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findOne(Integer id) {
        return commentRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        commentRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return commentRepository.exists(id);
    }

    @Override
    public Page<Comment> findByProperty(BookProperty property, Pageable pageable) {
        return commentRepository.findByProperty(property, pageable);
    }

    @Override
    public Page<Comment> findByPropertyAndEmployee(BookProperty property, Employee employee, Pageable pageable) {
        return commentRepository.findByPropertyAndEmployee(property, employee, pageable);
    }

}
