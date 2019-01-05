package com.perficient.library.web.controller.restful;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.HttpUtils;
import com.perficient.library.common.utils.JacksonUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.RecommendedBook;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.CategoryService;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.core.service.RecommendedBookService;
import com.perficient.library.douban.model.DoubanBook;
import com.perficient.library.douban.model.DoubanMessage;
import com.perficient.library.export.ExportXLS;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/book_property")
@Api("book_property")
public class BookPropertyController extends ExportXLS {

    private static final String DOUBAN_ISBN_API = "https://api.douban.com/v2/book/isbn/";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private BookService bookService;

    @Autowired
    private RecommendedBookService recommendedBookService;

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping
    @ApiOperation("(Librarian Only) get all book properties")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<BookProperty>> getAllBookProperties(
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
        Specification<BookProperty> spec = PageUtils.buildSpecificationForBookProperty(searchValue);
        Page<BookProperty> properties = bookPropertyService.findBySearchValue(spec, pageable);
        return ReturnResultUtils.successPaged(properties.getContent(), properties.getTotalElements());
    }

    @GetMapping("/{propertyId}")
    @ApiOperation("get a book property by book property id")
    public ReturnResult<BookProperty> getBookPropertyById(@PathVariable("propertyId") Integer propertyId) {

        BookProperty dbProperty = null;
        if ((dbProperty = bookPropertyService.findOne(propertyId)) == null) {
            throw new RestServiceException("the book property is not exist");
        }

        return ReturnResultUtils.success(dbProperty);
    }

    @GetMapping("/isbn/{isbn}")
    @ApiOperation("get book property by isbn")
    public ReturnResult<BookProperty> getBookPropertyByIsbn(@PathVariable("isbn") String isbn) {

        BookProperty property = bookPropertyService.findByIsbn(isbn);
        if (property == null) {
            throw new RestServiceException("the book property is not exist");
        }

        return ReturnResultUtils.success(property);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation("get book properties by category id")
    public ReturnResult<List<BookProperty>> getBookPropertiesByCategory(@PathVariable("categoryId") Integer categoryId,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {

        if ((categoryService.findOne(categoryId)) == null) {
            throw new RestServiceException("the category is not exist");
        }

        Pagination pagnation = Pagination.generatePagnation(page, size);
        PageRequest pageable = null;
        if ("asc".equalsIgnoreCase(order)) {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.ASC, key);
        } else {
            pageable = PageUtils.buildPageRequest(pagnation, Direction.DESC, key);
        }
        Page<BookProperty> properties = bookPropertyService.findByCategory(categoryId,
            searchValue == null ? "" : searchValue, pageable);
        return ReturnResultUtils.successPaged(properties.getContent(), properties.getTotalElements());
    }

    @GetMapping("/search/isbn/{isbn}")
    @ApiOperation("get book property by isbn, if isbn not in database, will search the douban api")
    public ReturnResult<BookProperty> getBookPropertyByIsbnAndSearchDouban(@PathVariable("isbn") String isbn) {

        BookProperty property = bookPropertyService.findByIsbn(isbn);
        if (property == null) {
            String json = HttpUtils.doGet(DOUBAN_ISBN_API + isbn);
            DoubanMessage doubanMessage = JacksonUtils.getObject(json, DoubanMessage.class);
            if (doubanMessage.getMsg() != null && doubanMessage.getCode() == 112) {
                return ReturnResultUtils.error("fetch douban api failed, " + doubanMessage.getMsg());
            }

            DoubanBook doubanBook = JacksonUtils.getObject(json, DoubanBook.class);
            // if isbn is null, means this book is not found in douban
            if (doubanBook.getIsbn() != null) {
                property = doubanBook.generateBookProperty();
            }
        }

        if (property == null) {
            return ReturnResultUtils.error("the isbn cannot be found in library records and douban");
        }

        return ReturnResultUtils.success(property);
    }

    @GetMapping("/search")
    @ApiOperation("get book property by keywords (title, isbn, author, tags)")
    public ReturnResult<List<BookProperty>> getBookPropertysBySearchValue(
        @RequestParam(value = "keywords", required = false) String searchValue,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {
        if (StringUtils.isBlank(searchValue)) {
            return null;
        }
        Specification<BookProperty> spec = PageUtils.buildSpecification(searchValue);
        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));

        Page<BookProperty> bookPropertys = bookPropertyService.findBySearchValue(spec, pageable);
        return ReturnResultUtils.successPaged(bookPropertys.getContent(), bookPropertys.getTotalElements());
    }

    @GetMapping("/latest")
    @ApiOperation("get book properties generated by latest books")
    public ReturnResult<List<BookProperty>> getLatestBookProperties(
        @RequestParam(value = "size", required = false) Integer size) {

        if (size == null || size <= 0) {
            size = Pagination.DEFAULT_SIZE;
        }
        if (size > Pagination.MAX_SIZE) {
            size = Pagination.MAX_SIZE;
        }
        List<Book> books = bookService.findLatestBooks(size);
        List<BookProperty> resultList = new ArrayList<BookProperty>();
        books.forEach(book -> {
            resultList.add(book.getProperty());
        });
        return ReturnResultUtils.success(resultList);
    }

    @GetMapping("/popular")
    @ApiOperation("get most popular book properties")
    public ReturnResult<List<BookProperty>> getMostPopularBookProperties() {
        Integer size = configurationService.get().getMostPopularAmount();
        List<BookProperty> properties = bookPropertyService.findPopularBookProperty(size);
        return ReturnResultUtils.success(properties);
    }

    @GetMapping("/export")
    @ApiOperation("(Librarian Only) export lost records")
    @PermissionRequired(role = Role.LIBRARIAN)
    public void exportLostRecord(@RequestParam(value = "propertyType", required = false) String propertyType,
        @RequestParam(value = "categoryId", required = false) Integer categoryId,
        @RequestParam(value = "searchValue", required = false) String searchValue,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "order", required = false) String order) {
        Page<BookProperty> properties = null;
        Integer size = null;
        ReturnResult<List<BookProperty>> result = null;
        // export properties at the category
        if ("category".equalsIgnoreCase(propertyType)) {
            if (categoryId != null) {
                if ((categoryService.findOne(categoryId)) == null) {
                    throw new RestServiceException("the category is not exist");
                }
                properties = bookPropertyService.findByCategory(categoryId, searchValue == null ? "" : searchValue,
                    null);
                size = (int) properties.getTotalElements();
                if (size == 0) {
                    bookPropertyService.exportBookProperty(properties.getContent());
                } else {
                    result = getBookPropertiesByCategory(categoryId, 1, size, searchValue, key, order);
                    bookPropertyService.exportBookProperty(result.getData());
                }
                return;
            } else {
                throw new RestServiceException("the categoryId can not be null");
            }
        }

        // export recommended
        if ("recommended".equalsIgnoreCase(propertyType)) {
            Specification<RecommendedBook> spec = PageUtils.buildSpecificationForRecommendedBook(searchValue);
            Page<RecommendedBook> recommendedBooks = recommendedBookService.findRecommendedBooks(spec, null);
            size = (int) recommendedBooks.getTotalElements();
            if (size == 0) {
                bookPropertyService.exportBookProperty(recommendedBooks.getContent());
            } else {
                Pagination pagnation = Pagination.generatePagnation(1, size);
                PageRequest pageable = null;
                if ("asc".equalsIgnoreCase(order)) {
                    pageable = PageUtils.buildPageRequest(pagnation, Direction.ASC, key);
                } else {
                    pageable = PageUtils.buildPageRequest(pagnation, Direction.DESC, key);
                }
                recommendedBooks = recommendedBookService.findRecommendedBooks(spec, pageable);
                List<BookProperty> resultList = new ArrayList<BookProperty>();
                recommendedBooks.getContent().forEach(book -> {
                    resultList.add(book.getProperty());
                });
                bookPropertyService.exportBookProperty(resultList);
            }
            return;
        }

        // export all properties
        if (!"catetory".equalsIgnoreCase(propertyType) || !"recommended".equalsIgnoreCase(propertyType)) {
            Specification<BookProperty> spec = PageUtils.buildSpecificationForBookProperty(searchValue);
            properties = bookPropertyService.findBySearchValue(spec, null);
            size = (int) properties.getTotalElements();
            if (size == 0) {
                bookPropertyService.exportBookProperty(properties.getContent());
            } else {
                result = getAllBookProperties(1, size, searchValue, key, order);
                bookPropertyService.exportBookProperty(result.getData());
            }
            return;
        }
    }

}
