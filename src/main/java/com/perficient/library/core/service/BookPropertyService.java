package com.perficient.library.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.model.BookProperty;

public interface BookPropertyService extends BaseService<BookProperty, Integer> {

    Page<BookProperty> findAll(Pageable pageable);

    BookProperty findByIsbn(String isbn);

    Page<BookProperty> findBySearchValue(Specification<BookProperty> spec, Pageable pageable);

    List<BookProperty> findPopularBookProperty(Integer size);

    List<BookProperty> findPopularBookProperty(Date beforeDate, Date afterDate, Integer size);

    Page<BookProperty> findByCategory(Integer categoryId, String searchValue, Pageable pageable);

    List<BookProperty> findByCategory(Integer categoryId);

    Integer findBorrowedAmountByPropertyId(Date beforeDate, Date afterDate, Integer propertyId);

    void exportBookProperty(Object object);

}
