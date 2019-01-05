package com.perficient.library.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;

public interface BookService extends BaseService<Book, Integer> {

    List<Book> findByProperty(BookProperty property);

    Page<Book> findByProperty(BookProperty property, Pageable pageable);

    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    Page<Book> findByPropertyAndStatus(BookProperty property, BookStatus status, Pageable pageable);

    Book findByTagNumber(String tagNumber);

    List<Book> findLatestBooks(Integer size);

    Book findLastBook();

    Page<Book> findAll(Pageable pageable);

    Page<Book> findByIsbn(String isbn, Pageable pageable);

    List<Book> findByPropertyIsbnAndStatus(String isbn, BookStatus status);

    Page<Book> findBySearchValue(Specification<Book> spec, Pageable pageable);

    void exportBook(Object object);
}
