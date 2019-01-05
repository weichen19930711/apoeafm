package com.perficient.library.web.controller.restful;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only librarian can upload files.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1")
@Api("file_upload")
public class FileUploadController {

    @Value("${library.upload.visit-url}")
    private String visitUrl;

    @Value("${library.upload.upload-folder}")
    private String uploadPath;

    @Value("${library.upload.max-file-size}")
    private Long maxFileSize;

    @PostMapping("/upload")
    @ApiOperation("(Librarian Only) upload a file")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<String> upload(@RequestParam("file") MultipartFile file) {

        if (file == null || StringUtils.isBlank(file.getOriginalFilename()) || file.getSize() == 0) {
            throw new RestServiceException("uploaded file is empty");
        }

        long fileSize = file.getSize();
        if (fileSize > maxFileSize) {
            throw new RestServiceException("file size cannot more than " + maxFileSize + "bytes");
        }

        try {
            String randomName = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String newFileName = null;
            if (StringUtils.isEmpty(fileExtension)) {
                newFileName = randomName;
            } else {
                newFileName = randomName + FilenameUtils.EXTENSION_SEPARATOR + fileExtension;
            }

            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File newFile = new File(uploadFolder + File.separator + newFileName);
            newFile.createNewFile();
            file.transferTo(newFile);

            return ReturnResultUtils.success(visitUrl + newFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
