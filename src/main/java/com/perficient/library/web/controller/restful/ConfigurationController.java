package com.perficient.library.web.controller.restful;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.ErrorConvertUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.model.Configuration;
import com.perficient.library.core.service.ConfigurationService;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only Librarian can do operations for configuration.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/conf")
@Api("configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping
    @ApiOperation("(Librarian Only) get configuration")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Configuration> getConfiguration() {
        return ReturnResultUtils.success(configurationService.get());
    }

    @PutMapping("/{configurationId}")
    @ApiOperation("(Librarian Only) update configuration")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Configuration> updateConfiguration(@PathVariable("configurationId") Integer configurationId,
        @Valid @RequestBody Configuration config, BindingResult result) {
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorConvertUtils.convertToString(result.getAllErrors()));
        }

        if (configurationId == null || (configurationService.get()) == null) {
            throw new RestServiceException("the configuration is not exist");
        }

        config.setId(configurationId);
        return ReturnResultUtils.success(configurationService.save(config));
    }
}
