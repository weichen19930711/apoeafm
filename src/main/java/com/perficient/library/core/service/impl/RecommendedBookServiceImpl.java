package com.perficient.library.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.RecommendedBook;
import com.perficient.library.core.repository.RecommendedBookRepository;
import com.perficient.library.core.service.RecommendedBookService;

@Service
public class RecommendedBookServiceImpl implements RecommendedBookService {

    @Autowired
    private RecommendedBookRepository recommendedBookRepository;

    @Override
    public RecommendedBook save(RecommendedBook entity) {
        return recommendedBookRepository.save(entity);
    }

    @Override
    public List<RecommendedBook> findAll() {
        return recommendedBookRepository.findAll();
    }

    @Override
    public RecommendedBook findOne(Integer id) {
        return recommendedBookRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        recommendedBookRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return recommendedBookRepository.exists(id);
    }

    @Override
    public RecommendedBook findByProperty(BookProperty property) {
        return recommendedBookRepository.findByProperty(property);
    }

    @Override
    public RecommendedBook findByPropertyIsbn(String isbn) {
        return recommendedBookRepository.findByPropertyIsbn(isbn);
    }

    @Override
    public List<RecommendedBook> findRandomRecommendedBooks(Integer size) {
        return recommendedBookRepository.findRandomRecommendedBooks(new PageRequest(0, size)).getContent();
    }

    @Override
    public Page<RecommendedBook> findRecommendedBooks(Specification<RecommendedBook> spec, Pageable pageable) {
        return recommendedBookRepository.findAll(spec, pageable);
    }

}
