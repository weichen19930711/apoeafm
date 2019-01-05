package com.perficient.library.web.controller.restful;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.RecommendedBook;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.core.service.RecommendedBookService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only Librarian can add/update/delete recommended books.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/book")
@Api("recommended_book")
@Transactional
public class RecommendedBookController {

    @Autowired
    private RecommendedBookService recommendedBookService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ConfigurationService configurationService;

    // TODO API format is unreasonable
    @PostMapping("/isbn/{isbn}/recommended")
    @ApiOperation("(Librarian Only) add a recommended book by isbn")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<RecommendedBook> addRecommendedBookByIsbn(@PathVariable("isbn") String isbn) {
        if (StringUtils.isEmpty(isbn)) {
            throw new RestServiceException("isbn cannot be empty");
        }
        BookProperty dbProperty = bookPropertyService.findByIsbn(isbn);
        if (dbProperty == null) {
            throw new RestServiceException("the book property is not exist");
        }
        if (recommendedBookService.findByProperty(dbProperty) != null) {
            throw new RestServiceException("the recommended book already exist");
        }

        RecommendedBook recommendedBook = new RecommendedBook();
        recommendedBook.setId(null);
        recommendedBook.setProperty(dbProperty);
        recommendedBook = recommendedBookService.save(recommendedBook);
        return ReturnResultUtils.success("save succeeded", recommendedBook);
    }

    // TODO API format is unreasonable
    @PostMapping("/tag_number/{tagNumber}/recommended")
    @ApiOperation("(Librarian Only) add a recommended book by tag number")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<RecommendedBook> addRecommendedBookByTagNumber(@PathVariable("tagNumber") String tagNumber) {
        if (StringUtils.isEmpty(tagNumber)) {
            throw new RestServiceException("tag number cannot be empty");
        }

        Book dbBook = bookService.findByTagNumber(tagNumber);
        if (dbBook == null) {
            throw new RestServiceException("the book is not exist");
        }

        BookProperty dbProperty = dbBook.getProperty();
        if (recommendedBookService.findByProperty(dbProperty) != null) {
            throw new RestServiceException("the recommended book already exist");
        }

        RecommendedBook recommendedBook = new RecommendedBook();
        recommendedBook.setId(null);
        recommendedBook.setProperty(dbProperty);
        recommendedBook = recommendedBookService.save(recommendedBook);
        return ReturnResultUtils.success("save succeeded", recommendedBook);
    }

    @PostMapping("/recommended/property")
    @ApiOperation("(Librarian Only) add book properties to recommened list")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> addRecommendedBook(@RequestParam(value = "propertyIds") String[] propertyIds) {
        for (String item : propertyIds) {
            Integer propertyId = null;
            try {
                propertyId = Integer.parseInt(item);
            } catch (Exception e) {
                throw new RestServiceException("the book property is not exist");
            }

            BookProperty dbProperty = null;
            if (propertyId == null || (dbProperty = bookPropertyService.findOne(propertyId)) == null) {
                throw new RestServiceException("the book property is not exist");
            }

            if (recommendedBookService.findByProperty(dbProperty) != null) {
                continue;
            }

            RecommendedBook recommendedBook = new RecommendedBook();
            recommendedBook.setId(null);
            recommendedBook.setProperty(dbProperty);
            recommendedBook = recommendedBookService.save(recommendedBook);
        }
        return ReturnResultUtils.success("save succeeded");
    }

    @DeleteMapping("/recommended/property")
    @ApiOperation("(Librarian Only) remove book properties from recommened list")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> deleteRecommendedBook(@RequestParam(value = "propertyIds") String[] propertyIds) {
        for (String item : propertyIds) {
            Integer propertyId = null;
            try {
                propertyId = Integer.parseInt(item);
            } catch (Exception e) {
                throw new RestServiceException("the book property not exist");
            }

            BookProperty dbProperty = null;
            if (propertyId == null || (dbProperty = bookPropertyService.findOne(propertyId)) == null) {
                throw new RestServiceException("the book property is not exist");
            }

            RecommendedBook recommendedBook = null;
            if ((recommendedBook = recommendedBookService.findByProperty(dbProperty)) == null) {
                continue;
            }

            recommendedBookService.delete(recommendedBook.getId());
        }
        return ReturnResultUtils.success("delete succeeded", null);
    }

    @GetMapping("/recommended/random")
    @ApiOperation("get random recommended books")
    public ReturnResult<List<RecommendedBook>> getHomeRecommendedBooks() {
        Integer size = configurationService.get().getRecommendedAmount();
        return ReturnResultUtils.success(recommendedBookService.findRandomRecommendedBooks(size));
    }

    @GetMapping("/recommended")
    @ApiOperation("get all recommended books")
    public ReturnResult<List<BookProperty>> getAllRecommendedBooks(
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
        Specification<RecommendedBook> spec = PageUtils.buildSpecificationForRecommendedBook(searchValue);
        Page<RecommendedBook> recommendedBooks = recommendedBookService.findRecommendedBooks(spec, pageable);
        List<BookProperty> resultList = new ArrayList<BookProperty>();
        recommendedBooks.getContent().forEach(book -> {
            resultList.add(book.getProperty());
        });
        long total = recommendedBooks.getTotalElements();
        return ReturnResultUtils.successPaged(resultList, total);
    }

}
