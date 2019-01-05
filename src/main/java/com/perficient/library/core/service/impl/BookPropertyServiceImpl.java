package com.perficient.library.core.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.perficient.library.common.utils.ServletUtils;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.repository.BookPropertyRepository;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.export.ExportXLS;

@Service
public class BookPropertyServiceImpl extends ExportXLS implements BookPropertyService {

    public static final String BOOKPROPERTY_LIST_TEMPLATE_PATH = "/xls-templates/bookPropertyListTemplate.xls";

    public static final String BOOKPROPERTY_LIST_REPORT_BASE_NAME = "BookPropertyReport";

    public static final String BOOKPROPERTY_LIST_CONTEXT_KEY = "bookProperties";

    @Autowired
    private BookPropertyRepository bookRepository;

    @Override
    public BookProperty save(BookProperty property) {
        return bookRepository.save(property);
    }

    @Override
    public List<BookProperty> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<BookProperty> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public BookProperty findOne(Integer id) {
        return bookRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        bookRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return bookRepository.exists(id);
    }

    @Override
    public BookProperty findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Page<BookProperty> findBySearchValue(Specification<BookProperty> spec, Pageable pageable) {
        return bookRepository.findAll(spec, pageable);
    }

    @Override
    public List<BookProperty> findPopularBookProperty(Integer size) {
        return bookRepository.findPopularBookProperty(size);
    }

    @Override
    public Page<BookProperty> findByCategory(Integer categoryId, String searchValue, Pageable pageable) {
        return bookRepository.findPropertyByCategoryId(categoryId, searchValue, pageable);
    }

    @Override
    public List<BookProperty> findByCategory(Integer categoryId) {
        return bookRepository.findPropertyByCategoryId(categoryId);
    }

    @Override
    public Integer findBorrowedAmountByPropertyId(Date beforeDate, Date afterDate, Integer propertyId) {
        return bookRepository.findBorrowedAmountByPropertyId(beforeDate, afterDate, propertyId);
    }

    @Override
    public List<BookProperty> findPopularBookProperty(Date beforeDate, Date afterDate, Integer size) {
        return bookRepository.findPopularBookProperty(beforeDate, afterDate, size);
    }

    @Override
    public void exportBookProperty(Object object) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(BOOKPROPERTY_LIST_CONTEXT_KEY, object);
        try {
            export(response, BOOKPROPERTY_LIST_REPORT_BASE_NAME, BOOKPROPERTY_LIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
