package com.perficient.library.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.DamagedRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.LostRecord;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.model.RecommendedBook;
import com.perficient.library.web.domain.Pagination;

public class PageUtils {

    private static final String DEFAULT_PROPERTY = "id";

    public static PageRequest buildPageRequest(Pagination pagnation) {
        return buildPageRequest(pagnation, null);
    }

    public static PageRequest buildPageRequest(Pagination pagnation, Direction direction, String... properties) {
        if (pagnation == null) {
            return null;
        }
        return buildPageRequest(pagnation.getPage(), pagnation.getSize(), direction, properties);
    }

    public static PageRequest buildPageRequest(Integer page, Integer size, Direction direction, String... properties) {
        List<String> propertyList = null;
        if (properties.length <= 0 || StringUtils.isBlank(properties[0])) {
            propertyList = new ArrayList<String>();
        } else {
            propertyList = new ArrayList<String>(Arrays.asList(properties));
        }
        if (propertyList.isEmpty()) {
            propertyList.add(DEFAULT_PROPERTY);
        }
        Sort sort = null;
        if (Direction.DESC == direction) {
            sort = new Sort(Direction.DESC, propertyList);
        } else {
            sort = new Sort(Direction.ASC, propertyList);
        }
        return new PageRequest(page - 1, size, sort);
    }

    public static Specification<BookProperty> buildSpecification(String searchValue) {
        Specification<BookProperty> spec = new Specification<BookProperty>() {

            @Override
            public Predicate toPredicate(Root<BookProperty> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.like(root.get("isbn").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("title").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("author").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("tags").as(String.class), "%" + searchValue + "%"));
                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return cb.or(predicates);
            }
        };
        return spec;
    }

    public static Specification<Book> buildSpecificationForBook(BookStatus status, String searchValue) {
        Specification<Book> spec = new Specification<Book>() {

            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<Book, BookProperty> bookPropertyJoin = root.join("property");
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                Predicate p4 = null;
                if (!StringUtils.isBlank(searchValue) && status != null) {
                    p1 = cb.like(bookPropertyJoin.get("isbn").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(root.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    p4 = cb.equal(root.get("status").as(BookStatus.class), status);
                    return cb.and(p4, cb.or(p1, p2, p3));
                }
                if (StringUtils.isBlank(searchValue) && status != null) {
                    p4 = cb.equal(root.get("status").as(BookStatus.class), status);
                    return cb.and(p4);
                }
                if (!StringUtils.isBlank(searchValue) && status == null) {
                    p1 = cb.like(bookPropertyJoin.get("isbn").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(root.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    return cb.or(p1, p2, p3);
                }
                return null;
            }
        };
        return spec;
    }

    public static Specification<OverdueRecord> buildSpecificationForOverdueRecord(String searchValue) {
        Specification<OverdueRecord> spec = new Specification<OverdueRecord>() {

            @Override
            public Predicate toPredicate(Root<OverdueRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<OverdueRecord, BorrowRecord> borrowRecordJoin = root.join("borrowRecord");
                Join<BorrowRecord, Book> bookJoin = borrowRecordJoin.join("book");
                Join<BorrowRecord, Employee> employeeJoin = borrowRecordJoin.join("employee");
                Join<Book, BookProperty> bookPropertyJoin = bookJoin.join("property");
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                Predicate p = cb.equal(root.get("returned").as(boolean.class), false);
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(employeeJoin.get("screenName").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookJoin.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    return cb.and(p, cb.or(p1, p2, p3));
                }
                return cb.and(p);

            }
        };
        return spec;
    }

    public static Specification<BorrowRecord> buildSpecificationForBorrowRecord(String searchValue) {
        Specification<BorrowRecord> spec = new Specification<BorrowRecord>() {

            @Override
            public Predicate toPredicate(Root<BorrowRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<BorrowRecord, Book> bookJoin = root.join("book");
                Join<BorrowRecord, Employee> employeeJoin = root.join("employee");
                Join<Book, BookProperty> bookPropertyJoin = bookJoin.join("property");
                Predicate p = cb.equal(bookJoin.get("status").as(BookStatus.class), BookStatus.CHECKED_OUT);
                Predicate p_ = cb.isNull(root.get("checkinDate").as(Date.class));
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(employeeJoin.get("screenName").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookJoin.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    return cb.and(p, p_, cb.or(p1, p2, p3));
                }
                return cb.and(p, p_);

            }
        };
        return spec;
    }

    public static Specification<DamagedRecord> buildSpecificationForDamagedRecord(String searchValue) {
        Specification<DamagedRecord> spec = new Specification<DamagedRecord>() {

            @Override
            public Predicate toPredicate(Root<DamagedRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<DamagedRecord, Book> bookJoin = root.join("book");
                Join<Book, BookProperty> bookPropertyJoin = bookJoin.join("property");
                Join<DamagedRecord, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
                Predicate p = cb.equal(bookJoin.get("status").as(BookStatus.class), BookStatus.DAMAGED);
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(employeeJoin.get("screenName").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookJoin.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    return cb.and(p, cb.or(p1, p2, p3));
                }
                return cb.or(p);

            }
        };
        return spec;
    }

    public static Specification<LostRecord> buildSpecificationForLostRecord(String searchValue) {
        Specification<LostRecord> spec = new Specification<LostRecord>() {

            @Override
            public Predicate toPredicate(Root<LostRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<LostRecord, Book> bookJoin = root.join("book");
                Join<Book, BookProperty> bookPropertyJoin = bookJoin.join("property");
                Join<LostRecord, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
                Predicate p = cb.equal(bookJoin.get("status").as(BookStatus.class), BookStatus.LOST);
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(employeeJoin.get("screenName").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookJoin.get("tagNumber").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    return cb.and(p, cb.or(p1, p2, p3));
                }
                return cb.or(p);

            }
        };
        return spec;
    }

    public static Specification<RecommendedBook> buildSpecificationForRecommendedBook(String searchValue) {
        Specification<RecommendedBook> spec = new Specification<RecommendedBook>() {

            @Override
            public Predicate toPredicate(Root<RecommendedBook> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<RecommendedBook, BookProperty> bookPropertyJoin = root.join("property");
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                Predicate p4 = null;
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(bookPropertyJoin.get("isbn").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(bookPropertyJoin.get("title").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(bookPropertyJoin.get("author").as(String.class), "%" + searchValue + "%");
                    p4 = cb.like(bookPropertyJoin.get("price").as(String.class), "%" + searchValue + "%");
                    return cb.or(p1, p2, p3, p4);
                }
                return null;
            }

        };
        return spec;
    }

    public static Specification<BookProperty> buildSpecificationForBookProperty(String searchValue) {
        Specification<BookProperty> spec = new Specification<BookProperty>() {

            @Override
            public Predicate toPredicate(Root<BookProperty> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p1 = null;
                Predicate p2 = null;
                Predicate p3 = null;
                Predicate p4 = null;
                if (!StringUtils.isBlank(searchValue)) {
                    p1 = cb.like(root.get("isbn").as(String.class), "%" + searchValue + "%");
                    p2 = cb.like(root.get("title").as(String.class), "%" + searchValue + "%");
                    p3 = cb.like(root.get("author").as(String.class), "%" + searchValue + "%");
                    p4 = cb.like(root.get("price").as(String.class), "%" + searchValue + "%");
                    return cb.or(p1, p2, p3, p4);
                }
                return null;
            }
        };
        return spec;
    }
}
