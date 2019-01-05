package com.perficient.library.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.RecommendedBook;

public interface RecommendedBookService extends BaseService<RecommendedBook, Integer> {

    RecommendedBook findByProperty(BookProperty property);

    RecommendedBook findByPropertyIsbn(String isbn);

    List<RecommendedBook> findRandomRecommendedBooks(Integer size);

    Page<RecommendedBook> findRecommendedBooks(Specification<RecommendedBook> spec, Pageable pageable);
}
