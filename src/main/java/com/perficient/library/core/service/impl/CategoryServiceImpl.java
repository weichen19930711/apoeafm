package com.perficient.library.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.perficient.library.core.model.Category;
import com.perficient.library.core.repository.CategoryRepository;
import com.perficient.library.core.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository bookCategoryRepository;

    @Override
    public Category save(Category entity) {
        return bookCategoryRepository.save(entity);
    }

    @Override
    public List<Category> findAll() {
        return bookCategoryRepository.findAll();
    }

    @Override
    public Category findOne(Integer id) {
        return bookCategoryRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        bookCategoryRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return bookCategoryRepository.exists(id);
    }

    @Override
    public Category findByCategoryName(String categoryName) {
        return bookCategoryRepository.findByName(categoryName);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return bookCategoryRepository.findAll(pageable);
    }

    @Override
    public Integer findBookAmountByCategoryId(Integer categoryId) {
        return bookCategoryRepository.findBookAmountByCategoryId(categoryId);
    }

    @Override
    public Integer findNoCategoryBookAmount() {
        return bookCategoryRepository.findNoCategoryBookAmount();
    }

    @Override
    public Integer findBorrowedReocrdAmountByCategoryId(Date beforeDate, Date afterDate, Integer categoryId) {
        return bookCategoryRepository.findBorrowedReocrdAmountByCategoryId(beforeDate, afterDate, categoryId);
    }

}
