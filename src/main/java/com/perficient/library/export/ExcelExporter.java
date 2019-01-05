package com.perficient.library.export;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

public abstract class ExcelExporter extends DataExporter {

    public static final String XSL_SUFFIX = ".xls";

    private InputStream inputStream;

    private String importFilePath;

    public ExcelExporter() {

    }

    public ExcelExporter(HttpServletResponse response, String exportFileName, String importFilePath) {
        super(exportFileName + XSL_SUFFIX, response);
        this.importFilePath = importFilePath;
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public InputStream getInput() {
        return this.inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getImportFilePath() {
        return importFilePath;
    }

    public void setImportFilePath(String importFilePath) {
        this.importFilePath = importFilePath;
    }

}
