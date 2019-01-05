package com.perficient.library.web.controller.restful;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.BookProperty;
import com.perficient.library.core.model.Category;
import com.perficient.library.core.service.BookPropertyService;
import com.perficient.library.core.service.CategoryService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * only Librarian can add/update/delete categories. only Librarian can add/remove book properties to a category.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/category")
@Api("category")
@Transactional
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookPropertyService bookPropertyService;

    @PostMapping()
    @ApiOperation("(Librarian Only) add a new category")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Category> addBookCategory(@Valid @RequestBody Category category, BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        if (categoryService.findByCategoryName(category.getName()) != null) {
            throw new RestServiceException("the category name already exist");
        }

        category.setId(null);
        category = categoryService.save(category);

        return ReturnResultUtils.success("save succeeded", category);
    }

    @PutMapping("/{categoryId}")
    @ApiOperation("(Librarian Only) update a category by category id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Category> updateBookCategory(@PathVariable("categoryId") Integer categoryId,
        @Valid @RequestBody Category updatedCategory, BindingResult result) {

        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        Category dbBookCategory = null;
        if (categoryId == null || (dbBookCategory = categoryService.findOne(categoryId)) == null) {
            throw new RestServiceException("the category is not exist");
        }

        String updatedCategoryName = updatedCategory.getName();
        String dbCategoryName = dbBookCategory.getName();
        Boolean categoryNameChanged = !dbCategoryName.equals(updatedCategoryName);
        if (categoryNameChanged && categoryService.findByCategoryName(updatedCategoryName) != null) {
            // updated category name already exist in Database
            throw new RestServiceException("the category name already exist");
        }

        dbBookCategory.setName(updatedCategoryName);
        dbBookCategory.setDescription(updatedCategory.getDescription());
        dbBookCategory = categoryService.save(dbBookCategory);

        return ReturnResultUtils.success("update succeeded", dbBookCategory);
    }

    @GetMapping
    @ApiOperation("get all categories")
    public ReturnResult<List<Category>> findBookCategories(@RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<Category> categorys = categoryService.findAll(pageable);
        return ReturnResultUtils.successPaged(categorys.getContent(), categorys.getTotalElements());
    }

    @GetMapping("/{categoryId}")
    @ApiOperation("get a category by category id")
    public ReturnResult<Category> findBookCategoryById(@PathVariable("categoryId") Integer categoryId) {

        return ReturnResultUtils.success(categoryService.findOne(categoryId));
    }

    @DeleteMapping("/{categoryId}")
    @ApiOperation("(Librarian Only) delete a category by category id")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> deleteBookCategory(@PathVariable("categoryId") Integer categoryId) {

        if ((categoryService.findOne(categoryId)) == null) {
            throw new RestServiceException("the category not exist");
        }

        List<BookProperty> bookProperties = bookPropertyService.findByCategory(categoryId);
        if (bookProperties != null && !bookProperties.isEmpty()) {
            throw new RestServiceException("the associated book properties are not empty");
        }

        categoryService.delete(categoryId);
        return ReturnResultUtils.success("delete succeeded", null);
    }

    @PostMapping("/property")
    @ApiOperation("(Librarian Only) add properties to categories")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> addBookPropertyForCategory(@RequestParam(value = "categoryIds") String[] categoryIds,
        @RequestParam(value = "propertyIds") String[] propertyIds) {

        List<Integer> ids = new ArrayList<Integer>();
        for (String item : categoryIds) {
            Integer categoryId = null;
            try {
                categoryId = Integer.parseInt(item);
                ids.add(categoryId);
            } catch (Exception e) {
                throw new RestServiceException("the category is not exist");
            }
            if ((categoryService.findOne(categoryId)) == null) {
                throw new RestServiceException("the category is not exist");
            }
        }

        for (String item : propertyIds) {
            Integer propertyId = null;
            try {
                propertyId = Integer.parseInt(item);
            } catch (Exception e) {
                throw new RestServiceException("the property is not exist");
            }

            BookProperty propertyDb = null;
            if (propertyId == null || (propertyDb = bookPropertyService.findOne(propertyId)) == null) {
                throw new RestServiceException("the property is not exist");
            }

            List<Integer> propertyCategoryDb = propertyDb.getCategory();
            if (propertyCategoryDb == null) {
                // category in property default value is null
                propertyCategoryDb = new ArrayList<Integer>();
            }

            for (Integer id : ids) {
                if (!propertyCategoryDb.isEmpty() && propertyCategoryDb.contains(id)) {
                    continue;
                }
                propertyCategoryDb.add(id);
                propertyDb.setCategory(propertyCategoryDb);
                bookPropertyService.save(propertyDb);
            }
        }
        return ReturnResultUtils.success("add properties to categories succeeded");
    }

    @DeleteMapping("/{categoryId}/property")
    @ApiOperation("(Librarian Only) delete properties from categories")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> deleteBookPropertyForCategory(@PathVariable("categoryId") Integer categoryId,
        @RequestParam(value = "propertyIds") String[] propertyIds) {

        if ((categoryService.findOne(categoryId)) == null) {
            throw new RestServiceException("the category is not exist");
        }

        for (String item : propertyIds) {
            Integer propertyId = null;
            try {
                propertyId = Integer.parseInt(item);
            } catch (Exception e) {
                throw new RestServiceException("the property is not exist");
            }

            BookProperty propertyDb = null;
            if (propertyId == null || (propertyDb = bookPropertyService.findOne(propertyId)) == null) {
                throw new RestServiceException("the property is not exist");
            }

            List<Integer> propertyCategoryDb = propertyDb.getCategory();
            if (propertyCategoryDb == null || !propertyCategoryDb.contains(categoryId)) {
                continue;
            }

            Iterator<Integer> iterator = propertyCategoryDb.iterator();
            while (iterator.hasNext()) {
                Integer interger = iterator.next();
                if (interger == categoryId) {
                    iterator.remove();
                }
            }
            propertyDb.setCategory(propertyCategoryDb);
            propertyDb = bookPropertyService.save(propertyDb);

        }
        return ReturnResultUtils.success("delete properties from categories succeeded");
    }

}
