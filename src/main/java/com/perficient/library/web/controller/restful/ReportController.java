package com.perficient.library.web.controller.restful;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.DateFormatUtil;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Category;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.vo.BorrowedPropertyVo;
import com.perficient.library.core.model.vo.CategoryBookVo;
import com.perficient.library.core.model.vo.CategoryBorrowedPropertyVo;
import com.perficient.library.core.model.vo.EmployeeBorrowedRecordVo;
import com.perficient.library.core.model.vo.EmployeeOverdueRecordVo;
import com.perficient.library.core.model.vo.PeriodBorrowedRecordVo;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.CategoryService;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/report")
@Api("report")
public class ReportController {

    private static final String NO_CATEGORY_NAME = "NO CATEGORY";

    public static final int DEFAULT_RESULT_SIZE = 10;

    public static final String YEAR_PATTERN = "yyyy";

    public static final String MONTH_PATTERN = "yyyy-MM";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/category_book")
    @ApiOperation("(Librarian Only) report category book")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<CategoryBookVo>> reportCategoryBook() {
        List<Category> categorys = categoryService.findAll();
        List<CategoryBookVo> result = new ArrayList<CategoryBookVo>();
        for (Category item : categorys) {
            CategoryBookVo categoryBookVo = new CategoryBookVo();
            categoryBookVo.setCategoryId(item.getId());
            categoryBookVo.setCategoryName(item.getName());
            categoryBookVo.setBookAmount(categoryService.findBookAmountByCategoryId(item.getId()));
            result.add(categoryBookVo);
        }
        CategoryBookVo categoryBookVo = new CategoryBookVo();
        categoryBookVo.setBookAmount(categoryService.findNoCategoryBookAmount());
        categoryBookVo.setCategoryName(NO_CATEGORY_NAME);
        result.add(categoryBookVo);
        return ReturnResultUtils.success(result);
    }

    @GetMapping("/category_borrowed_reocrd")
    @ApiOperation("(Librarian Only) report category borrowed record")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<CategoryBorrowedPropertyVo>> reportCategoryBorrowedReocrd(
        @RequestParam(value = "beforeDate") @DateTimeFormat(pattern = DATE_PATTERN) Date beforeDate,
        @RequestParam(value = "afterDate") @DateTimeFormat(pattern = DATE_PATTERN) Date afterDate) {
        if (beforeDate == null || afterDate == null) {
            throw new RestServiceException("the date not null");
        }
        if (DateUtils.truncatedCompareTo(beforeDate, afterDate, Calendar.DATE) > 0) {
            throw new RestServiceException("the beforeDate is greater than afterDate");
        }
        List<Category> categorys = categoryService.findAll();
        List<CategoryBorrowedPropertyVo> result = new ArrayList<CategoryBorrowedPropertyVo>();
        for (Category item : categorys) {
            CategoryBorrowedPropertyVo categoryBorrowedPropertyVo = new CategoryBorrowedPropertyVo();
            categoryBorrowedPropertyVo.setCategoryId(item.getId());
            categoryBorrowedPropertyVo.setCategoryName(item.getName());
            Integer borrowedAmount = categoryService.findBorrowedReocrdAmountByCategoryId(beforeDate, afterDate,
                item.getId());
            categoryBorrowedPropertyVo.setBorrowedAmount(borrowedAmount == null ? 0 : borrowedAmount);
            result.add(categoryBorrowedPropertyVo);
        }
        return ReturnResultUtils.success(result);
    }

    @GetMapping("/borrowed_book_amount")
    @ApiOperation("(Librarian Only) report borrowed book amount")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<BorrowedPropertyVo>> reportBorrowedBookAmount(
        @RequestParam(value = "beforeDate") @DateTimeFormat(pattern = DATE_PATTERN) Date beforeDate,
        @RequestParam(value = "afterDate") @DateTimeFormat(pattern = DATE_PATTERN) Date afterDate) {
        if (beforeDate == null || afterDate == null) {
            throw new RestServiceException("the date not null");
        }
        if (DateUtils.truncatedCompareTo(beforeDate, afterDate, Calendar.DATE) > 0) {
            throw new RestServiceException("the beforeDate is greater than afterDate");
        }
        List<BookProperty> properties = bookPropertyService.findPopularBookProperty(beforeDate, afterDate,
            DEFAULT_RESULT_SIZE);
        List<BorrowedPropertyVo> result = new ArrayList<BorrowedPropertyVo>();
        for (BookProperty item : properties) {
            BorrowedPropertyVo borrowedPropertyVo = new BorrowedPropertyVo();
            borrowedPropertyVo.setPropertyId(item.getId());
            borrowedPropertyVo.setPropertyName(item.getTitle());
            borrowedPropertyVo.setBorrowedAmount(
                bookPropertyService.findBorrowedAmountByPropertyId(beforeDate, afterDate, item.getId()));
            result.add(borrowedPropertyVo);
        }
        return ReturnResultUtils.success(result);
    }

    @GetMapping("/period_borrowed_record_amount")
    @ApiOperation("(Librarian Only) report period borrowed record amount")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<PeriodBorrowedRecordVo>> reportPeriodBorrowedRecordAmount(
        @RequestParam(value = "beforeDate") @DateTimeFormat(pattern = DATE_PATTERN) Date startDate,
        @RequestParam(value = "afterDate") @DateTimeFormat(pattern = DATE_PATTERN) Date endDate) {
        if (startDate == null || endDate == null) {
            throw new RestServiceException("the date not null");
        }
        if (DateUtils.truncatedCompareTo(startDate, endDate, Calendar.DATE) > 0) {
            throw new RestServiceException("the beforeDate is greater than afterDate");
        }

        Integer daysBetween = DateFormatUtil.daysBetween(startDate, endDate) + 1;
        List<BorrowRecord> records = borrowRecordService.findDayBorrowRecordAmount(startDate, endDate);
        List<PeriodBorrowedRecordVo> result = new ArrayList<PeriodBorrowedRecordVo>();
        Set<String> dates = new TreeSet<String>();
        String pattern = null;

        try {
            if (daysBetween > 366) {
                dates = DateFormatUtil.getYearsBetween(startDate, endDate, YEAR_PATTERN);
                pattern = YEAR_PATTERN;
            } else if (31 < daysBetween && daysBetween <= 366) {
                dates = DateFormatUtil.getMonthsBetween(startDate, endDate, MONTH_PATTERN);
                pattern = MONTH_PATTERN;
            } else {
                dates = DateFormatUtil.getDaysBetween(startDate, endDate, DATE_PATTERN);
                pattern = DATE_PATTERN;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Iterator<String> iterator = dates.iterator();
        while (iterator.hasNext()) {
            PeriodBorrowedRecordVo periodBorrowedRecordVo = new PeriodBorrowedRecordVo();
            String sDate = iterator.next();
            Integer recordAmount = 0;
            for (BorrowRecord item : records) {
                if (!DateFormatUtils.format(item.getCheckoutDate(), pattern).equals(sDate)) {
                    continue;
                } else {
                    recordAmount++;
                }
            }
            periodBorrowedRecordVo.setRecordAmount(recordAmount);
            periodBorrowedRecordVo.setDate(sDate);
            result.add(periodBorrowedRecordVo);
        }
        return ReturnResultUtils.success(result);
    }

    @GetMapping("/employee_borrowed_record_amount")
    @ApiOperation("(Librarian Only) report employee borrowed record amount")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<EmployeeBorrowedRecordVo>> reportEmployeeBorrowedRecordAmount(
        @RequestParam(value = "beforeDate") @DateTimeFormat(pattern = DATE_PATTERN) Date startDate,
        @RequestParam(value = "afterDate") @DateTimeFormat(pattern = DATE_PATTERN) Date endDate) {
        if (startDate == null || endDate == null) {
            throw new RestServiceException("the date not null");
        }
        if (DateUtils.truncatedCompareTo(startDate, endDate, Calendar.DATE) > 0) {
            throw new RestServiceException("the beforeDate is greater than afterDate");
        }

        List<Employee> employees = employeeService.findEmployeeBorrowedRecordRank(startDate, endDate,
            DEFAULT_RESULT_SIZE);
        List<EmployeeBorrowedRecordVo> result = new ArrayList<EmployeeBorrowedRecordVo>();
        for (Employee item : employees) {
            Integer employeeId = item.getId();
            Integer borrowedAmount = employeeService.findEmployeeBorrowedRecordsByEmployeeId(employeeId);
            EmployeeBorrowedRecordVo employeeBorrowedRecordVo = new EmployeeBorrowedRecordVo();
            employeeBorrowedRecordVo.setBorrowedAmount(borrowedAmount);
            employeeBorrowedRecordVo.setEmployeeId(employeeId);
            employeeBorrowedRecordVo.setEmployeeName(item.getScreenName());
            result.add(employeeBorrowedRecordVo);
        }
        return ReturnResultUtils.success(result);
    }

    @GetMapping("/employee_overdue_record_amount")
    @ApiOperation("(Librarian Only) report employee overdue record amount")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<EmployeeOverdueRecordVo>> reportEmployeeOverdueRecordAmount(
        @RequestParam(value = "beforeDate") @DateTimeFormat(pattern = DATE_PATTERN) Date startDate,
        @RequestParam(value = "afterDate") @DateTimeFormat(pattern = DATE_PATTERN) Date endDate) {
        if (startDate == null || endDate == null) {
            throw new RestServiceException("the date not null");
        }
        if (DateUtils.truncatedCompareTo(startDate, endDate, Calendar.DATE) > 0) {
            throw new RestServiceException("the beforeDate is greater than afterDate");
        }

        List<Employee> employees = employeeService.findEmployeeOverdueRecordRank(startDate, endDate,
            DEFAULT_RESULT_SIZE);
        List<EmployeeOverdueRecordVo> result = new ArrayList<EmployeeOverdueRecordVo>();
        for (Employee item : employees) {
            Integer employeeId = item.getId();
            Integer overdueAmount = employeeService.findEmployeeOverdueRecordsByEmployeeId(employeeId);
            EmployeeOverdueRecordVo employeeOverdueRecordVo = new EmployeeOverdueRecordVo();
            employeeOverdueRecordVo.setOverdueAmount(overdueAmount);
            employeeOverdueRecordVo.setEmployeeId(employeeId);
            employeeOverdueRecordVo.setEmployeeName(item.getScreenName());
            result.add(employeeOverdueRecordVo);
        }
        return ReturnResultUtils.success(result);
    }
}
