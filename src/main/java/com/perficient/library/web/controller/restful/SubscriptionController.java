package com.perficient.library.web.controller.restful;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.exception.UnauthorizedException;
import com.perficient.library.core.model.Book;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.Subscription;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.BookService;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.core.service.SubscriptionService;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/book")
@Api("subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private BookService bookService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/subscription")
    @ApiOperation("add a new subscription")
    public ReturnResult<Subscription> addBookSubscription(@Valid @RequestBody Subscription subscription,
        BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        Integer propertyId = subscription.getProperty().getId();
        BookProperty dbProperty = null;
        if (propertyId == null || (dbProperty = bookPropertyService.findOne(propertyId)) == null) {
            throw new RestServiceException("the book property is not exist");
        }

        Integer employeeId = subscription.getEmployee().getId();
        Employee dbEmployee = null;
        if (employeeId == null || (dbEmployee = employeeService.findOne(employeeId)) == null) {
            throw new RestServiceException("the employee is not exist");
        }

        // If the associated books exist available one, the subscribe operation will not change anything
        List<Book> books = bookService.findByProperty(dbProperty);
        if (books != null && !books.isEmpty()) {
            for (Book book : books) {
                if (BookStatus.AVAILABLE.equals(book.getStatus())) {
                    throw new RestServiceException("there exists available books, don't need to subscribe");
                }
            }
        }

        // One person can only subscribe a book once
        if (subscriptionService.findByBookAndEmployee(dbProperty, dbEmployee) != null) {
            throw new RestServiceException("you have already subscribed this book");
        }

        subscription.setId(null);
        subscription.setProperty(dbProperty);
        subscription.setEmployee(dbEmployee);
        subscription = subscriptionService.save(subscription);
        return ReturnResultUtils.success("save succeeded", subscription);
    }

    //TODO the API is not reasonable, it should be delete by it's owner rather than everyone
    @DeleteMapping("/subscription/{subscriptionId}")
    @ApiOperation("delete a subscription by subscription id")
    public ReturnResult<String> deleteBookSubscription(@PathVariable("subscriptionId") Integer subscriptionId) {

        if (!subscriptionService.exists(subscriptionId)) {
            throw new RestServiceException("the subscription is not exist");
        }
        subscriptionService.delete(subscriptionId);
        return ReturnResultUtils.success("delete succeeded");
    }

    @GetMapping("/{bookId}/subscription")
    @ApiOperation("get employee's subscription by book id")
    public ReturnResult<Subscription> getSubscriptionByBookId(@PathVariable("bookId") Integer bookId) {

        BookProperty dbBook = null;
        if (bookId == null || (dbBook = bookPropertyService.findOne(bookId)) == null) {
            throw new RestServiceException("the book is not exist");
        }
        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }
        return ReturnResultUtils.success(subscriptionService.findByBookAndEmployee(dbBook, employee));
    }

    @GetMapping("/subscription")
    @ApiOperation("get employee's subscriptions")
    public ReturnResult<List<Subscription>> getSubscriptions() {

        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }
        return ReturnResultUtils.success(subscriptionService.findByEmployee(employee));
    }

}
