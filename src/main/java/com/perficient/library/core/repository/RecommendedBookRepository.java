package com.perficient.library.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.RecommendedBook;

@Repository
public interface RecommendedBookRepository
    extends JpaRepository<RecommendedBook, Integer>, JpaSpecificationExecutor<RecommendedBook> {

    RecommendedBook findByProperty(BookProperty property);

    RecommendedBook findByPropertyIsbn(String isbn);

    @Query("SELECT book FROM RecommendedBook book ORDER BY RAND()")
    Page<RecommendedBook> findRandomRecommendedBooks(Pageable pageable);

}
