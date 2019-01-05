package com.perficient.library.core.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.perficient.library.core.model.Category;

public interface CategoryService extends BaseService<Category, Integer> {

    Category findByCategoryName(String categoryName);

    Page<Category> findAll(Pageable pageable);

    Integer findBookAmountByCategoryId(Integer categoryId);

    Integer findNoCategoryBookAmount();

    Integer findBorrowedReocrdAmountByCategoryId(Date beforeDate, Date afterDate, Integer categoryId);

}
