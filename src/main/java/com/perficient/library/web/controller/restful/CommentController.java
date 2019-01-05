package com.perficient.library.web.controller.restful;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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

import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.exception.UnauthorizedException;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Comment;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.CommentService;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/book_property")
@Api("comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/comment")
    @ApiOperation("add a comment")
    public ReturnResult<Comment> addComment(@Valid @RequestBody Comment newComment, BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        // Book property's id must exist
        Integer propertyId = newComment.getProperty().getId();
        BookProperty dbProperty = null;
        if (propertyId == null || (dbProperty = bookPropertyService.findOne(propertyId)) == null) {
            throw new RestServiceException("the property is not exist");
        }

        // Employee's id must exist
        Integer employeeId = newComment.getEmployee().getId();
        Employee dbEmployee = null;
        if (employeeId == null || (dbEmployee = employeeService.findOne(employeeId)) == null) {
            throw new RestServiceException("the employee is not exist");
        }

        newComment.setId(null);
        newComment.setProperty(dbProperty);
        newComment.setEmployee(dbEmployee);
        newComment = commentService.save(newComment);
        return ReturnResultUtils.success("save succeeded", newComment);
    }

    @PutMapping("/comment/{commentId}")
    @ApiOperation("update a comment by comment id")
    public ReturnResult<Comment> updateComment(@PathVariable("commentId") Integer commentId,
        @Valid @RequestBody Comment updatedComment, BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        // Comment's id must exist
        Comment dbComment = null;
        if (commentId == null || (dbComment = commentService.findOne(commentId)) == null) {
            throw new RestServiceException("the comment is not exist");
        }

        dbComment.setContent(updatedComment.getContent());
        dbComment.setRating(updatedComment.getRating());
        dbComment = commentService.save(dbComment);
        return ReturnResultUtils.success("update succeeded", dbComment);
    }

    @GetMapping("/{propertyId}/employee/comment")
    @ApiOperation("get employee's comments for a book property by property id")
    public ReturnResult<List<Comment>> getEmployeeCommentsForProperty(@PathVariable("propertyId") Integer propertyId,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        BookProperty dbProperty = bookPropertyService.findOne(propertyId);
        if (dbProperty == null) {
            throw new RestServiceException("the property is not exist");
        }

        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }

        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<Comment> comments = commentService.findByPropertyAndEmployee(dbProperty, employee, pageable);
        return ReturnResultUtils.successPaged(comments.getContent(), comments.getTotalElements());
    }

    @GetMapping("/{propertyId}/comment")
    @ApiOperation("get comments for a book property by property id")
    public ReturnResult<List<Comment>> getCommentsByBookId(@PathVariable("propertyId") Integer propertyId,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        BookProperty dbProperty = null;
        if ((dbProperty = bookPropertyService.findOne(propertyId)) == null) {
            throw new RestServiceException("the property is not exist");
        }

        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size), Direction.DESC,
            "createDate");
        Page<Comment> comments = commentService.findByProperty(dbProperty, pageable);
        return ReturnResultUtils.successPaged(comments.getContent(), comments.getTotalElements());
    }

    // TODO TBD
    @DeleteMapping("/comment/{commentId}")
    @ApiOperation("delete a comment by comment id")
    public ReturnResult<String> deleteComment(@PathVariable("commentId") Integer commentId) {
        if (!commentService.exists(commentId)) {
            throw new RestServiceException("the comment is not exist");
        }
        commentService.delete(commentId);
        return ReturnResultUtils.success("delete succeeded", null);
    }
}
