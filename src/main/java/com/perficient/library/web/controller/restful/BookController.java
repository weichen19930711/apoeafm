package com.perficient.library.web.controller.restful;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.CategoryService;
import com.perficient.library.core.service.SubscriptionService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * only Librarian can add/update/delete/export books.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/book")
@Api("book")
@Transactional
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Transactional
    @PostMapping()
    @ApiOperation("(Librarian Only) add a new book")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Book> addBook(@Valid @RequestBody Book newBook, BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        BookProperty newProperty = newBook.getProperty();

        // validate category
        List<Integer> categoryIds = newProperty.getCategory();
        if (categoryIds != null) {
            for (int i = 0; i < categoryIds.size(); i++) {
                Integer categoryId = categoryIds.get(i);
                if (categoryId == null || (categoryService.findOne(categoryId)) == null) {
                    throw new RestServiceException("the category is not exist");
                }
            }
            newProperty.setCategory(categoryIds);
        }

        String isbn = newProperty.getIsbn();
        BookProperty dbProperty = bookPropertyService.findByIsbn(isbn);
        if (dbProperty != null) {
            // ISBN already exist in DB:
            newProperty.setId(dbProperty.getId()); // ensure that save method will invoke update operation
        }

        newProperty = bookPropertyService.save(newProperty);

        // ensure that save method will invoke insert operation
        newBook.setId(null);
        newBook.setProperty(newProperty);
        newBook.setStatus(BookStatus.AVAILABLE);
        final Book savedBook = bookService.save(newBook);

        // Send mails to notify the subscribers and delete all associated subscriptions
        new Thread(new Runnable() {

            @Override
            public void run() {
                subscriptionService.remindSubscribers(savedBook);
            }
        }).start();

        return ReturnResultUtils.success("save succeeded", savedBook);
    }

    @PutMapping("/{bookId}")
    @ApiOperation("(Librarian Only) update a book by book id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Book> updateBook(@Valid @RequestBody Book updatedBook, BindingResult result,
        @PathVariable("bookId") Integer bookId) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        Book dbBook = null;
        // Book's id should exist in Database
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }
        updatedBook.setId(bookId);

        BookProperty updatedProperty = updatedBook.getProperty();

        // validate category
        List<Integer> categoryIds = updatedProperty.getCategory();
        if (categoryIds != null) {
            for (int i = 0; i < categoryIds.size(); i++) {
                Integer categoryId = categoryIds.get(i);
                if (categoryId == null || (categoryService.findOne(categoryId)) == null) {
                    throw new RestServiceException("the category is not exist");
                }
            }
            updatedProperty.setCategory(categoryIds);
        }

        BookProperty dbProperty = dbBook.getProperty();
        updatedProperty.setId(dbProperty.getId());
        // ISBN cannot be modified
        updatedProperty.setIsbn(dbProperty.getIsbn());

        // first, update the property entity
        updatedProperty = bookPropertyService.save(updatedProperty);

        // then, set the saved property into book, just update the mapping
        updatedBook.setProperty(updatedProperty);
        // set previous status (cannot modify status directly)
        updatedBook.setStatus(dbBook.getStatus());
        // set tag number (cannot modify tag number)
        updatedBook.setTagNumber(dbBook.getTagNumber());

        // last, update the book
        updatedBook = bookService.save(updatedBook);

        return ReturnResultUtils.success("update succeeded", updatedBook);
    }

    @DeleteMapping("/{bookId}")
    @ApiOperation("(Librarian Only) delete a book by book id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> deleteBook(@PathVariable("bookId") Integer bookId) {

        Book dbBook = null;

        // Book's id should exist in Database
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }

        // If the book already checked out, it cannot be deleted.
        if (BookStatus.CHECKED_OUT.equals(dbBook.getStatus())) {
            throw new RestServiceException("the book has been checked out");
        }

        // If the associated borrow records are not empty, the book cannot be deleted
        List<BorrowRecord> borrowRecords = borrowRecordService.findByBook(dbBook);
        if (borrowRecords != null && !borrowRecords.isEmpty()) {
            throw new RestServiceException("the accociated borrow records are not empty");
        }

        return ReturnResultUtils.success("delete succeeded", null);
    }

    @GetMapping
    @ApiOperation("(Librarian Only) get all books")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<Book>> getAllBooks(@RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Pagination pagnation = Pagination.generatePagnation(page, size);
        Pageable pageable = PageUtils.buildPageRequest(pagnation);
        Page<Book> books = bookService.findAll(pageable);
        return ReturnResultUtils.successPaged(books.getContent(), books.getTotalElements());
    }

    @GetMapping("/tag_number/{tagNumber}")
    @ApiOperation("get book by tag number")
    public ReturnResult<Book> getBookByTagNumber(@PathVariable("tagNumber") String tagNumber) {

        if (StringUtils.isEmpty(tagNumber)) {
            throw new RestServiceException("the tag number cannot be empty");
        }
        return ReturnResultUtils.success(bookService.findByTagNumber(tagNumber));
    }

    // TODO API format is unreasonable
    @GetMapping("/isbn/{isbn}/status/{status}")
    @ApiOperation("get books by isbn and status")
    public ReturnResult<List<Book>> getAvailableBooksByIsbn(@PathVariable("isbn") String isbn,
        @PathVariable("status") BookStatus status) {

        return ReturnResultUtils.success(bookService.findByPropertyIsbnAndStatus(isbn, status));
    }

    @GetMapping("/{bookId}")
    @ApiOperation("get a book by book id")
    public ReturnResult<Book> getBookById(@PathVariable("bookId") Integer bookId) {

        Book dbBook = null;
        if ((dbBook = bookService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }
        return ReturnResultUtils.success(dbBook);
    }

    @GetMapping("/status")
    @ApiOperation("get books by status")
    public ReturnResult<List<Book>> getBooksByStatus(
        @RequestParam(value = "status", required = false) BookStatus status,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Pagination pagnation = Pagination.generatePagnation(page, size);
        PageRequest pageable = null;
        if (Direction.ASC.toString().equalsIgnoreCase(order)) {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.ASC, key);
        } else {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.DESC, key);
        }
        Specification<Book> spec = PageUtils.buildSpecificationForBook(status, searchValue);
        Page<Book> books = bookService.findBySearchValue(spec, pageable);
        return ReturnResultUtils.successPaged(books.getContent(), books.getTotalElements());
    }

    @GetMapping("/export")
    @ApiOperation("(Librarian Only) export books")
    @PermissionRequired(role = Role.LIBRARIAN)
    public void exportAllBook(@RequestParam(value = "status", required = false) BookStatus status,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Specification<Book> spec = PageUtils.buildSpecificationForBook(status, searchValue);
        Page<Book> books = bookService.findBySearchValue(spec, null);
        int size = (int) books.getTotalElements();
        if (size == 0) {
            bookService.exportBook(books.getContent());
        } else {
            ReturnResult<List<Book>> result = getBooksByStatus(status, 1, size, searchValue, key, order);
            bookService.exportBook(result.getData());
        }
    }
}
