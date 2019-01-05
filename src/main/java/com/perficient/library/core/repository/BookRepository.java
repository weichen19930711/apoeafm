package com.perficient.library.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    List<Book> findByProperty(BookProperty property);

    Page<Book> findByProperty(BookProperty property, Pageable pageable);

    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    List<Book> findByPropertyIsbnAndStatus(String isbn, BookStatus status);

    Page<Book> findByPropertyAndStatus(BookProperty property, BookStatus status, Pageable pageable);

    Book findByTagNumber(String tagNumber);

    @Query(value = "SELECT * FROM book ORDER BY id DESC LIMIT 0,1", nativeQuery = true)
    Book findLastBook();

    @Query(value = "SELECT t1.* FROM book t1 JOIN (SELECT property_id, MAX(create_date) as create_date FROM book GROUP BY property_id ) t2 ON t1.property_id = t2.property_id AND t1.create_date = t2.create_date ORDER BY create_date DESC LIMIT 0,?1", nativeQuery = true)
    List<Book> findLatestBooks(Integer size);

    Page<Book> findByPropertyIsbn(String isbn, Pageable pageable);

}
