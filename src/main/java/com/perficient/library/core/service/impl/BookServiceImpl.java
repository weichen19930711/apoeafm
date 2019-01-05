package com.perficient.library.core.service.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.perficient.library.common.utils.ServletUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.repository.BookRepository;
import com.perficient.library.core.service.BookService;
import com.perficient.library.export.ExportXLS;

@Service
public class BookServiceImpl extends ExportXLS implements BookService {

    public static final String BOOKLIST_TEMPLATE_PATH = "/xls-templates/bookListTemplate.xls";

    public static final String BOOKLIST_REPORT_BASE_NAME = "BookReport";

    public static final String BOOKLIST_CONTEXT_KEY = "books";

    public static final String FIRST_TAGNUMBER = "1001";

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book save(Book entity) {
        if (entity.getId() == null) {
            // only insert operation should generate and set a tag number
            Book book = this.findLastBook();
            try {
                Integer tagNum = Integer.parseInt(book.getTagNumber());
                entity.setTagNumber(((Integer) (tagNum + 1)).toString());
            } catch (NumberFormatException e) {
                entity.setTagNumber(FIRST_TAGNUMBER);
            }
        }
        return bookRepository.save(entity);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findOne(Integer id) {
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
    public List<Book> findByProperty(BookProperty property) {
        return bookRepository.findByProperty(property);
    }

    @Override
    public Page<Book> findByProperty(BookProperty property, Pageable pageable) {
        return bookRepository.findByProperty(property, pageable);
    }

    @Override
    public Page<Book> findByStatus(BookStatus status, Pageable pageable) {
        return bookRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Book> findByPropertyIsbnAndStatus(String isbn, BookStatus status) {
        return bookRepository.findByPropertyIsbnAndStatus(isbn, status);
    }

    @Override
    public Page<Book> findByPropertyAndStatus(BookProperty property, BookStatus status, Pageable pageable) {
        return bookRepository.findByPropertyAndStatus(property, status, pageable);
    }

    @Override
    public Book findByTagNumber(String tagNumber) {
        return bookRepository.findByTagNumber(tagNumber);
    }

    @Override
    public List<Book> findLatestBooks(Integer size) {
        return bookRepository.findLatestBooks(size);
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> findByIsbn(String isbn, Pageable pageable) {
        return bookRepository.findByPropertyIsbn(isbn, pageable);
    }

    @Override
    public Book findLastBook() {
        return bookRepository.findLastBook();
    }

    @Override
    public Page<Book> findBySearchValue(Specification<Book> spec, Pageable pageable) {
        return bookRepository.findAll(spec, pageable);
    }

    @Override
    public void exportBook(Object books) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(BOOKLIST_CONTEXT_KEY, books);
        try {
            export(response, BOOKLIST_REPORT_BASE_NAME, BOOKLIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
